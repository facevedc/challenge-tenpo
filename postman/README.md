# Postman

Coleccion y environment para ejecutar escenarios del challenge sin reiniciar contenedores.

## Archivos

- `challenge-tenpo-local.postman_collection.json`
- `challenge-tenpo-local.postman_environment.json`

## Escenarios disponibles

- flujo exitoso
- retry exitoso
- fallback con cache
- fallback sin cache
- consulta de historial paginado
- validacion de trazas exitosas en historial
- validacion de trazas con error en historial

## Precondiciones

- stack levantada con `docker compose up -d --build`
- API disponible en `http://localhost:8080`
- mock disponible en `http://localhost:8081`

## Paso a paso sugerido

### 1. Validar salud de la API

Ejecuta el request:

- `Health`

Esperado:

- HTTP `200`
- `status: UP`

### 2. Reiniciar el estado del mock

Ejecuta el request:

- `Reiniciar escenarios mock`

O desde terminal:

```bash
../scripts/reset_mock_scenarios.sh
```

### 3. Probar flujo exitoso

Ejecuta el request:

- `Flujo Exitoso`

Esperado:

- HTTP `200`
- `percentage_source: external-mock`
- `final_amount: 13.2`

### 4. Probar retry exitoso

Antes de ejecutar:

- reinicia los escenarios del mock
- limpia Redis con `../scripts/reset_percentage_cache.sh`
```bash
../scripts/reset_mock_scenarios.sh
../scripts/reset_percentage_cache.sh
```
Luego ejecuta:

- `Retry Exitoso`

Esperado:

- HTTP `200`
- `percentage_source: external-mock`
- `percentage: 10`

### 5. Probar fallback con cache

Primero ejecuta:

- `Sembrar Cache Para Fallback`

Luego ejecuta:

- `Fallback Con Cache`

Esperado:

- HTTP `200`
- `percentage_source: redis-cache`

### 6. Probar fallback sin cache

Antes de ejecutar:

- limpia Redis con `../scripts/reset_percentage_cache.sh`

Luego ejecuta:

- `Fallback Sin Cache`

Esperado:

- HTTP `503`
- `code: SERVICE_UNAVAILABLE`

### 7. Probar consulta de historial paginado

Ejecuta el request:

- `Consultar Historial Paginado`

Esperado:

- HTTP `200`
- respuesta con `items`, `page`, `size`, `total_elements` y `total_pages`

### 8. Probar registro de una llamada exitosa en historial

Primero ejecuta:

- `Flujo Exitoso`

Espera un momento si quieres dar margen al registro asincrono.

Luego ejecuta:

- `Validar Registro De Exito En Historial`

Esperado:

- existe una entrada con:
  - `endpoint: /api/v1/calculations`
  - `response_status: 200`

### 9. Probar registro de una llamada con error en historial

Primero genera el error con:

```bash
curl -sS -H 'Accept: application/json' '{{baseUrl}}/api/v1/calculations?num1=abc&num2=7'
```

o desde Postman duplicando `Flujo Exitoso` y dejando `num1=abc`.

Luego ejecuta:

- `Validar Registro De Error En Historial`

Esperado:

- existe una entrada con:
  - `endpoint: /api/v1/calculations`
  - `response_status: 400`
  - `error_message: Invalid decimal query param: num1`

### 10. Probar validacion de paginacion del historial

Ejecuta el request:

- `Historial Paginacion Invalida`

Esperado:

- HTTP `400`
- `code: BAD_REQUEST`

## Notas

- Para reiniciar el estado del mock usa el request `Reiniciar escenarios mock`.
- Tambien puedes reiniciar el mock desde terminal con `./scripts/reset_mock_scenarios.sh`.
- Para limpiar Redis antes de `fallback sin cache` usa `./scripts/reset_percentage_cache.sh`.
- Para inspeccionar el valor cacheado usa `./scripts/show_percentage_cache.sh`.
- El historial se registra de forma asincrona, por lo que puede convenir esperar 1 o 2 segundos antes de validar una traza recien generada.
