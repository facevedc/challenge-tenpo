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

- escenario exitoso con porcentaje fijo
- escenario de error para validar retries y fallback a cache

