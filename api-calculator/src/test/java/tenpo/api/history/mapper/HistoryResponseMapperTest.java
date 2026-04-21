package tenpo.api.history.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import tenpo.api.history.dto.HistoryResponse;
import tenpo.domain.history.model.HistoryEntry;
import tenpo.domain.history.model.HistoryPage;
import tenpo.settings.JacksonConfig;

class HistoryResponseMapperTest {

    private final ObjectMapper objectMapper = new JacksonConfig().objectMapper();
    private final HistoryResponseMapper historyResponseMapper = new HistoryResponseMapper(objectMapper);

    @Test
    void shouldMapJsonFieldsAsStructuredObjects() {
        HistoryPage historyPage = new HistoryPage(
                List.of(new HistoryEntry(
                        1L,
                        OffsetDateTime.parse("2026-04-19T10:15:30Z"),
                        "/api/v1/calculations",
                        "GET",
                        "{\"num1\":\"5\",\"num2\":\"7\"}",
                        null,
                        200,
                        "{\"final_amount\":13.2}",
                        null,
                        15L
                )),
                0,
                20,
                1,
                1
        );

        HistoryResponse response = historyResponseMapper.toResponse(historyPage);

        JsonNode queryParams = (JsonNode) response.getItems().get(0).getQueryParams();
        JsonNode responseBody = (JsonNode) response.getItems().get(0).getResponseBody();

        assertEquals("5", queryParams.get("num1").asText());
        assertEquals(13.2d, responseBody.get("final_amount").asDouble());
        assertNull(response.getItems().get(0).getRequestBody());
    }

    @Test
    void shouldFallbackToStringWhenStoredValueIsNotValidJson() {
        HistoryPage historyPage = new HistoryPage(
                List.of(new HistoryEntry(
                        1L,
                        OffsetDateTime.parse("2026-04-19T10:15:30Z"),
                        "/api/v1/calculations",
                        "GET",
                        "num1=5&num2=7",
                        "plain request",
                        400,
                        "plain response",
                        "error",
                        10L
                )),
                0,
                20,
                1,
                1
        );

        HistoryResponse response = historyResponseMapper.toResponse(historyPage);

        assertTrue(response.getItems().get(0).getQueryParams() instanceof String);
        assertEquals("num1=5&num2=7", response.getItems().get(0).getQueryParams());
        assertEquals("plain request", response.getItems().get(0).getRequestBody());
        assertEquals("plain response", response.getItems().get(0).getResponseBody());
    }

    @Test
    void shouldReturnNullWhenStoredValuesAreBlankOrJsonNull() {
        HistoryPage historyPage = new HistoryPage(
                List.of(new HistoryEntry(
                        1L,
                        OffsetDateTime.parse("2026-04-19T10:15:30Z"),
                        "/api/v1/calculations",
                        "GET",
                        " ",
                        "null",
                        200,
                        null,
                        null,
                        10L
                )),
                0,
                20,
                1,
                1
        );

        HistoryResponse response = historyResponseMapper.toResponse(historyPage);

        assertNull(response.getItems().get(0).getQueryParams());
        assertNull(response.getItems().get(0).getRequestBody());
        assertNull(response.getItems().get(0).getResponseBody());
    }
}
