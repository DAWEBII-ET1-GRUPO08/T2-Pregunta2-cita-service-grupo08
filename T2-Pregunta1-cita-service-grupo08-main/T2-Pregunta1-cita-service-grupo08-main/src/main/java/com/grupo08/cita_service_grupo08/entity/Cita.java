package com.grupo08.cita_service_grupo08.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "citas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String codigoAtencion;

    @Column(nullable = false)
    private LocalDateTime fechaCita;

    // Referencia al paciente de paciente-service (no es FK, es otro microservicio)
    @Column(nullable = false)
    private Long pacienteId;

    // Edad del paciente registrada al momento de la cita
    private Integer edadPaciente;

    @Column(nullable = false, length = 100)
    private String medico;

    @Column(nullable = false, length = 80)
    private String especialidad;

    // PROGRAMADA, COMPLETADA, CANCELADA
    @Column(nullable = false, length = 20)
    private String estadoCita;

    // Monto base de la consulta (copago base antes de aplicar el seguro)
    @Column(nullable = false)
    private Double montoConsulta;

    // Copago final que paga el paciente después de aplicar el seguro
    private Double copagoAplicado;

    @OneToMany(mappedBy = "cita", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DiagnosticoCita> diagnosticos = new ArrayList<>();

    public void agregarDiagnostico(DiagnosticoCita diagnostico) {
        diagnostico.setCita(this);
        this.diagnosticos.add(diagnostico);
    }
}
