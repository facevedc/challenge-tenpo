# Infrastructure

Esta capa queda reservada para adaptadores concretos de integración externa.

En esta iteración ya vive aquí:

- `MockPercentageClient` para consumir el mock externo del porcentaje dinámico
- `RedisPercentageCacheStore` para persistir el porcentaje distribuido en Redis con TTL

En las próximas iteraciones deberían agregarse además:

- `PostgresRepository` para historial de llamadas
