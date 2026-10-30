package com.empresa.reembolsos.dominio;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SolicitudReembolso {
    private Long id;
    private String empleadoId;
    private CategoriaGasto categoria;
    private BigDecimal montoSolicitado;
    private BigDecimal montoAprobado;
    private LocalDate fechaGasto;
    private int diasViaje;
    private boolean documentosSoporte;
    private EstadoSolicitud estado;

    // getters y setters
}

