# Infrastructure

Esta capa queda reservada para adaptadores concretos de integración externa.

En esta iteración ya vive aquí:

- `MockPercentageClient` para consumir el mock externo del porcentaje dinámico

En las próximas iteraciones deberían agregarse además:

- `RedisClient` para cache y rate limit distribuido
- `PostgresRepository` para historial de llamadas
