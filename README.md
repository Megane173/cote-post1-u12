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