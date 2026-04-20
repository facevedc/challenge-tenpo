# api-mocks

Mocks del servicio externo que entrega el porcentaje adicional.

## Implementacion

Se usa WireMock en contenedor para mantener el repo simple, portable y facil de probar.

## Endpoint mock principal

- `GET /external/percentage`

Respuesta esperada inicial:

```json
{
  "percentage": 10
}
```

## Casos pensados para pruebas

- escenario `success`
- escenario `retry-success`
- escenario `error`

## Seleccion de escenario por header

El mock cambia su comportamiento segun el header opcional:

- `X-Mock-Scenario: success`
- `X-Mock-Scenario: retry-success`
- `X-Mock-Scenario: error`

Si no se envia el header, responde el escenario exitoso por defecto.

## Reinicio de escenarios

Para reiniciar el estado de WireMock:

```bash
curl -X POST http://localhost:8081/__admin/scenarios/reset
```

O bien:

```bash
./scripts/reset_mock_scenarios.sh
```
