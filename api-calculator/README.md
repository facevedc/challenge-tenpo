# api-calculator

API reactiva principal del challenge.

## Stack

- Java 21
- Spring Boot 3.5
- Spring WebFlux
- Spring Data R2DBC
- Spring Data Redis Reactive
- PostgreSQL
- Redis

## Capas

- `api`: handlers, DTOs, validaciones y manejo HTTP.
- `domain`: casos de uso, contratos y modelos.
- `infrastructure`: adaptadores concretos de cache, persistencia y cliente externo.
- `settings`: configuracion de router, serializacion, WebClient y Redis.

## Responsabilidades

- exponer `GET /api/v1/calculations`
- exponer `GET /api/v1/history`
- integrar el porcentaje externo con retry y fallback
- registrar historial asincrono
- responder errores homogeneos

## Ejecucion

Para desarrollo aislado:

```bash
./gradlew bootRun
```

Para la solucion completa se recomienda levantar el proyecto desde la raiz con Docker Compose.

## Testing

```bash
./gradlew test jacocoTestCoverageVerification
```

## Contrato

El contrato OpenAPI fuente vive en:

- [openapi.yml](/Users/facevedo/Documents/challenge-tenpo/api-calculator/src/main/resources/static/openapi.yml)

Y queda accesible desde la stack completa en:

- `http://localhost/openapi.yml`
- `http://localhost:8082`

## Pruebas locales con mocks

La API acepta el header opcional `X-Mock-Scenario` para pruebas controladas con `api-mocks`.

Valores soportados:

- `success`
- `retry-success`
- `error`
