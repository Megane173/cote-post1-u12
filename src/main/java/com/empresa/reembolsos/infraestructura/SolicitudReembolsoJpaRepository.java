
package com.empresa.reembolsos.infraestructura;

import com.empresa.reembolsos.dominio.EstadoSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolicitudReembolsoJpaRepository
        extends JpaRepository<SolicitudReembolsoEntity, Long> {

    List<SolicitudReembolsoEntity> findByEmpleadoIdAndEstado(
            String empleadoId,
            EstadoSolicitud estado
    );
}
