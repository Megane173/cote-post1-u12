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

### Decisión de diseño 4: Permitir estereotipos de Spring sin acoplar el procesamiento a la infraestructura

**Decisión:** Las reglas de arquitectura permitirán que las clases de `procesamiento` utilicen anotaciones de estereotipo de Spring, como `@Component`, pero prohibirán las dependencias hacia los paquetes de infraestructura, adaptadores, Spring Data JPA y Jakarta Persistence. Las clases de procesamiento tampoco deben depender de tipos propios de la capa web.

**Justificación:** Prohibir indiscriminadamente cualquier dependencia de `org.springframework` sería una restricción más estricta de lo necesario para los objetivos actuales del proyecto. Las anotaciones como `@Component` permiten que el contenedor de Spring detecte y registre las calculadoras, los validadores y los notificadores como componentes administrados. Su utilización introduce una dependencia del mecanismo de configuración de Spring, pero no obliga por sí misma a que estos componentes conozcan cómo se almacenan los datos o cómo se reciben las solicitudes HTTP.

En cambio, depender de clases de Spring Data o Jakarta Persistence vincularía el procesamiento con tecnologías concretas de persistencia. Del mismo modo, depender de clases web, como los tipos utilizados para representar solicitudes y respuestas HTTP, mezclaría las responsabilidades del procesamiento con las de la comunicación. Evitar estas dependencias facilita mantener las reglas de negocio independientes de los detalles de almacenamiento y del protocolo de comunicación.

**Trade-off:** Se acepta un acoplamiento limitado a Spring para registrar los componentes, a cambio de conservar una configuración sencilla con el contenedor de dependencias existente. Esta decisión no convierte el procesamiento en una capa completamente independiente de Spring, pero evita imponer una restricción más amplia de lo que requiere la arquitectura actual.

Si en el futuro se busca una independencia completa del framework, se podría retirar las anotaciones de los componentes y trasladar su registro a una configuración externa. Por ahora, ese cambio añadiría complejidad sin una necesidad identificada que lo justifique.

## Qué se descartó y por qué

### Alternativa descartada: Strategy para mecanismos de notificación

**Qué se consideró:** Introducir una interfaz `MecanismoNotificacion` con distintas implementaciones, como `NotificacionApiRest`, `NotificacionCorreo` y `NotificacionMensajeria`, y utilizar el patrón Strategy para seleccionar el mecanismo de entrega. Así, el sistema podría cambiar de mecanismo sin modificar la lógica que gestiona las notificaciones.

**Por qué se descartó:** Actualmente, solo está anunciada una integración con la API externa de Contabilidad y no existen requisitos que exijan admitir varios mecanismos de entrega o intercambiarlos. Además, `ObservadorCambioEstado` ya desacopla el servicio de las implementaciones concretas de los notificadores. Añadir otra abstracción para seleccionar mecanismos de entrega anticiparía una variabilidad que todavía no está documentada, incorporando interfaces, implementaciones y lógica de selección sin una necesidad actual demostrada.

**Qué señal futura haría reconsiderar la decisión:** Se reconsideraría cuando aparezca un requisito concreto que exija utilizar varios mecanismos de entrega, por ejemplo, enviar las notificaciones de Contabilidad mediante API REST y disponer de un mecanismo alternativo por correo electrónico, o permitir seleccionar el canal según el destinatario o el tipo de notificación.