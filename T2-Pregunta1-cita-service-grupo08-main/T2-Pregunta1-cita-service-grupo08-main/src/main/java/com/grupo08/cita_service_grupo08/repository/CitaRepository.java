package com.grupo08.cita_service_grupo08.repository;

import com.grupo08.cita_service_grupo08.entity.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findByPacienteIdOrderByFechaCitaDesc(Long pacienteId);
}
