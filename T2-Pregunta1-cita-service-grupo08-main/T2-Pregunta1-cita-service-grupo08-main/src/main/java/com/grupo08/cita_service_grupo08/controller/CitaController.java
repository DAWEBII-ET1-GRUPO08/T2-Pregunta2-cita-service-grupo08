package com.grupo08.cita_service_grupo08.controller;

import com.grupo08.cita_service_grupo08.dto.AtencionMedicaResponseDTO;
import com.grupo08.cita_service_grupo08.dto.ResumenFichaMedicaConsolidadaDTO;
import com.grupo08.cita_service_grupo08.service.CitaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/citas")
@RequiredArgsConstructor
public class CitaController {

    private final CitaService citaService;

    // GET /api/v1/citas/{idCita}
    @GetMapping("/{idCita}")
    public ResponseEntity<AtencionMedicaResponseDTO> obtenerCita(@PathVariable("idCita") Long idCita) {
        return ResponseEntity.ok(citaService.obtenerCita(idCita));
    }

    // GET /api/v1/citas/evaluar-y-completar/{idCita}
    @GetMapping("/evaluar-y-completar/{idCita}")
    public ResponseEntity<AtencionMedicaResponseDTO> evaluarYCompletar(@PathVariable("idCita") Long idCita) {
        return ResponseEntity.ok(citaService.evaluarYCompletar(idCita));
    }

    // GET /api/v1/citas/ficha-medica/{pacienteId}
    @GetMapping("/ficha-medica/{pacienteId}")
    public ResponseEntity<ResumenFichaMedicaConsolidadaDTO> fichaMedica(@PathVariable("pacienteId") Long pacienteId) {
        return ResponseEntity.ok(citaService.generarFichaMedica(pacienteId));
    }
}
