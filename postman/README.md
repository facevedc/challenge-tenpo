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

## Notas

- Para reiniciar el estado del mock usa el request `Reiniciar escenarios mock`.
- Tambien puedes reiniciar el mock desde terminal con `./scripts/reset_mock_scenarios.sh`.
- Para limpiar Redis antes de `fallback sin cache` usa `./scripts/reset_percentage_cache.sh`.
- Para inspeccionar el valor cacheado usa `./scripts/show_percentage_cache.sh`.
