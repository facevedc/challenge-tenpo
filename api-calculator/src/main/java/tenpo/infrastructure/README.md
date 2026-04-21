# Infrastructure

Esta capa contiene los adaptadores concretos de integracion externa del servicio.

Hoy vive aqui:

- `MockPercentageClient` para consumir el servicio mock de porcentaje
- `RedisPercentageCacheStore` para persistir el porcentaje distribuido en Redis con TTL
- `PostgresHistoryPersistence` y `ApiCallHistoryRepository` para persistir y consultar historial en PostgreSQL
