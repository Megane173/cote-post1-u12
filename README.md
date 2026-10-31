## Diseño Propuesto

### RF1 — Validación en varias etapas

**Punto de variabilidad:** La incorporación de nuevas verificaciones sin modificar las existentes.

**Patrón propuesto:** Chain of Responsibility (Cadena de Responsabilidad).

**Justificación:** Chain of Responsibility permite organizar las verificaciones en una secuencia de pasos independientes y agregar nuevas reglas sin modificar las existentes. Se prefiere frente a Decorator, que añade responsabilidades a un objeto mediante envolturas sucesivas, porque el problema consiste en encadenar validaciones que pueden aceptar o rechazar una solicitud e interrumpir la ejecución cuando alguna falla.

### RF2 — Cálculo distinto por categoría de gasto

**Punto de variabilidad:** Las fórmulas de cálculo del monto reembolsable según la categoría de gasto.

**Patrón propuesto:** Strategy (Estrategia).

**Justificación:** Strategy permite encapsular cada fórmula en una implementación independiente y agregar nuevas calculadoras sin modificar el selector ni las calculadoras existentes. Se prefiere frente a State, que permite modificar el comportamiento de un objeto según su estado interno, porque en este caso el algoritmo de cálculo depende de la categoría de gasto, no del estado de la solicitud.

### RF3 — Notificación a varios interesados

**Punto de variabilidad:** Los interesados que reciben las notificaciones y las acciones que realizan ante cada cambio de estado.

**Patrón propuesto:** Observer (Observador).

**Justificación:** Observer permite notificar a múltiples interesados independientes cuando cambia el estado de una solicitud e incorporar nuevos observadores sin modificar el servicio que publica el cambio. Se prefiere frente a Mediator, que centraliza la coordinación de las interacciones entre componentes, porque el sistema necesita comunicar un cambio a varios interesados que reaccionan con su propia lógica.

## Decisiones de diseño

### Decisión de diseño 1 — Orden de los validadores

**Decisión:** Encadenar los validadores en el siguiente orden: `ValidadorDocumentosSoporte` → `ValidadorMontoMaximo` → `ValidadorDuplicados`.

**Justificación:** Se ejecutan primero las verificaciones sencillas y se deja al final la comparación con las solicitudes pendientes. Esta última recorre la lista para buscar duplicados, por lo que ubicarla al final permite evitar ese recorrido cuando alguna validación anterior rechaza la solicitud.

**Alternativa considerada:** Ubicar `ValidadorDuplicados` al principio permitiría rechazar inmediatamente una solicitud duplicada, pero obligaría a recorrer la lista antes de comprobar si cumple los requisitos documentales y económicos.

**Consecuencias:** El orden elegido evita recorridos innecesarios de la lista en los casos en que fallan las validaciones anteriores. No evita la consulta al repositorio ni la recuperación de las solicitudes pendientes, ya que el servicio obtiene esa lista antes de ejecutar la cadena. Por tanto, la mejora se limita al procesamiento de las validaciones en memoria.

### Decisión de diseño 2 — Selección de calculadoras

**Decisión:** Construir un mapa de calculadoras a partir de la lista de implementaciones de `CalculadoraReembolso` que Spring inyecta automáticamente.

**Justificación:** El mapa permite incorporar nuevas calculadoras registradas como componentes de Spring sin modificar el selector ni las calculadoras existentes. Se prefiere frente a un `switch` manual sobre `CategoriaGasto`, que exigiría modificar el selector para contemplar cada categoría nueva.

**Alternativa considerada:** Utilizar un `switch` sobre `CategoriaGasto` para seleccionar la calculadora correspondiente.

**Consecuencias:** El mapa facilita extender el mecanismo de selección y mantiene independientes las fórmulas de cálculo. Sin embargo, agregar una categoría no queda resuelto únicamente con una nueva calculadora: también puede requerir modificar `CategoriaGasto` y actualizar las reglas de validación de los topes aplicables.

### Decisión de diseño 3 — Capas simples para persistencia y conservación del punto de extensión de notificaciones

**Decisión:** Mantener la dependencia directa de `SolicitudReembolsoService` respecto de `RepositorioSolicitudesJpa`, sin introducir por ahora un puerto de persistencia. Para las notificaciones, conservar la interfaz `ObservadorCambioEstado`, sin añadir otra abstracción para seleccionar mecanismos de entrega.

**Justificación:** Actualmente no existe un plan para cambiar el motor de base de datos, por lo que introducir un puerto de persistencia anticiparía una necesidad que no está documentada. En cambio, ya existe una interfaz que desacopla el servicio de los notificadores concretos, y Contabilidad ha anunciado una integración con una API externa para el próximo trimestre. La integración podrá incorporarse detrás del punto de extensión existente sin modificar el servicio central, siempre que se mantenga el contrato de notificación.

