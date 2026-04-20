# postgres

Contiene la inicializacion dockerizada de PostgreSQL para el challenge.

## Objetivo

Persistir el historial de llamadas de la API principal.

## Inicializacion

Los scripts SQL en `postgres/init` se ejecutan automaticamente cuando el contenedor se crea por primera vez.

## Credenciales por defecto

- Base de datos: `tenpo_challenge`
- Usuario: `tenpo`
- Password: `tenpo_secret`

Estas credenciales estan pensadas para entorno local y pueden externalizarse por variables de entorno.

