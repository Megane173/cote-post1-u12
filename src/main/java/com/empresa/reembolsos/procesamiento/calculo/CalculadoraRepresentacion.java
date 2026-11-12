package com.empresa.reembolsos.procesamiento.calculo;

import com.empresa.reembolsos.dominio.CategoriaGasto;
import com.empresa.reembolsos.dominio.SolicitudReembolso;
import com.empresa.reembolsos.procesamiento.LimitesReembolso;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CalculadoraRepresentacion implements CalculadoraReembolso {

    @Override
    public CategoriaGasto getCategoria() {
        return CategoriaGasto.REPRESENTACION;
    }

    @Override
    public BigDecimal calcular(SolicitudReembolso solicitud) {
        return solicitud.getMontoSolicitado().min(
                LimitesReembolso.TOPE_MENSUAL_REPRESENTACION);
    }
}