package com.empresa.reembolsos.procesamiento.calculo;

import com.empresa.reembolsos.dominio.CategoriaGasto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

import static com.empresa.reembolsos.procesamiento.SolicitudReembolsoTestData.crearSolicitud;

class CalculadoraRepresentacionTest {

    private final CalculadoraRepresentacion calculadora =
            new CalculadoraRepresentacion();

    @Test
    void calcular_aplicaTopeMensualDeRepresentacion() {

        var solicitud = crearSolicitud(
                CategoriaGasto.REPRESENTACION,
                "2500000",
                0,
                LocalDate.of(2026, 11, 10),
                true,
                "EMP-01"
        );

        var resultado = calculadora.calcular(solicitud);

        assertEquals(
                0,
                new BigDecimal("2000000").compareTo(resultado)
        );
    }

    @Test
    void calcularDevuelveMontoSolicitadoCuandoEsInferiorAlTope() {

        var solicitud = crearSolicitud(
                CategoriaGasto.REPRESENTACION,
                "1500000",
                0,
                LocalDate.of(2026, 11, 10),
                true,
                "EMP-01"
        );

        var resultado = calculadora.calcular(solicitud);

        assertEquals(
                0,
                new BigDecimal("1500000").compareTo(resultado)
        );
    }
}