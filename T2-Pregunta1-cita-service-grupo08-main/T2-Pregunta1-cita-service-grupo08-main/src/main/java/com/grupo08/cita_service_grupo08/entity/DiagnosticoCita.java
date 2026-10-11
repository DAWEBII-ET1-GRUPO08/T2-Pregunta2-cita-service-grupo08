package com.grupo08.cita_service_grupo08.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "diagnosticos_cita")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiagnosticoCita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String codigoCIE10;

    @Column(nullable = false, length = 255)
    private String descripcion;

    @Column(nullable = false)
    private LocalDateTime fechaDiagnostico;

    // LEVE, MODERADO, SEVERO
    @Column(nullable = false, length = 20)
    private String gravedad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cita_id", nullable = false)
    private Cita cita;
}
