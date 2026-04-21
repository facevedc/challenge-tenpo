# challenge-tenpo

Solucion del challenge de Tenpo construida con Java 21, Spring Boot WebFlux, PostgreSQL, Redis, Nginx y Docker Compose.

El repositorio parte desde un skeleton propio, alineado a una cultura de desarrollo empresarial que mantengo de forma personal: arquitectura por capas, configuracion desacoplada, trazabilidad, documentacion desde el inicio y una historia de trabajo construida por features pequenas y coherentes.

## Arquitectura

- `api-calculator`: API reactiva principal del challenge.
- `postgres`: persistencia del historial de llamadas.
- `redis`: cache distribuido del porcentaje.
- `api-mocks`: mock del servicio externo de porcentaje.
- `nginx`: reverse proxy de entrada, balanceo y rate limit.
- `swagger-ui`: visor OpenAPI para el contrato YAML.
- `postman`: coleccion y environment para ejecutar los casos solicitados.

## URLs locales

- API publica: `http://localhost`
- Health: `http://localhost/actuator/health`
- OpenAPI YAML: `http://localhost/openapi.yml`
- Swagger UI: `http://localhost:8082`
- Mock porcentaje: `http://localhost:8081`
- PostgreSQL: `localhost:5432`
- Redis: `localhost:6379`

## Documentacion por modulo

- [API calculator](./api-calculator/README.md)
- [PostgreSQL](./postgres/README.md)
- [API mocks](./api-mocks/README.md)
- [Postman](./postman/README.md)

## Levantar el proyecto

```bash
docker compose up -d --build
```

Verifica que la stack este operativa:

```bash
curl -sS http://localhost/actuator/health
curl -sS http://localhost/openapi.yml | head -n 5
open http://localhost:8082
```

## Scripts utiles

- `./scripts/reset_mock_scenarios.sh`: reinicia el estado de WireMock.
- `./scripts/reset_percentage_cache.sh`: limpia el porcentaje cacheado en Redis.
- `./scripts/show_percentage_cache.sh`: muestra el porcentaje actualmente cacheado.
- `./scripts/reset_history.sh`: limpia la tabla de historial en PostgreSQL.
- `./scripts/reset_challenge_state.sh`: reinicia mock, cache e historial.

## Como probar cada caso solicitado

### 1. Documentacion y salud

```bash
curl -sS http://localhost/actuator/health
curl -sS http://localhost/openapi.yml | head -n 10
```

Abre en el navegador:

- `http://localhost:8082`

Esperado:

- health `UP`
- `openapi.yml` visible
- Swagger UI renderizando el contrato

### 2. Calculo con porcentaje dinamico

```bash
./scripts/reset_challenge_state.sh
curl -sS -H 'X-Client-Id: demo-success-1' -H 'X-Mock-Scenario: success' \
  'http://localhost/api/v1/calculations?num1=5&num2=7'
```

Esperado:

```json
{"num1":5,"num2":7,"base_sum":12,"percentage":10,"final_amount":13.2,"percentage_source":"external-mock"}
```

### 3. Retry del servicio externo

```bash
./scripts/reset_mock_scenarios.sh
./scripts/reset_percentage_cache.sh
curl -sS -H 'X-Client-Id: demo-retry-1' -H 'X-Mock-Scenario: retry-success' \
  'http://localhost/api/v1/calculations?num1=5&num2=7'
```

Esperado:

- respuesta `200`
- `percentage_source = external-mock`

### 4. Fallback con cache

```bash
./scripts/reset_mock_scenarios.sh
./scripts/reset_percentage_cache.sh
curl -sS -H 'X-Client-Id: demo-cache-seed-1' -H 'X-Mock-Scenario: success' \
  'http://localhost/api/v1/calculations?num1=5&num2=7'
curl -sS -H 'X-Client-Id: demo-cache-fallback-1' -H 'X-Mock-Scenario: error' \
  'http://localhost/api/v1/calculations?num1=5&num2=7'
```

Esperado en la segunda llamada:

```json
{"num1":5,"num2":7,"base_sum":12,"percentage":10,"final_amount":13.2,"percentage_source":"redis-cache"}
```

### 5. Fallback sin cache

```bash
./scripts/reset_mock_scenarios.sh
./scripts/reset_percentage_cache.sh
curl -sS -i -H 'X-Client-Id: demo-no-cache-1' -H 'X-Mock-Scenario: error' \
  'http://localhost/api/v1/calculations?num1=5&num2=7'
```

