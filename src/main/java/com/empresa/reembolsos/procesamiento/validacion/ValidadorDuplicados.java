package com.empresa.reembolsos.procesamiento.validacion;

import com.empresa.reembolsos.dominio.SolicitudReembolso;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.util.List;
import java.util.Objects;

@Component
public class ValidadorDuplicados extends ValidadorSolicitud {

    @Override
    protected ResultadoValidacion validarPropio(
            SolicitudReembolso solicitud,
            List<SolicitudReembolso> pendientes) {

        if (solicitud.getFechaGasto() == null) {
            return ResultadoValidacion.rechazar(
                    "La fecha del gasto es obligatoria");
        }

        YearMonth periodoSolicitud = YearMonth.from(solicitud.getFechaGasto());

        boolean existeDuplicado = pendientes.stream()
                .filter(p -> p.getFechaGasto() != null)
                .anyMatch(p ->
                        Objects.equals(p.getEmpleadoId(), solicitud.getEmpleadoId())
                                && p.getCategoria() == solicitud.getCategoria()
                                && YearMonth.from(p.getFechaGasto()).equals(periodoSolicitud)
                );

        return existeDuplicado
                ? ResultadoValidacion.rechazar(
                "Ya existe una solicitud pendiente de la misma categoria en el periodo")
                : ResultadoValidacion.ok();
    }
}