package com.empresa.reembolsos.procesamiento;

import com.empresa.reembolsos.dominio.CategoriaGasto;
import com.empresa.reembolsos.dominio.SolicitudReembolso;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class SolicitudReembolsoTestData {

    private SolicitudReembolsoTestData() {
    }

    public static SolicitudReembolso crearSolicitud(
            CategoriaGasto categoria,
            String monto,
            int dias,
            LocalDate fecha,
            boolean documentos,
            String empleadoId) {

        var solicitud = new SolicitudReembolso();

        solicitud.setEmpleadoId(empleadoId);
        solicitud.setCategoria(categoria);
        solicitud.setMontoSolicitado(new BigDecimal(monto));
        solicitud.setFechaGasto(fecha);
        solicitud.setDiasViaje(dias);
        solicitud.setDocumentosSoporte(documentos);

        return solicitud;
    }
}