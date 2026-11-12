package com.empresa.reembolsos;

import com.empresa.reembolsos.aplicacion.SolicitudReembolsoService;
import com.empresa.reembolsos.aplicacion.SolicitudInvalidaException;
import com.empresa.reembolsos.dominio.*;
import com.empresa.reembolsos.infraestructura.RepositorioSolicitudesJpa;

import com.empresa.reembolsos.infraestructura.SolicitudReembolsoJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SolicitudReembolsoServiceIntegrationTest {

    @Autowired
    private SolicitudReembolsoService servicio;

    @Autowired
    private RepositorioSolicitudesJpa repositorio;

    @Autowired
    private SolicitudReembolsoJpaRepository repositorioJpa;

    @BeforeEach
    void limpiarBaseDeDatos() {
        repositorio.deleteAll();
    }

    @Test
    void crearSolicitud_procesaCorrectamenteLasTresCategorias() {

        var viaje = crearSolicitud(
                "EMP-01",
                CategoriaGasto.VIAJE,
                "300000",
                2
        );

        var representacion = crearSolicitud(
                "EMP-01",
                CategoriaGasto.REPRESENTACION,
                "1800000",
                0
        );

        var capacitacion = crearSolicitud(
                "EMP-01",
                CategoriaGasto.CAPACITACION,
                "1000000",
                0
        );

        var viajeGuardado = servicio.crear(viaje);
        var representacionGuardada = servicio.crear(representacion);
        var capacitacionGuardada = servicio.crear(capacitacion);

        // Comprueba que fueron persistidas.
        assertNotNull(viajeGuardado.getId());
        assertNotNull(representacionGuardada.getId());
        assertNotNull(capacitacionGuardada.getId());

        // Comprueba los cálculos.
        assertEquals(
                0,
                new BigDecimal("300000").compareTo(
                        viajeGuardado.getMontoAprobado())
        );

        assertEquals(
                0,
                new BigDecimal("1800000").compareTo(
                        representacionGuardada.getMontoAprobado())
        );

        assertEquals(
                0,
                new BigDecimal("800000").compareTo(
                        capacitacionGuardada.getMontoAprobado())
        );

        // Comprueba el estado inicial.
        assertEquals(
                EstadoSolicitud.PENDIENTE,
                viajeGuardado.getEstado()
        );

        assertEquals(
                EstadoSolicitud.PENDIENTE,
                representacionGuardada.getEstado()
        );

        assertEquals(
                EstadoSolicitud.PENDIENTE,
                capacitacionGuardada.getEstado()
        );
    }

    @Test
    void crearSolicitud_rechazaDuplicadoDeMismaCategoriaYPeriodo() {

        var primeraSolicitud = crearSolicitud(
                "EMP-01",
                CategoriaGasto.VIAJE,
                "100000",
                1
        );

        var duplicada = crearSolicitud(
                "EMP-01",
                CategoriaGasto.VIAJE,
                "120000",
                1
        );

        servicio.crear(primeraSolicitud);

        assertThrows(
                SolicitudInvalidaException.class,
                () -> servicio.crear(duplicada)
        );

        assertEquals(1, repositorioJpa.count());
    }

    @Test
    void cambiarEstado_persisteElNuevoEstadoDeLaSolicitud() {

        var solicitud = crearSolicitud(
                "EMP-02",
                CategoriaGasto.REPRESENTACION,
                "1000000",
                0
        );

        var creada = servicio.crear(solicitud);

        var actualizada = servicio.cambiarEstado(
                creada.getId(),
                EstadoSolicitud.APROBADA
        );

        assertEquals(
                EstadoSolicitud.APROBADA,
                actualizada.getEstado()
        );

        // Comprueba el estado directamente en la persistencia.
        var entidadPersistida = repositorio.findById(creada.getId())
                .orElseThrow();

        assertEquals(
                EstadoSolicitud.APROBADA,
                entidadPersistida.getEstado()
        );
    }

    private SolicitudReembolso crearSolicitud(
            String empleadoId,
            CategoriaGasto categoria,
            String monto,
            int diasViaje) {

        var solicitud = new SolicitudReembolso();

        solicitud.setEmpleadoId(empleadoId);
        solicitud.setCategoria(categoria);
        solicitud.setMontoSolicitado(new BigDecimal(monto));
        solicitud.setFechaGasto(LocalDate.of(2026, 11, 12));
        solicitud.setDiasViaje(diasViaje);
        solicitud.setDocumentosSoporte(true);

        return solicitud;
    }
}