package tenpo.domain.history.model;

import java.time.OffsetDateTime;

public record HistoryLogCommand(
        OffsetDateTime createdAt,
        String endpoint,
        String httpMethod,
        String queryParams,
        Integer responseStatus,
        String responseBody,
        String errorMessage,
        Long durationMs
) {
}
