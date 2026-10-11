package com.grupo08.cita_service_grupo08.service;

import com.grupo08.cita_service_grupo08.client.PacienteClient;
import com.grupo08.cita_service_grupo08.dto.AtencionMedicaResponseDTO;
import com.grupo08.cita_service_grupo08.dto.DiagnosticoDTO;
import com.grupo08.cita_service_grupo08.dto.PacienteHistorialDTO;
import com.grupo08.cita_service_grupo08.dto.ResumenFichaMedicaConsolidadaDTO;
import com.grupo08.cita_service_grupo08.entity.Cita;
import com.grupo08.cita_service_grupo08.entity.DiagnosticoCita;
import com.grupo08.cita_service_grupo08.repository.CitaRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CitaService {

    private static final String ESTADO_COMPLETADA = "COMPLETADA";
    private static final String ESTADO_CANCELADA = "CANCELADA";
    private static final String GRAVEDAD_SEVERO = "SEVERO";

    private final CitaRepository citaRepository;
    private final PacienteClient pacienteClient;

    // ================================================================
    // GET /api/v1/citas/{idCita}
    // ================================================================
    @Transactional(readOnly = true)
    public AtencionMedicaResponseDTO obtenerCita(Long idCita) {
        return toAtencionDTO(buscarCita(idCita));
    }

    // ================================================================
    // GET /api/v1/citas/evaluar-y-completar/{idCita}
    // ================================================================
    @Transactional
    public AtencionMedicaResponseDTO evaluarYCompletar(Long idCita) {
        Cita cita = buscarCita(idCita);

        if (ESTADO_CANCELADA.equalsIgnoreCase(cita.getEstadoCita())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La cita " + idCita + " está CANCELADA y no puede completarse");
        }

        // 1. Consultar tipoSeguro en paciente-service vía Feign
        PacienteHistorialDTO paciente = consultarPaciente(cita.getPacienteId());

        // 2. Aplicar cobertura del seguro
        double copago = calcularCopago(cita.getMontoConsulta(), paciente.getTipoSeguro());

        // 3. Actualizar estado y copago
        cita.setCopagoAplicado(copago);
        cita.setEstadoCita(ESTADO_COMPLETADA);

        return toAtencionDTO(citaRepository.save(cita));
    }

    // ================================================================
    // GET /api/v1/citas/ficha-medica/{pacienteId}
    // ================================================================
    @Transactional(readOnly = true)
    public ResumenFichaMedicaConsolidadaDTO generarFichaMedica(Long pacienteId) {

        // 1. Datos del paciente e historial clínico desde paciente-service (Feign)
        PacienteHistorialDTO paciente = consultarPaciente(pacienteId);
        List<DiagnosticoDTO> historialClinico = consultarDiagnosticos(pacienteId);

        // 2. Atenciones locales del paciente
        List<Cita> citas = citaRepository.findByPacienteIdOrderByFechaCitaDesc(pacienteId);

        List<AtencionMedicaResponseDTO> historialAtenciones = citas.stream()
                .map(this::toAtencionDTO)
                .toList();

        List<Cita> citasAtendidas = citas.stream()
                .filter(c -> ESTADO_COMPLETADA.equalsIgnoreCase(c.getEstadoCita()))
                .toList();

        double montoTotal = citasAtendidas.stream()
                .map(Cita::getCopagoAplicado)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .sum();

        // Edad registrada en la cita más reciente
        Integer edad = citas.stream()
                .map(Cita::getEdadPaciente)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        // 3. Consolidar diagnósticos: historial clínico + diagnósticos de las citas (sin repetir código CIE-10)
        List<DiagnosticoDTO> diagnosticosConsolidados = new ArrayList<>(historialClinico);
        Set<String> codigosExistentes = historialClinico.stream()
                .map(DiagnosticoDTO::getCodigoCIE10)
                .collect(Collectors.toSet());

        citas.stream()
                .flatMap(c -> c.getDiagnosticos().stream())
                .filter(d -> codigosExistentes.add(d.getCodigoCIE10()))
                .map(this::toDiagnosticoDTO)
                .forEach(diagnosticosConsolidados::add);

        // 4. Evaluación de riesgo
        boolean requiereEspecializada = diagnosticosConsolidados.stream()
                .anyMatch(d -> GRAVEDAD_SEVERO.equalsIgnoreCase(d.getGravedad()));

        return ResumenFichaMedicaConsolidadaDTO.builder()
                .pacienteId(paciente.getPacienteId())
                .nombrePaciente(paciente.getNombreCompleto())
                .edad(edad)
                .tipoSeguro(paciente.getTipoSeguro())
                .totalCitasAtendidas(citasAtendidas.size())
                .montoTotalGasto(redondear(montoTotal))
                .historialAtenciones(historialAtenciones)
                .diagnosticosConsolidados(diagnosticosConsolidados)
                .requiereAtencionEspecializada(requiereEspecializada)
                .clasificacionRiesgo(requiereEspecializada ? "ALTO_RIESGO" : "BAJO_RIESGO")
                .build();
    }

    // ================================================================
    // Reglas de cobertura
    // EPS: 80% descuento | ESSALUD: copago 0 | PARTICULAR: 0% descuento
    // ================================================================
    private double calcularCopago(Double copagoBase, String tipoSeguro) {
        double base = copagoBase != null ? copagoBase : 0.0;
        String seguro = tipoSeguro != null ? tipoSeguro.trim().toUpperCase() : "";

        double copago = switch (seguro) {
            case "EPS" -> base * 0.20;
            case "ESSALUD" -> 0.0;
            case "PARTICULAR" -> base;
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Tipo de seguro no reconocido: " + tipoSeguro);
        };
        return redondear(copago);
    }

    // ================================================================
    // Llamadas Feign con manejo de errores
    // ================================================================
    private PacienteHistorialDTO consultarPaciente(Long pacienteId) {
        try {
            return pacienteClient.obtenerPaciente(pacienteId);
        } catch (FeignException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Paciente no encontrado en paciente-service con id: " + pacienteId);
        } catch (FeignException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo comunicar con paciente-service: " + e.getMessage());
        }
    }

    private List<DiagnosticoDTO> consultarDiagnosticos(Long pacienteId) {
        try {
            List<DiagnosticoDTO> lista = pacienteClient.obtenerDiagnosticos(pacienteId);
            return lista != null ? lista : List.of();
        } catch (FeignException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Paciente no encontrado en paciente-service con id: " + pacienteId);
        } catch (FeignException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo comunicar con paciente-service: " + e.getMessage());
        }
    }

    // ================================================================
    // Utilidades y mapeo
    // ================================================================
    private Cita buscarCita(Long idCita) {
        return citaRepository.findById(idCita)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cita no encontrada con id: " + idCita));
    }

    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    private AtencionMedicaResponseDTO toAtencionDTO(Cita c) {
        return AtencionMedicaResponseDTO.builder()
                .idCita(c.getId())
                .codigoAtencion(c.getCodigoAtencion())
                .fechaCita(c.getFechaCita())
                .pacienteId(c.getPacienteId())
                .medico(c.getMedico())
                .especialidad(c.getEspecialidad())
                .estadoCita(c.getEstadoCita())
                .listaDiagnosticos(c.getDiagnosticos().stream().map(this::toDiagnosticoDTO).toList())
                .montoConsultaCalculado(c.getMontoConsulta())
                .copagoAplicado(c.getCopagoAplicado())
                .build();
    }

    private DiagnosticoDTO toDiagnosticoDTO(DiagnosticoCita d) {
        return DiagnosticoDTO.builder()
                .idDiagnostico(d.getId())
                .codigoCIE10(d.getCodigoCIE10())
                .descripcion(d.getDescripcion())
                .fechaDiagnostico(d.getFechaDiagnostico())
                .gravedad(d.getGravedad())
                .build();
    }
}