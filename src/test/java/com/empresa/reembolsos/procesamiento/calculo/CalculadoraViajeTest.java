package com.empresa.reembolsos.procesamiento.calculo;

import com.empresa.reembolsos.dominio.CategoriaGasto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

import static com.empresa.reembolsos.procesamiento.SolicitudReembolsoTestData.crearSolicitud;

class CalculadoraViajeTest {

    private final CalculadoraViaje calculadora =
            new CalculadoraViaje();

    @Test
    void calcular_aplicaTopeDiarioMultiplicadoPorDias() {

        var solicitud = crearSolicitud(
                CategoriaGasto.VIAJE,
                "500000",
                3,
                LocalDate.of(2026, 11, 10),
                true,
                "EMP-01"
        );

        var resultado = calculadora.calcular(solicitud);

        assertEquals(
                0,
                new BigDecimal("450000").compareTo(resultado)
        );
    }

    @Test
    void calcularDevuelveMontoSolicitadoCuandoEsInferiorAlTope() {

        var solicitud = crearSolicitud(
                CategoriaGasto.VIAJE,
                "200000",
                3,
                LocalDate.of(2026, 11, 10),
                true,
                "EMP-01"
        );

        var resultado = calculadora.calcular(solicitud);

        assertEquals(
                0,
                new BigDecimal("200000").compareTo(resultado)
        );
    }
}