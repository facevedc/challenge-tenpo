# challenge-tenpo

Monorepo para resolver el challenge de Tenpo con Java 21, Spring Boot WebFlux y PostgreSQL.

## Estructura

- `api-calculator`: API principal del challenge.
- `postgres`: inicializacion y documentacion de la base de datos.
- `api-mocks`: mocks del servicio externo de porcentaje.
- `docker-compose.yml`: orquestacion local de servicios.

## Estado

Este primer paso deja el esqueleto del repositorio y la base de infraestructura para iterar por commits pequenos y coherentes.

El skeleton inicial refleja una cultura de desarrollo empresarial que mantengo de forma personal: estructura por capas, configuracion desacoplada, observabilidad basica, documentacion desde el arranque, y una base preparada para evolucionar con criterio tecnico y trazabilidad en el historial.

## Documentacion por modulo

- [API calculator](/Users/facevedo/Documents/challenge-tenpo/api-calculator/README.md)
- [PostgreSQL](/Users/facevedo/Documents/challenge-tenpo/postgres/README.md)
- [API mocks](/Users/facevedo/Documents/challenge-tenpo/api-mocks/README.md)

## OpenAPI

El contrato inicial vive en:

- [openapi.yml](/Users/facevedo/Documents/challenge-tenpo/api-calculator/src/main/resources/static/openapi.yml)

## Levante local

```bash
docker compose up --build
```

Servicios previstos:

- API: `http://localhost:8080`
- OpenAPI YAML: `http://localhost:8080/openapi.yml`
- Mock porcentaje: `http://localhost:8081`
- PostgreSQL: `localhost:5432`

## Estrategia tecnica

- WebFlux obligatorio para el bonus y para mantener el flujo no bloqueante.
- R2DBC + PostgreSQL para historial de llamadas.
- Cache local simple con Caffeine solo sirve bien en despliegue single replica.
- Si el challenge enfatiza replicas, lo correcto es migrar cache y rate limit a un store compartido, idealmente Redis.
- Rate limit en la API con filtro reactivo. Para replicas reales, conviene algoritmo distribuido respaldado por Redis.
- Registro de historial desacoplado y asincrono para no impactar la latencia del endpoint principal.
