# api-mocks

Mocks del servicio externo que entrega el porcentaje adicional.

## Implementacion

Se usa WireMock en contenedor para mantener el proyecto simple, portable y reproducible.

## Endpoint principal

- `GET /external/percentage`

Respuesta base:

```json
{
  "percentage": 10
}
```

## Escenarios soportados

- `success`
- `retry-success`
- `error`

## Seleccion de escenario

El comportamiento cambia segun el header opcional:

- `X-Mock-Scenario: success`
- `X-Mock-Scenario: retry-success`
- `X-Mock-Scenario: error`

Si el header no se envia, el mock responde el flujo exitoso por defecto.

## Reinicio de escenarios

```bash
./scripts/reset_mock_scenarios.sh
```

## Verificacion rapida

```bash
curl -sS -H 'X-Mock-Scenario: success' http://localhost:8081/external/percentage
curl -sS -H 'X-Mock-Scenario: error' http://localhost:8081/external/percentage
```
