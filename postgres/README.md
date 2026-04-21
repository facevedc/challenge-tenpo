# postgres

Inicializacion dockerizada de PostgreSQL para el challenge.

## Objetivo

Persistir el historial de llamadas registradas por `api-calculator`.

## Inicializacion

Los scripts SQL dentro de `postgres/init` se ejecutan automaticamente cuando el volumen se crea por primera vez.

Bootstrap actual:

- base de datos `tenpo_challenge`
- usuario `tenpo`
- schema `tenpo`
- tabla `tenpo.api_call_history`

## Credenciales locales

- Base de datos: `tenpo_challenge`
- Usuario: `tenpo`
- Password: `tenpo_secret`
- Puerto: `5432`

## Limpieza de historial

```bash
./scripts/reset_history.sh
```

## Verificacion rapida

```bash
docker compose exec -T postgres sh -lc \
  "PGPASSWORD=tenpo_secret psql -U tenpo -d tenpo_challenge -c 'SELECT count(*) FROM tenpo.api_call_history;'"
```
