package com.empresa.reembolsos.procesamiento.calculo;

import com.empresa.reembolsos.dominio.CategoriaGasto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

import static com.empresa.reembolsos.procesamiento.SolicitudReembolsoTestData.crearSolicitud;

class CalculadoraCapacitacionTest {

    private final CalculadoraCapacitacion calculadora =
            new CalculadoraCapacitacion();

    @Test
    void calcular_aplicaPorcentajeDeReembolsoDel80PorCiento() {

        var solicitud = crearSolicitud(
                CategoriaGasto.CAPACITACION,
                "1000000",
                0,
                LocalDate.of(2026, 11, 10),
                true,
                "EMP-01"
        );

        var resultado = calculadora.calcular(solicitud);

        assertEquals(
                0,
                new BigDecimal("800000").compareTo(resultado)
        );
    }

    @Test
    void calcular_aplicaTopeMaximoDeCapacitacion() {

        var solicitud = crearSolicitud(
                CategoriaGasto.CAPACITACION,
                "5000000",
                0,
                LocalDate.of(2026, 11, 10),
                true,
                "EMP-01"
        );

        var resultado = calculadora.calcular(solicitud);

        assertEquals(
                0,
                new BigDecimal("3000000").compareTo(resultado)
        );
    }
}