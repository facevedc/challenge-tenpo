package tenpo.api.history.mapper;

import java.util.List;
import org.springframework.stereotype.Component;
import tenpo.api.history.dto.HistoryEntryResponse;
import tenpo.api.history.dto.HistoryResponse;
import tenpo.domain.history.model.HistoryEntry;
import tenpo.domain.history.model.HistoryPage;

@Component
public class HistoryResponseMapper {

    public HistoryResponse toResponse(HistoryPage model) {
        List<HistoryEntryResponse> items = model.getItems().stream()
                .map(this::toEntryResponse)
                .toList();
        return new HistoryResponse(items, model.getPage(), model.getSize(), model.getTotalElements(), model.getTotalPages());
    }

    private HistoryEntryResponse toEntryResponse(HistoryEntry entry) {
        return new HistoryEntryResponse(
                entry.getId(),
                entry.getCreatedAt(),
                entry.getEndpoint(),
                entry.getHttpMethod(),
                entry.getRequestSummary(),
                entry.getOutcome()
        );
    }
}
