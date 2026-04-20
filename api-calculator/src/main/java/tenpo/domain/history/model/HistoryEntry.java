package tenpo.domain.history.model;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HistoryEntry {

    private final Long id;
    private final OffsetDateTime createdAt;
    private final String endpoint;
    private final String httpMethod;
    private final String requestSummary;
    private final String outcome;
}
