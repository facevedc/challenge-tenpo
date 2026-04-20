package tenpo.api.history.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
@AllArgsConstructor
public class HistoryEntryResponse {

    private final Long id;
    private final OffsetDateTime createdAt;
    private final String endpoint;
    private final String httpMethod;
    private final String requestSummary;
    private final String outcome;
}