**Documentación asociada:** La decisión completa, incluidas sus consecuencias positivas y negativas, se registra en `docs/adr/ADR-001.md`.

### Decisión de diseño 4 — Permitir estereotipos de Spring sin acoplar el procesamiento a la infraestructura

**Decisión:** Las reglas de arquitectura permiten que las clases de `procesamiento` utilicen anotaciones de estereotipo de Spring, como `@Component`, pero prohíben las dependencias hacia los paquetes de infraestructura y adaptadores, Spring Data JPA y Jakarta Persistence. También se prohíben las dependencias hacia tipos de la capa web.

**Justificación:** Prohibir indiscriminadamente cualquier dependencia de `org.springframework` sería una restricción más estricta de lo necesario para los objetivos actuales del proyecto. Anotaciones como `@Component` permiten que el contenedor de Spring detecte y registre las calculadoras, los validadores y los notificadores como componentes administrados. Su utilización introduce una dependencia del mecanismo de configuración de Spring, pero no obliga por sí misma a que estos componentes conozcan cómo se almacenan los datos o cómo se reciben las solicitudes HTTP.

En cambio, las dependencias hacia Spring Data JPA o Jakarta Persistence vincularían el procesamiento con tecnologías concretas de persistencia. Del mismo modo, depender de tipos web mezclaría las responsabilidades del procesamiento con las de la comunicación. Evitar estas dependencias facilita mantener separadas las reglas de negocio y los detalles de almacenamiento y comunicación.

**Trade-off:** Se acepta un acoplamiento limitado a Spring para registrar los componentes, a cambio de conservar una configuración sencilla con el contenedor de dependencias existente. Esta decisión no convierte el procesamiento en una capa completamente independiente de Spring, pero evita imponer una restricción más amplia de lo que requieren los objetivos actuales.

## Resumen de ADR-001

**ADR-001: Capas simples para persistencia y conservación del punto de extensión de notificaciones.**

El ADR evalúa por separado las dependencias de persistencia y notificación, tomando como referencia el criterio de la guía que recomienda valorar la arquitectura Hexagonal para sistemas que sobrevivirán tres años o más con cambios de infraestructura. Como no se ha establecido la vida útil total del sistema, no se afirma que ese umbral temporal se cumpla.

**Persistencia:** Se mantiene la dependencia directa de `RepositorioSolicitudesJpa` porque no existe un plan para cambiar el motor de base de datos ni una necesidad actual que justifique un puerto de persistencia. La consecuencia positiva es evitar abstracciones y mantenimiento adicionales sin un beneficio demostrado. La consecuencia negativa es conservar la dependencia del framework de persistencia, lo que podría requerir modificaciones posteriores si se necesitara sustituirlo.

**Notificaciones:** Se conserva `ObservadorCambioEstado`, ya que desacopla el servicio de los notificadores concretos y proporciona un punto de extensión para la integración de Contabilidad. La consecuencia positiva es que la integración puede incorporarse sin modificar el servicio central, manteniendo el contrato actual. La consecuencia negativa es que la interfaz no garantiza por sí sola la entrega fiable de las notificaciones ni resuelve los errores, reintentos o problemas de disponibilidad de la futura API.

En consecuencia, se mantienen las capas simples para la persistencia y se aprovecha el desacoplamiento existente para las notificaciones, sin introducir una arquitectura Hexagonal completa ni nuevas abstracciones que todavía no responden a una necesidad documentada.

## Qué se descartó y por qué

### Alternativa descartada: Strategy para mecanismos de entrega de notificaciones

**Qué se consideró:** Introducir una interfaz `MecanismoNotificacion`, con implementaciones como `NotificacionApiRest`, `NotificacionCorreo` y `NotificacionMensajeria`, para que los observadores delegaran en una estrategia la entrega de cada notificación. Observer seguiría determinando qué componentes reaccionan ante los cambios de estado, mientras que Strategy permitiría intercambiar el mecanismo de entrega sin modificar la lógica de los observadores.

**Por qué se descartó:** Actualmente, solo está anunciada una integración con la API externa de Contabilidad y no existen requisitos que exijan seleccionar o intercambiar mecanismos de entrega. `ObservadorCambioEstado` ya desacopla el servicio de los notificadores concretos, por lo que introducir otra abstracción anticiparía una variabilidad no documentada. Tampoco se ha identificado duplicación de código de entrega entre varios observadores que justifique centralizar esa responsabilidad.

**Qué señal futura haría reconsiderar la decisión:** Se reconsideraría cuando un requisito exija entregar un mismo tipo de notificación mediante mecanismos intercambiables, por ejemplo, seleccionar entre API REST y correo electrónico según el destinatario o la configuración. En ese escenario, Strategy permitiría encapsular las implementaciones de entrega y reducir las modificaciones necesarias en los observadores al incorporar nuevos mecanismos.