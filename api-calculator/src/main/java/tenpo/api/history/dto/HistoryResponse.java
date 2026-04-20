package tenpo.api.history.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
@AllArgsConstructor
public class HistoryResponse {

    private final List<HistoryEntryResponse> items;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
}
