package tenpo.api.history.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.stereotype.Component;
import tenpo.api.history.dto.HistoryEntryResponse;
import tenpo.api.history.dto.HistoryResponse;
import tenpo.domain.history.model.HistoryEntry;
import tenpo.domain.history.model.HistoryPage;

@Component
public class HistoryResponseMapper {

    private final ObjectMapper objectMapper;

    public HistoryResponseMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

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
                parseJson(entry.getQueryParams()),
                parseJson(entry.getRequestBody()),
                entry.getResponseStatus(),
                parseJson(entry.getResponseBody()),
                entry.getErrorMessage(),
                entry.getDurationMs()
        );
    }

    private Object parseJson(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            JsonNode jsonNode = objectMapper.readTree(value);
            if (jsonNode.isNull()) {
                return null;
            }
            return jsonNode;
        } catch (JsonProcessingException exception) {
            return value;
        }
    }
}
