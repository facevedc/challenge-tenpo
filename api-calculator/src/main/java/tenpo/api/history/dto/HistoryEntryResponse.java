package tenpo.api.history.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
@AllArgsConstructor
public class HistoryEntryResponse {

    private final Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private final OffsetDateTime createdAt;
    private final String endpoint;
    private final String httpMethod;
    private final Object queryParams;
    private final Object requestBody;
    private final Integer responseStatus;
    private final Object responseBody;
    private final String errorMessage;
    private final Long durationMs;
}
