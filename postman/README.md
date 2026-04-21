# Postman

Coleccion y environment para ejecutar los casos del challenge desde Postman, alineados al acceso actual por Nginx.

## Archivos

- `challenge-tenpo-local.postman_collection.json`
- `challenge-tenpo-local.postman_environment.json`

## Variables principales

- `baseUrl = http://localhost`
- `swaggerUiUrl = http://localhost:8082`
- `mockBaseUrl = http://localhost:8081`

La coleccion genera un `X-Client-Id` propio para la mayoria de los requests funcionales, evitando que el rate limit afecte escenarios que no se estan probando.

## Grupos disponibles

- `Soporte`
- `Escenarios Calculo`
- `Escenarios Historial`
- `Escenarios Rate Limit`
- `Escenarios Balanceo`

## Paso a paso recomendado

### 1. Confirmar stack y documentacion

Ejecuta:

- `Soporte / Health`
- `Soporte / OpenAPI YAML`
- `Soporte / Swagger UI`

Esperado:

- health `200`
- `openapi.yml` visible
- `swagger-ui` disponible en `http://localhost:8082`

### 2. Reiniciar estado base

Desde terminal:

```bash
./scripts/reset_challenge_state.sh
```

Desde Postman:

- `Soporte / Reiniciar escenarios mock`

### 3. Probar flujo exitoso

Ejecuta:

- `Escenarios Calculo / Flujo Exitoso`

Esperado:

- `200`
- `final_amount = 13.2`
- `percentage_source = external-mock`

### 4. Probar retry exitoso

Antes:

```bash
./scripts/reset_mock_scenarios.sh
./scripts/reset_percentage_cache.sh
```

Ejecuta:

- `Escenarios Calculo / Retry Exitoso`

Esperado:

- `200`
- `percentage = 10`
- `percentage_source = external-mock`

### 5. Probar fallback con cache

Antes:

```bash
./scripts/reset_mock_scenarios.sh
./scripts/reset_percentage_cache.sh
```

Ejecuta:

- `Escenarios Calculo / Sembrar Cache Para Fallback`
- `Escenarios Calculo / Fallback Con Cache`

Esperado en el segundo request:

- `200`
- `percentage_source = redis-cache`

### 6. Probar fallback sin cache

Antes:

```bash
./scripts/reset_mock_scenarios.sh
./scripts/reset_percentage_cache.sh
```

Ejecuta:

- `Escenarios Calculo / Fallback Sin Cache`

Esperado:

- `503`
- `code = SERVICE_UNAVAILABLE`

### 7. Generar un error funcional

Ejecuta:

- `Escenarios Calculo / Calculo Con Parametro Invalido`

Esperado:

- `400`
- `code = BAD_REQUEST`

### 8. Validar historial asincrono

Antes:

```bash
./scripts/reset_history.sh
```

Ejecuta:

- `Escenarios Calculo / Flujo Exitoso`
- `Escenarios Calculo / Calculo Con Parametro Invalido`

Espera 1 o 2 segundos y luego ejecuta:

- `Escenarios Historial / Consultar Historial Paginado`
- `Escenarios Historial / Validar Registro De Exito En Historial`
- `Escenarios Historial / Validar Registro De Error En Historial`

Esperado:

- historial `200`
- existe una entrada con `response_status = 200`
- existe una entrada con `response_status = 400`

### 9. Validar paginacion invalida

Ejecuta:

- `Escenarios Historial / Historial Paginacion Invalida`

Esperado:

- `400`
- `code = BAD_REQUEST`

### 10. Validar rate limit

Ejecuta:

- `Soporte / Generar cliente de rate limit`
- `Escenarios Rate Limit / Rate Limit 1`
- `Escenarios Rate Limit / Rate Limit 2`
- `Escenarios Rate Limit / Rate Limit 3`
- `Escenarios Rate Limit / Rate Limit 4`

Esperado:

- primeras tres `200`
- cuarta `429`

### 11. Validar balanceo

Ejecuta:

- `Soporte / Reiniciar evidencia de balanceo`
- `Escenarios Balanceo / Observar Replica 1`
- `Escenarios Balanceo / Observar Replica 2`
- `Escenarios Balanceo / Observar Replica 3`
- `Escenarios Balanceo / Observar Replica 4`
- `Escenarios Balanceo / Validar Balanceo Observado`

Esperado:

- se detectan al menos dos `app.instance-id` distintos
