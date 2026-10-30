## Diseño Propuesto

### RF1 — Validación en varias etapas

**Punto de variabilidad:** La incorporación de nuevas verificaciones sin modificar las existentes.

**Patrón propuesto:** Chain of Responsibility (Cadena de Responsabilidad).

**Justificación:** Chain of Responsibility permite organizar las verificaciones en una secuencia de pasos independientes y agregar nuevas reglas sin modificar las existentes, a diferencia de Decorator, que añade responsabilidades a un objeto mediante envolturas sucesivas.

### RF2 — Cálculo distinto por categoría de gasto

**Punto de variabilidad:** Las fórmulas de cálculo del monto aprobado según la categoría de gasto.

**Patrón propuesto:** Strategy (Estrategia).

**Justificación:** Strategy permite encapsular cada fórmula en una implementación independiente y agregar nuevas categorías sin modificar los cálculos existentes, a diferencia de State, que modifica el comportamiento de un objeto según su estado interno.

### RF3 — Notificación a varios interesados

**Punto de variabilidad:** Los interesados que reciben las notificaciones y las acciones que realizan ante cada cambio de estado.

**Patrón propuesto:** Observer (Observador).

**Justificación:** Observer permite notificar a múltiples interesados independientes cuando cambia el estado de una solicitud e incorporar nuevos observadores sin modificar el publicador, a diferencia de Mediator, que centraliza la coordinación de las interacciones entre componentes.

## Decisiones de diseño

### Decisión de diseño 1 — Orden de los validadores

**Decisión:** Encadenar los validadores en el siguiente orden: `ValidadorDocumentosSoporte` → `ValidadorMontoMaximo` → `ValidadorDuplicados`.

**Justificación:** Se priorizan las verificaciones de menor costo y se deja la comparación con las solicitudes pendientes para el final, ya que `Chain of Responsibility` permite interrumpir la cadena cuando una validación falla, evitando recorrer la lista de solicitudes existentes cuando la solicitud ya incumple alguna regla anterior.

**Alternativa considerada:** Ubicar `ValidadorDuplicados` al principio permitiría rechazar inmediatamente las solicitudes duplicadas y evitar las verificaciones posteriores; sin embargo, obligaría a recorrer la lista de solicitudes pendientes incluso cuando la solicitud incumple requisitos más sencillos, como la documentación o el tope del monto.

**Consecuencias:** El orden elegido reduce el costo de procesamiento cuando las verificaciones iniciales rechazan solicitudes, pero no permite detectar duplicados antes de ejecutar esas verificaciones. Se considera preferible porque la comparación contra las solicitudes existentes es la operación más costosa y las otras dos verificaciones son relativamente económicas.

### Decisión de diseño 2 — Selección de calculadoras

**Decisión:** Construir un mapa de calculadoras a partir de la lista de implementaciones de `CalculadoraReembolso` que Spring inyecta automáticamente.

**Justificación:** Se elige el mapa porque permite incorporar nuevas categorías mediante nuevas implementaciones de `CalculadoraReembolso` registradas como componentes de Spring, sin modificar el mecanismo de selección ni las calculadoras existentes, mientras que un `switch` manual exigiría modificar el selector para contemplar cada categoría nueva.

**Alternativa considerada:** Utilizar un `switch` sobre `CategoriaGasto` para seleccionar la calculadora correspondiente.

**Consecuencias:** El mapa reduce la necesidad de modificar código existente al extender el sistema, aunque requiere construir correctamente la asociación entre cada categoría y su calculadora, así como garantizar que cada categoría tenga una implementación registrada.