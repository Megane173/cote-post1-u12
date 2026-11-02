
package com.empresa.reembolsos.infraestructura;

import com.empresa.reembolsos.dominio.EstadoSolicitud;
import com.empresa.reembolsos.dominio.SolicitudReembolso;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RepositorioSolicitudesJpa {

    private final SolicitudReembolsoJpaRepository repositorio;

    public RepositorioSolicitudesJpa(
            SolicitudReembolsoJpaRepository repositorio) {
        this.repositorio = repositorio;
    }

    public SolicitudReembolso save(SolicitudReembolso solicitud) {
        var entidad = convertirAEntidad(solicitud);
        var guardada = repositorio.save(entidad);

        return convertirADominio(guardada);
    }

    public Optional<SolicitudReembolso> findById(Long id) {
        return repositorio.findById(id)
                .map(this::convertirADominio);
    }

    public List<SolicitudReembolso> findByEmpleadoIdAndEstado(
            String empleadoId,
            EstadoSolicitud estado) {

        return repositorio.findByEmpleadoIdAndEstado(empleadoId, estado)
                .stream()
                .map(this::convertirADominio)
                .toList();
    }

    public void deleteAll() {
        repositorio.deleteAll();
    }

    private SolicitudReembolsoEntity convertirAEntidad(
            SolicitudReembolso solicitud) {

        var entidad = new SolicitudReembolsoEntity();

        entidad.setId(solicitud.getId());
        entidad.setEmpleadoId(solicitud.getEmpleadoId());
        entidad.setCategoria(solicitud.getCategoria());
        entidad.setMontoSolicitado(solicitud.getMontoSolicitado());
        entidad.setMontoAprobado(solicitud.getMontoAprobado());
        entidad.setFechaGasto(solicitud.getFechaGasto());
        entidad.setDiasViaje(solicitud.getDiasViaje());
        entidad.setDocumentosSoporte(solicitud.isDocumentosSoporte());
        entidad.setEstado(solicitud.getEstado());

        return entidad;
    }

    private SolicitudReembolso convertirADominio(
            SolicitudReembolsoEntity entidad) {

        var solicitud = new SolicitudReembolso();

        solicitud.setId(entidad.getId());
        solicitud.setEmpleadoId(entidad.getEmpleadoId());
        solicitud.setCategoria(entidad.getCategoria());
        solicitud.setMontoSolicitado(entidad.getMontoSolicitado());
        solicitud.setMontoAprobado(entidad.getMontoAprobado());
        solicitud.setFechaGasto(entidad.getFechaGasto());
        solicitud.setDiasViaje(entidad.getDiasViaje());
        solicitud.setDocumentosSoporte(entidad.isDocumentosSoporte());
        solicitud.setEstado(entidad.getEstado());

        return solicitud;
    }
}
