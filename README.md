# Urban Bike Rental API

## Descripción

Urban Bike Rental API es una API REST desarrollada con Spring Boot para gestionar bicicletas urbanas y sus alquileres. Permite registrar bicicletas, consultar disponibilidad, iniciar y finalizar alquileres, consultar historial por bicicleta, calcular costos de uso y aplicar multas por retraso.

## Tecnologías utilizadas

- Java 17
- Spring Boot 3.x
- Maven
- Spring Web
- Spring Data JPA
- H2 Database
- Bean Validation
- JUnit 5
- Mockito

## Arquitectura elegida

El proyecto usa una arquitectura por capas:

- `Controller`: expone los endpoints REST y recibe DTOs de entrada.
- `Service`: contiene las reglas de negocio y coordina operaciones transaccionales.
- `Repository`: encapsula el acceso a datos con Spring Data JPA.
- `Model`: define las entidades JPA y enums del dominio.
- `DTO`: separa los contratos HTTP de las entidades internas.
- `Exception`: contiene excepciones de negocio y manejo global de errores.
- `Config`: configura datos iniciales de la aplicación.
- `PricingService`: centraliza tarifas, redondeo, costos base, multas y total.

## Justificación de arquitectura

Se eligió arquitectura por capas porque cumple el alcance de una API REST sencilla, separa responsabilidades, facilita pruebas unitarias y evita complejidad innecesaria. No se usaron microservicios ni arquitectura hexagonal porque el taller no lo requiere y agregarían sobreingeniería para el problema planteado.

## Principios aplicados

- SOLID: cada clase tiene una responsabilidad clara y las dependencias se inyectan por constructor.
- DRY: `PricingService` centraliza el cálculo de costos y `GlobalExceptionHandler` centraliza las respuestas de error.

## Reglas de negocio implementadas

- Tarifas por tipo de bicicleta:
  - `URBANA`: 3500 por hora.
  - `MONTANA`: 5000 por hora.
  - `ELECTRICA`: 7500 por hora.
- El tiempo usado se redondea al alza a horas completas.
- Si hay retraso, se calcula una multa del 50% de la tarifa por hora por cada hora tarde redondeada al alza.
- Solo una bicicleta en estado `DISPONIBLE` puede alquilarse.
- Al iniciar un alquiler, la bicicleta cambia a `ALQUILADA`.
- Al finalizar un alquiler, la bicicleta cambia a `DISPONIBLE`.
- No se puede finalizar un alquiler inexistente.
- No se puede finalizar un alquiler que ya está `FINALIZADO`.
- No se permite registrar bicicletas con códigos duplicados.
- La duración estimada del alquiler debe ser mayor que cero.

## Supuestos tomados

- `MONTANA` representa `MONTAÑA` por convención técnica en enums Java.
- `ELECTRICA` representa `ELÉCTRICA` por convención técnica en enums Java.
- `startTime` se genera automáticamente al iniciar un alquiler.
- `returnTime` puede enviarse al finalizar para facilitar pruebas.
- Si `returnTime` no se envía, se usa `LocalDateTime.now()`.
- H2 se usa para facilitar ejecución local sin instalar una base de datos externa.
- No se implementó autenticación porque el enunciado no lo pide; se aplicó seguridad básica con validaciones, DTOs y manejo global de errores.

## Cómo ejecutar localmente

Con Maven instalado:

```bash
mvn clean test
mvn spring-boot:run
```

Con Maven Wrapper en Windows:

```powershell
.\mvnw.cmd clean test
.\mvnw.cmd spring-boot:run
```

Con Maven Wrapper en Linux/Mac:

```bash
./mvnw clean test
./mvnw spring-boot:run
```

API:

```text
http://localhost:8080
```

H2 Console:

```text
http://localhost:8080/h2-console
```

Credenciales H2:

```text
JDBC URL: jdbc:h2:mem:bikerentaldb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
User: sa
Password: vacío
```

## Cómo ejecutar pruebas

```bash
mvn test
```

En Windows también se puede usar:

```powershell
.\mvnw.cmd clean test
```

## Datos iniciales

| Código | Tipo | Estado |
|---|---|---|
| BIC-001 | URBANA | DISPONIBLE |
| BIC-002 | MONTANA | DISPONIBLE |
| BIC-003 | ELECTRICA | DISPONIBLE |
| BIC-004 | MONTANA | EN_MANTENIMIENTO |
| BIC-005 | URBANA | DISPONIBLE |

## Endpoints

| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/api/bicycles` | Registra una bicicleta. |
| GET | `/api/bicycles` | Lista todas las bicicletas. |
| GET | `/api/bicycles/available` | Lista bicicletas disponibles. |
| GET | `/api/bicycles/available?type=URBANA` | Lista bicicletas disponibles filtradas por tipo. |
| GET | `/api/bicycles/{code}/rentals` | Consulta el historial de alquileres de una bicicleta. |
| POST | `/api/rentals/start` | Inicia un alquiler. |
| PUT | `/api/rentals/{id}/finish` | Finaliza un alquiler y calcula costos. |
| GET | `/api/rentals` | Lista todos los alquileres. |
| GET | `/api/rentals/{id}` | Consulta un alquiler por ID. |

## Ejemplos curl

Crear bicicleta:

```bash
curl -X POST http://localhost:8080/api/bicycles \
  -H "Content-Type: application/json" \
  -d '{"code":"BIC-006","type":"URBANA","status":"DISPONIBLE"}'
```

Crear bicicleta en Windows CMD:

```cmd
curl.exe -X POST http://localhost:8080/api/bicycles -H "Content-Type: application/json" -d "{\"code\":\"BIC-006\",\"type\":\"URBANA\",\"status\":\"DISPONIBLE\"}"
```

Listar bicicletas:

```bash
curl http://localhost:8080/api/bicycles
```

Consultar bicicletas disponibles:

```bash
curl http://localhost:8080/api/bicycles/available
```

Consultar bicicletas disponibles por tipo:

```bash
curl "http://localhost:8080/api/bicycles/available?type=URBANA"
```

Iniciar alquiler:

```bash
curl -X POST http://localhost:8080/api/rentals/start \
  -H "Content-Type: application/json" \
  -d '{"bicycleCode":"BIC-001","customerName":"Juan Vizuette","estimatedDurationHours":2}'
```

Iniciar alquiler en Windows CMD:

```cmd
curl.exe -X POST http://localhost:8080/api/rentals/start -H "Content-Type: application/json" -d "{\"bicycleCode\":\"BIC-001\",\"customerName\":\"Juan Vizuette\",\"estimatedDurationHours\":2}"
```

Finalizar alquiler con `returnTime`:

```bash
curl -X PUT http://localhost:8080/api/rentals/1/finish \
  -H "Content-Type: application/json" \
  -d '{"returnTime":"2026-05-16T13:20:00"}'
```

Finalizar alquiler en Windows CMD:

```cmd
curl.exe -X PUT http://localhost:8080/api/rentals/1/finish -H "Content-Type: application/json" -d "{\"returnTime\":\"2026-05-16T13:20:00\"}"
```

Consultar historial por bicicleta:

```bash
curl http://localhost:8080/api/bicycles/BIC-001/rentals
```

## Flujo recomendado de prueba

1. Consultar bicicletas disponibles:

```bash
curl http://localhost:8080/api/bicycles/available
```

2. Iniciar alquiler con `BIC-001`:

```bash
curl -X POST http://localhost:8080/api/rentals/start \
  -H "Content-Type: application/json" \
  -d '{"bicycleCode":"BIC-001","customerName":"Juan Vizuette","estimatedDurationHours":2}'
```

3. Consultar disponibles para verificar que `BIC-001` ya no aparece:

```bash
curl http://localhost:8080/api/bicycles/available
```

4. Finalizar el alquiler:

```bash
curl -X PUT http://localhost:8080/api/rentals/1/finish \
  -H "Content-Type: application/json" \
  -d '{"returnTime":"2026-05-16T13:20:00"}'
```

5. Verificar que la bicicleta vuelve a estar disponible:

```bash
curl http://localhost:8080/api/bicycles/available
```

6. Consultar historial por bicicleta:

```bash
curl http://localhost:8080/api/bicycles/BIC-001/rentals
```

## Manejo de errores

La API devuelve errores JSON consistentes con `status`, `message` y `timestamp`. No se exponen entidades internas ni stacktraces.

Ejemplo:

```json
{
  "status": 400,
  "message": "Campo 'estimatedDurationHours': must be greater than 0",
  "timestamp": "2026-05-16T11:18:40"
}
```

## Pruebas automatizadas

Las pruebas automatizadas cubren:

- Tarifas por tipo de bicicleta.
- Redondeo al alza de horas.
- Cálculo de horas tarde.
- Costo base, multa y costo total.
- Inicio de alquiler con bicicleta disponible.
- Rechazo de alquiler para bicicleta no disponible.
- Finalización de alquiler activo.
- Rechazo de alquiler ya finalizado.
- Rechazo de alquiler inexistente.
- Consulta de bicicletas disponibles con y sin filtro.
- Rechazo de códigos duplicados.
- Carga básica del contexto Spring.

## Verificación final

```powershell
.\mvnw.cmd clean test
.\mvnw.cmd spring-boot:run
```

Revisar:

```text
http://localhost:8080
http://localhost:8080/h2-console
```
