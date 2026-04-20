# Infrastructure

Esta capa queda reservada para adaptadores concretos de integración externa.

En las próximas iteraciones deberían vivir aquí componentes como:

- `MockClient` para el porcentaje externo
- `RedisClient` para cache y rate limit distribuido
- `PostgresRepository` para historial de llamadas

En esta rama base todavía no se implementan esos adaptadores, por eso los casos de uso bootstrap viven en `domain`.
