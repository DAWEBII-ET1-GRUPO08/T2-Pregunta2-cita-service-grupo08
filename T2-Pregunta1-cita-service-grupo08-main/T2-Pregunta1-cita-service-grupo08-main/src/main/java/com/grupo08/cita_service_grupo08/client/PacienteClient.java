package com.grupo08.cita_service_grupo08.client;

import com.grupo08.cita_service_grupo08.dto.DiagnosticoDTO;
import com.grupo08.cita_service_grupo08.dto.PacienteHistorialDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "paciente-service-grupo08", path = "/api/v1/pacientes")
public interface PacienteClient {

    // GET /api/v1/pacientes/{pacienteId}
    @GetMapping("/{pacienteId}")
    PacienteHistorialDTO obtenerPaciente(@PathVariable("pacienteId") Long pacienteId);

    // GET /api/v1/pacientes/{pacienteId}/diagnosticos
    @GetMapping("/{pacienteId}/diagnosticos")
    List<DiagnosticoDTO> obtenerDiagnosticos(@PathVariable("pacienteId") Long pacienteId);
}