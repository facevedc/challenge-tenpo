# api-calculator

API principal del challenge.

## Stack

- Java 21
- Spring Boot 3
- Spring WebFlux
- Spring Data R2DBC
- PostgreSQL
- OpenAPI
- Docker

## Capas

- `api`: endpoints, DTOs, manejo HTTP y filtros.
- `domain`: reglas de negocio, contratos y modelos.
- `infrastructure`: adaptadores externos, persistencia y cache.
- `setting`: configuracion de beans y propiedades.

## Estado actual

Esta primera iteracion deja el bootstrap tecnico del servicio y la estructura base para desarrollar los requerimientos del challenge por pasos.

## Nota sobre replicas

Caffeine es util para una primera implementacion local, pero no garantiza consistencia entre multiples replicas.
Si quieres defender escalabilidad real en la entrevista, conviene evolucionar a Redis para:

- cache compartido del porcentaje
- rate limit distribuido
- comportamiento consistente al escalar horizontalmente

## Ejecucion local

```bash
./gradlew bootRun
```

## OpenAPI

- [openapi.yml](/Users/facevedo/Documents/challenge-tenpo/api-calculator/src/main/resources/static/openapi.yml)

La documentacion se expone como archivo OpenAPI estatico para reducir dependencias runtime no esenciales del challenge.

## Soporte de pruebas locales

La API acepta el header opcional `X-Mock-Scenario` para pruebas locales e integracion con `api-mocks`.

Escenarios soportados:

- `success`
- `retry-success`
- `error`

Si el header no se envia, el flujo sigue usando el comportamiento exitoso por defecto del mock.
