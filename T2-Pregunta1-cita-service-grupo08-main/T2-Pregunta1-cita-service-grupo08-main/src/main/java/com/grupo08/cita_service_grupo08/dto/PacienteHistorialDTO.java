package com.grupo08.cita_service_grupo08.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PacienteHistorialDTO {
    private Long pacienteId;
    private String numeroDocumento;
    private String nombreCompleto;
    private String tipoSeguro;
    private List<DiagnosticoDTO> listaDiagnosticosActivos;
    private String estadoPaciente;
}
