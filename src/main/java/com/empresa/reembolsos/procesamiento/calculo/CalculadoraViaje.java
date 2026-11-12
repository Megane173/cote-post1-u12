package com.empresa.reembolsos.procesamiento.calculo;

import com.empresa.reembolsos.dominio.CategoriaGasto;
import com.empresa.reembolsos.dominio.SolicitudReembolso;
import com.empresa.reembolsos.procesamiento.LimitesReembolso;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CalculadoraViaje implements CalculadoraReembolso {

    @Override
    public CategoriaGasto getCategoria() {
        return CategoriaGasto.VIAJE;
    }

    @Override
    public BigDecimal calcular(SolicitudReembolso solicitud) {

        var topeTotal = LimitesReembolso.TOPE_DIARIO_VIAJE.multiply(
                BigDecimal.valueOf(solicitud.getDiasViaje()));

        return solicitud.getMontoSolicitado().min(topeTotal);
    }
}