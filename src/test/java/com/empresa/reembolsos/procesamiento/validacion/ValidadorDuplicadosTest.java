package com.empresa.reembolsos.procesamiento.validacion;

import com.empresa.reembolsos.dominio.CategoriaGasto;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static com.empresa.reembolsos.procesamiento.SolicitudReembolsoTestData.crearSolicitud;

class ValidadorDuplicadosTest {

    private final ValidadorDuplicados validador =
            new ValidadorDuplicados();

    @Test
    void validar_rechazaSolicitudesDeMismoEmpleadoCategoriaYPeriodo() {

        var solicitud = crearSolicitud(
                CategoriaGasto.VIAJE,
                "100000",
                1,
                LocalDate.of(2026, 11, 20),
                true,
                "EMP-01"
        );

        var duplicada = crearSolicitud(
                CategoriaGasto.VIAJE,
                "120000",
                1,
                LocalDate.of(2026, 11, 10),
                true,
                "EMP-01"
        );

        var resultado = validador.validar(
                solicitud,
                List.of(duplicada)
        );

        assertFalse(resultado.valida());
    }

    @Test
    void validar_permiteMismoMesDeUnAnioDiferente() {

        var solicitud = crearSolicitud(
                CategoriaGasto.VIAJE,
                "100000",
                1,
                LocalDate.of(2026, 11, 20),
                true,
                "EMP-01"
        );

        var solicitudAnterior = crearSolicitud(
                CategoriaGasto.VIAJE,
                "120000",
                1,
                LocalDate.of(2025, 11, 10),
                true,
                "EMP-01"
        );

        var resultado = validador.validar(
                solicitud,
                List.of(solicitudAnterior)
        );

        assertTrue(resultado.valida());
    }

    @Test
    void validar_permiteSolicitudesDeEmpleadosDiferentes() {

        var solicitud = crearSolicitud(
                CategoriaGasto.VIAJE,
                "100000",
                1,
                LocalDate.of(2026, 11, 20),
                true,
                "EMP-01"
        );

        var otraSolicitud = crearSolicitud(
                CategoriaGasto.VIAJE,
                "120000",
                1,
                LocalDate.of(2026, 11, 10),
                true,
                "EMP-02"
        );

        var resultado = validador.validar(
                solicitud,
                List.of(otraSolicitud)
        );

        assertTrue(resultado.valida());
    }

    @Test
    void validar_permiteCategoriasDiferentesEnElMismoPeriodo() {

        var solicitud = crearSolicitud(
                CategoriaGasto.VIAJE,
                "100000",
                1,
                LocalDate.of(2026, 11, 20),
                true,
                "EMP-01"
        );

        var otraSolicitud = crearSolicitud(
                CategoriaGasto.REPRESENTACION,
                "120000",
                0,
                LocalDate.of(2026, 11, 10),
                true,
                "EMP-01"
        );

        var resultado = validador.validar(
                solicitud,
                List.of(otraSolicitud)
        );

        assertTrue(resultado.valida());
    }

    @Test
    void validar_rechazaSolicitudSinFechaDeGasto() {

        var solicitud = crearSolicitud(
                CategoriaGasto.VIAJE,
                "100000",
                1,
                LocalDate.of(2026, 11, 20),
                true,
                "EMP-01"
        );

        solicitud.setFechaGasto(null);

        var resultado = validador.validar(solicitud, List.of());

        assertFalse(resultado.valida());
        assertEquals(
                "La fecha del gasto es obligatoria",
                resultado.motivoRechazo()
        );
    }

    @Test
    void validar_ignoraSolicitudesPendientesSinFechaDeGasto() {

        var solicitud = crearSolicitud(
                CategoriaGasto.VIAJE,
                "100000",
                1,
                LocalDate.of(2026, 11, 20),
                true,
                "EMP-01"
        );

        var solicitudSinFecha = crearSolicitud(
                CategoriaGasto.VIAJE,
                "120000",
                1,
                LocalDate.of(2026, 11, 10),
                true,
                "EMP-01"
        );

        solicitudSinFecha.setFechaGasto(null);

        var resultado = validador.validar(
                solicitud,
                List.of(solicitudSinFecha)
        );

        assertTrue(resultado.valida());
    }
}