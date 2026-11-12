package com.empresa.reembolsos.procesamiento.validacion;

import com.empresa.reembolsos.dominio.CategoriaGasto;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static com.empresa.reembolsos.procesamiento.SolicitudReembolsoTestData.crearSolicitud;

class ValidadorDocumentosSoporteTest {

    private final ValidadorDocumentosSoporte validador =
            new ValidadorDocumentosSoporte();

    @Test
    void validar_rechazaSolicitudSinDocumentosSoporte() {

        var solicitud = crearSolicitud(
                CategoriaGasto.VIAJE,
                "100000",
                1,
                LocalDate.of(2026, 11, 10),
                false,
                "EMP-01"
        );

        var resultado = validador.validar(solicitud, List.of());

        assertFalse(resultado.valida());
        assertEquals(
                "Faltan documentos soporte",
                resultado.motivoRechazo()
        );
    }

    @Test
    void validar_aceptaSolicitudConDocumentosSoporte() {

        var solicitud = crearSolicitud(
                CategoriaGasto.VIAJE,
                "100000",
                1,
                LocalDate.of(2026, 11, 10),
                true,
                "EMP-01"
        );

        var resultado = validador.validar(solicitud, List.of());

        assertTrue(resultado.valida());
        assertNull(resultado.motivoRechazo());
    }
}