# challenge-tenpo

Monorepo para resolver el challenge de Tenpo con Java 21, Spring Boot WebFlux, PostgreSQL y Redis.

## Estructura

- `api-calculator`: API principal del challenge.
- `postgres`: inicializacion y documentacion de la base de datos.
- `api-mocks`: mocks del servicio externo de porcentaje.
- `redis`: cache distribuido y soporte de rate limit.
- `docker-compose.yml`: orquestacion local de servicios.

## Estado

Este primer paso deja el esqueleto del repositorio y la base de infraestructura para iterar por commits pequenos y coherentes.

El skeleton inicial refleja una cultura de desarrollo empresarial que mantengo de forma personal: estructura por capas, configuracion desacoplada, observabilidad basica, documentacion desde el arranque, y una base preparada para evolucionar con criterio tecnico y trazabilidad en el historial.

## Documentacion por modulo

- [API calculator](./api-calculator/README.md)
- [PostgreSQL](./postgres/README.md)
- [API mocks](./api-mocks/README.md)
- [Postman](./postman/README.md)

## OpenAPI

El contrato inicial vive en:

- [openapi.yml](./api-calculator/src/main/resources/static/openapi.yml)

## Levante local

```bash
docker compose up --build
```

Servicios previstos:

- API: `http://localhost:8080`
- OpenAPI YAML: `http://localhost:8080/openapi.yml`
- Mock porcentaje: `http://localhost:8081`
- PostgreSQL: `localhost:5432`
- Redis: `localhost:6379`
- Postman assets: `./postman`

## Estrategia tecnica

- WebFlux obligatorio para el bonus y para mantener el flujo no bloqueante.
- R2DBC + PostgreSQL para historial de llamadas.
- Redis como base de cache distribuido y soporte para rate limit consistente entre replicas.
- Rate limit implementado en la API para mantener el alcance acotado, usando Redis como store compartido.
- Registro de historial desacoplado y asincrono para no impactar la latencia del endpoint principal.

## Flujo de desarrollo

- `main`: rama estable de presentacion y entrega.
- `develop`: rama de integracion.
- `feature/*`: ramas de trabajo por bloque funcional.
- Los PR deben apuntar a `develop`.
- La estrategia de merge definida es `Squash and merge` para mantener un historial limpio y coherente.
- Cada PR debe pasar el workflow de `pull_request` antes de integrarse.
