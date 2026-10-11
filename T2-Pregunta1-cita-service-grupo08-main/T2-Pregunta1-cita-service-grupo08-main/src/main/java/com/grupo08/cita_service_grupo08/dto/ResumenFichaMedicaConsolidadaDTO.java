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
public class ResumenFichaMedicaConsolidadaDTO {
    private Long pacienteId;
    private String nombrePaciente;
    private Integer edad;
    private String tipoSeguro;
    private Integer totalCitasAtendidas;
    private Double montoTotalGasto;
    private List<AtencionMedicaResponseDTO> historialAtenciones;
    private List<DiagnosticoDTO> diagnosticosConsolidados;
    private Boolean requiereAtencionEspecializada;
    private String clasificacionRiesgo;
}