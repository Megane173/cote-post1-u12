package com.empresa.reembolsos.procesamiento;

import java.math.BigDecimal;

public final class LimitesReembolso {

    public static final BigDecimal TOPE_DIARIO_VIAJE =
            new BigDecimal("150000");

    public static final BigDecimal TOPE_MENSUAL_REPRESENTACION =
            new BigDecimal("2000000");

    public static final BigDecimal TOPE_SOLICITUD_CAPACITACION =
            new BigDecimal("3000000");

    private LimitesReembolso() {
    }
}