package com.empresa.reembolsos.procesamiento.validacion;

import com.empresa.reembolsos.dominio.CategoriaGasto;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static com.empresa.reembolsos.procesamiento.SolicitudReembolsoTestData.crearSolicitud;

class ValidadorMontoMaximoTest {

    private final ValidadorMontoMaximo validador =
            new ValidadorMontoMaximo();

    @Test
    void validarViaje_aceptaMontoDentroDelTopeDiarioMultiplicadoPorDias() {

        var solicitud = crearSolicitud(
                CategoriaGasto.VIAJE,
                "300000",
                2,
                LocalDate.of(2026, 11, 10),
                true,
                "EMP-01"
        );

        var resultado = validador.validar(solicitud, List.of());

        assertTrue(resultado.valida());
    }

    @Test
    void validarViaje_rechazaMontoQueSuperaElTopeDiarioMultiplicadoPorDias() {

        var solicitud = crearSolicitud(
                CategoriaGasto.VIAJE,
                "300001",
                2,
                LocalDate.of(2026, 11, 10),
                true,
                "EMP-01"
        );

        var resultado = validador.validar(solicitud, List.of());

        assertFalse(resultado.valida());
    }

    @Test
    void validarViaje_rechazaDuracionIgualACero() {

        var solicitud = crearSolicitud(
                CategoriaGasto.VIAJE,
                "100000",
                0,
                LocalDate.of(2026, 11, 10),
                true,
                "EMP-01"
        );

        var resultado = validador.validar(solicitud, List.of());

        assertFalse(resultado.valida());
        assertEquals(
                "La duracion del viaje debe ser mayor a cero",
                resultado.motivoRechazo()
        );
    }

    @Test
    void validarRepresentacion_aceptaMontoIgualAlTope() {

        var solicitud = crearSolicitud(
                CategoriaGasto.REPRESENTACION,
                "2000000",
                0,
                LocalDate.of(2026, 11, 10),
                true,
                "EMP-02"
        );

        var resultado = validador.validar(solicitud, List.of());

        assertTrue(resultado.valida());
    }

    @Test
    void validarRepresentacion_rechazaMontoQueSuperaElTope() {

        var solicitud = crearSolicitud(
                CategoriaGasto.REPRESENTACION,
                "2000001",
                0,
                LocalDate.of(2026, 11, 10),
                true,
                "EMP-02"
        );

        var resultado = validador.validar(solicitud, List.of());

        assertFalse(resultado.valida());
    }

    @Test
    void validarCapacitacion_aceptaMontoIgualAlTope() {

        var solicitud = crearSolicitud(
                CategoriaGasto.CAPACITACION,
                "3000000",
                0,
                LocalDate.of(2026, 11, 10),
                true,
                "EMP-03"
        );

        var resultado = validador.validar(solicitud, List.of());

        assertTrue(resultado.valida());
    }

    @Test
    void validarCapacitacion_rechazaMontoQueSuperaElTope() {

        var solicitud = crearSolicitud(
                CategoriaGasto.CAPACITACION,
                "3000001",
                0,
                LocalDate.of(2026, 11, 10),
                true,
                "EMP-03"
        );

        var resultado = validador.validar(solicitud, List.of());

        assertFalse(resultado.valida());
    }

    @Test
    void validar_rechazaCategoriaNula() {

        var solicitud = crearSolicitud(
                CategoriaGasto.VIAJE,
                "100000",
                1,
                LocalDate.of(2026, 11, 10),
                true,
                "EMP-01"
        );

        solicitud.setCategoria(null);

        var resultado = validador.validar(solicitud, List.of());

        assertFalse(resultado.valida());
    }

    @Test
    void validar_rechazaMontoNulo() {

        var solicitud = crearSolicitud(
                CategoriaGasto.VIAJE,
                "100000",
                1,
                LocalDate.of(2026, 11, 10),
                true,
                "EMP-01"
        );

        solicitud.setMontoSolicitado(null);

        var resultado = validador.validar(solicitud, List.of());

        assertFalse(resultado.valida());
    }
}