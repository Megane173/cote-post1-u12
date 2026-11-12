package com.empresa.reembolsos.procesamiento.validacion;

import com.empresa.reembolsos.dominio.CategoriaGasto;
import com.empresa.reembolsos.dominio.SolicitudReembolso;
import com.empresa.reembolsos.procesamiento.LimitesReembolso;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class ValidadorMontoMaximo extends ValidadorSolicitud {

    @Override
    protected ResultadoValidacion validarPropio(
            SolicitudReembolso solicitud,
            List<SolicitudReembolso> pendientes) {

        if (solicitud.getCategoria() == null
                || solicitud.getMontoSolicitado() == null) {

            return ResultadoValidacion.rechazar(
                    "La categoria y el monto solicitado son obligatorios");
        }

        if (solicitud.getCategoria() == CategoriaGasto.VIAJE
                && solicitud.getDiasViaje() <= 0) {

            return ResultadoValidacion.rechazar(
                    "La duracion del viaje debe ser mayor a cero");
        }

        BigDecimal tope = obtenerTope(solicitud);

        return solicitud.getMontoSolicitado().compareTo(tope) > 0
                ? ResultadoValidacion.rechazar(
                "Monto excede el tope de la categoria "
                        + solicitud.getCategoria())
                : ResultadoValidacion.ok();
    }

    private BigDecimal obtenerTope(SolicitudReembolso solicitud) {

        return switch (solicitud.getCategoria()) {

            case VIAJE ->
                    LimitesReembolso.TOPE_DIARIO_VIAJE.multiply(
                            BigDecimal.valueOf(solicitud.getDiasViaje()));

            case REPRESENTACION ->
                    LimitesReembolso.TOPE_MENSUAL_REPRESENTACION;

            case CAPACITACION ->
                    LimitesReembolso.TOPE_SOLICITUD_CAPACITACION;
        };
    }
}