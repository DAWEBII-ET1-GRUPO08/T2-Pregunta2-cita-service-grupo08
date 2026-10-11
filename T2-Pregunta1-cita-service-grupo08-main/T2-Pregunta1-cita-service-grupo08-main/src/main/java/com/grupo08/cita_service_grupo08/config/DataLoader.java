package com.grupo08.cita_service_grupo08.config;

import com.grupo08.cita_service_grupo08.entity.Cita;
import com.grupo08.cita_service_grupo08.entity.DiagnosticoCita;
import com.grupo08.cita_service_grupo08.repository.CitaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final CitaRepository citaRepository;

    @Override
    public void run(String... args) {
        if (citaRepository.count() > 0) {
            return;
        }

        // Cita 1 - Paciente 1 (EPS) - PROGRAMADA
        Cita c1 = cita("ATN-2026-0001", LocalDateTime.of(2026, 10, 12, 9, 0), 1L, 41,
                "Dr. Luis Mendoza Vargas", "Medicina General", "PROGRAMADA", 150.0, null);
        c1.agregarDiagnostico(diagnostico("J06.9", "Infección aguda de las vías respiratorias superiores",
                LocalDateTime.of(2026, 10, 12, 9, 30), "LEVE"));

        // Cita 2 - Paciente 1 (EPS) - COMPLETADA (copago EPS ya aplicado: 200 x 0.20 = 40)
        Cita c2 = cita("ATN-2026-0002", LocalDateTime.of(2026, 9, 5, 10, 30), 1L, 41,
                "Dra. Ana Torres Salazar", "Cardiología", "COMPLETADA", 200.0, 40.0);
        c2.agregarDiagnostico(diagnostico("I10", "Hipertensión esencial (primaria)",
                LocalDateTime.of(2026, 9, 5, 11, 0), "MODERADO"));

        // Cita 3 - Paciente 2 (ESSALUD) - PROGRAMADA
        Cita c3 = cita("ATN-2026-0003", LocalDateTime.of(2026, 10, 14, 15, 0), 2L, 63,
                "Dr. Ricardo Flores Paredes", "Endocrinología", "PROGRAMADA", 180.0, null);
        c3.agregarDiagnostico(diagnostico("E11.9", "Diabetes mellitus tipo 2 sin complicaciones",
                LocalDateTime.of(2026, 10, 14, 15, 30), "SEVERO"));

        // Cita 4 - Paciente 3 (PARTICULAR) - PROGRAMADA
        Cita c4 = cita("ATN-2026-0004", LocalDateTime.of(2026, 10, 15, 8, 0), 3L, 28,
                "Dra. Patricia Rojas León", "Traumatología", "PROGRAMADA", 120.0, null);
        c4.agregarDiagnostico(diagnostico("M54.5", "Lumbago no especificado",
                LocalDateTime.of(2026, 10, 15, 8, 20), "LEVE"));

        // Cita 5 - Paciente 3 (PARTICULAR) - CANCELADA
        Cita c5 = cita("ATN-2026-0005", LocalDateTime.of(2026, 10, 1, 11, 0), 3L, 28,
                "Dr. Jorge Castillo Ruiz", "Dermatología", "CANCELADA", 100.0, null);

        citaRepository.save(c1);
        citaRepository.save(c2);
        citaRepository.save(c3);
        citaRepository.save(c4);
        citaRepository.save(c5);
    }

    private Cita cita(String codigo, LocalDateTime fecha, Long pacienteId, Integer edad,
                      String medico, String especialidad, String estado,
                      Double monto, Double copago) {
        return Cita.builder()
                .codigoAtencion(codigo)
                .fechaCita(fecha)
                .pacienteId(pacienteId)
                .edadPaciente(edad)
                .medico(medico)
                .especialidad(especialidad)
                .estadoCita(estado)
                .montoConsulta(monto)
                .copagoAplicado(copago)
                .build();
    }

    private DiagnosticoCita diagnostico(String codigo, String descripcion,
                                        LocalDateTime fecha, String gravedad) {
        return DiagnosticoCita.builder()
                .codigoCIE10(codigo)
                .descripcion(descripcion)
                .fechaDiagnostico(fecha)
                .gravedad(gravedad)
                .build();
    }
}