Esperado:

- HTTP `503`
- body con `code = SERVICE_UNAVAILABLE`

### 6. Historial asincrono

Primero deja el estado limpio:

```bash
./scripts/reset_history.sh
```

Genera una llamada exitosa y una con error:

```bash
curl -sS -H 'X-Client-Id: demo-history-success-1' -H 'X-Mock-Scenario: success' \
  'http://localhost/api/v1/calculations?num1=5&num2=7'
curl -sS -H 'X-Client-Id: demo-history-error-1' -H 'X-Mock-Scenario: success' \
  'http://localhost/api/v1/calculations?num1=abc&num2=7'
sleep 2
curl -sS -H 'X-Client-Id: demo-history-read-1' \
  'http://localhost/api/v1/history?page=0&size=20'
```

Esperado:

- solo aparecen trazas de `/api/v1/calculations`
- se observan fecha/hora, endpoint, query params, response status, response body o error
- la consulta soporta paginacion

Prueba de validacion de paginacion:

```bash
curl -sS -H 'X-Client-Id: demo-history-invalid-1' \
  'http://localhost/api/v1/history?page=-1&size=20'
```

Esperado:

- HTTP `400`
- `code = BAD_REQUEST`

### 7. Rate limit

Usa el mismo `X-Client-Id` para forzar la misma ventana de limitacion:

```bash
curl -sS -o /dev/null -w '%{http_code}\n' -H 'X-Client-Id: demo-rate-limit-1' -H 'X-Mock-Scenario: success' 'http://localhost/api/v1/calculations?num1=5&num2=7'
curl -sS -o /dev/null -w '%{http_code}\n' -H 'X-Client-Id: demo-rate-limit-1' -H 'X-Mock-Scenario: success' 'http://localhost/api/v1/calculations?num1=5&num2=7'
curl -sS -o /dev/null -w '%{http_code}\n' -H 'X-Client-Id: demo-rate-limit-1' -H 'X-Mock-Scenario: success' 'http://localhost/api/v1/calculations?num1=5&num2=7'
curl -sS -o /dev/null -w '%{http_code}\n' -H 'X-Client-Id: demo-rate-limit-1' -H 'X-Mock-Scenario: success' 'http://localhost/api/v1/calculations?num1=5&num2=7'
```

Esperado:

- primeras 3 respuestas `200`
- cuarta respuesta `429`

### 8. Balanceo entre replicas

Consulta varias veces el `info` del Actuator:

```bash
curl -sS http://localhost/actuator/info
curl -sS http://localhost/actuator/info
curl -sS http://localhost/actuator/info
curl -sS http://localhost/actuator/info
```

Esperado:

- el campo `app.instance-id` cambia entre respuestas
- eso evidencia trafico distribuido entre dos replicas de `api-calculator`

## Postman

Importa:

- `./postman/challenge-tenpo-local.postman_collection.json`
- `./postman/challenge-tenpo-local.postman_environment.json`

La coleccion cubre:

- salud y documentacion
- flujo exitoso
- retry exitoso
- fallback con cache
- fallback sin cache
- historial paginado
- validacion de traza exitosa y con error
- rate limit
- evidencia de balanceo

La guia detallada de uso esta en [postman/README.md](./postman/README.md).

## Imagen Docker de la API

La API se construye localmente con este nombre:

- `facevedc/challenge-tenpo-api-calculator:latest`

Tambien deje etiquetada la version:

- `facevedc/challenge-tenpo-api-calculator:1.0.0`

`docker-compose.yml` ya esta preparado para usar esa imagen por defecto en `api-calculator`.

## Estrategia tecnica

- `WebFlux` y `R2DBC` para mantener flujo reactivo y no bloqueante.
- `Redis` para cache distribuido del porcentaje entre replicas.
- `PostgreSQL` para persistir historial de llamadas.
- `Nginx` para rate limit y balanceo de entrada, que es la opcion que mejor conversa con una arquitectura realista de borde.
- registro de historial asincrono para no bloquear la respuesta principal.
- `OpenAPI` estatico + `Swagger UI` desacoplado, evitando dependencias runtime adicionales en la API.

## Flujo Git

- `main`: rama de entrega.
- `develop`: rama de integracion.
- `feature/*`: trabajo incremental por requerimiento.
- PRs hacia `develop`.
- merge `squash` para mantener historial corto y coherente.
