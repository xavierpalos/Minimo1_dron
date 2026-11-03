# Gestión de flota de drones — Mínimo 1 DSA

Prueba piloto para gestionar una flota de drones: altas de drones y pilotos,
almacén de mantenimiento y reservas de planes de vuelo.

## Tecnologías
- Java 17 · Maven
- JUnit 4 (tests) · Log4j 1.2 (trazas)
- Jersey 2 + Grizzly (servicio REST)

## Cómo ejecutarlo
- **Tests:** ejecutar `DroneManagerTest`, o `mvn test`.
- **Servicio REST:** ejecutar `Main`. El servidor arranca en `http://localhost:8080/dsaApp/`
  con 3 drones (D1, D2, D3) y 2 pilotos (P1, P2) cargados.
- **Pruebas del servicio:** fichero `drones.http` (y/o la colección Postman `Drones.postman_collection.json`).

## Parte I — Componente

### Entidades
| Entidad | Atributos |
|---|---|
| Drone | id, name, manufacturer, model, flightHours, inMaintenance |
| Pilot | id, name, surname, flightHours |
| Position | lat, lon |
| FlightPlan | droneId, pilotId, date, hours, start, destination |

### Estructuras de datos (DroneManagerImpl, Singleton)
- `HashMap<String, Drone>` y `HashMap<String, Pilot>`: búsqueda por id en O(1).
- `Stack<Drone>` para el almacén: el dron que se repara es el más cercano a la puerta,
  es decir, el último que entró (LIFO).
- `HashMap<String, List<FlightPlan>>` por dron y por piloto: listados directos y
  comprobación de solapamientos sin recorrer todas las reservas.

### Decisiones de diseño
- Las horas de vuelo de un dron y de un piloto aumentan al **crear una reserva**,
  sumando las horas estimadas.
- `FlightPlan` guarda los **ids** del dron y del piloto (no los objetos) para evitar ciclos.
- Dos vuelos solapan si `inicioA < finB && inicioB < finA`. Un vuelo que empieza
  justo cuando acaba otro no solapa.
- Formato de fecha: ISO `yyyy-MM-ddTHH:mm` (ej. `2026-11-05T10:00`).

### Tests
4 tests JUnit: altas, almacén LIFO, reservas (con todos los casos de error)
y listados ordenados por horas de vuelo.

## Parte II — Servicio REST

| Operación | Método | Ruta | Body | Respuestas |
|---|---|---|---|---|
| Añadir dron | POST | /drones | DroneTO | 201 · 409 id repetido |
| Añadir piloto | POST | /pilots | PilotTO | 201 · 409 id repetido |
| Drones por horas (desc) | GET | /drones | — | 200 |
| Pilotos por horas (desc) | GET | /pilots | — | 200 |
| Guardar en almacén | POST | /warehouse/{droneId} | — | 201 · 404 |
| Reparar dron | PUT | /warehouse/repair | — | 200 · 404 almacén vacío |
| Añadir reserva | POST | /flightplans | FlightPlan | 201 · 400 |
| Reservas de un piloto | GET | /pilots/{id}/flightplans | — | 200 · 404 |
| Reservas de un dron | GET | /drones/{id}/flightplans | — | 200 · 404 |

Se usan objetos de transferencia (`DroneTO`, `PilotTO`) para que el cliente solo
pueda enviar los datos que pide el enunciado.

## Estado actual
- ✅ Parte I completa: entidades, interfaz, fachada Singleton con trazas Log4j y 4 tests en verde.
- ✅ Parte II completa: las 9 operaciones expuestas por REST y probadas.

## Problemas encontrados
- Jersey 2 no puede leer clases compiladas con Java 26 ("Unsupported class file
  major version 70"). Solución: compilar para Java 17 en el `pom.xml`.

## Pendiente / mejoras
- La fachada devuelve `null` en cualquier error, así que el servicio REST no
  distingue el motivo (por ejemplo, en el almacén se devuelve 404 tanto si el dron
  no existe como si ya está dentro). Mejora: usar excepciones propias para devolver
  un código HTTP distinto en cada caso.
- Los datos se guardan solo en memoria; se pierden al reiniciar el servidor.