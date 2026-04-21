package tenpo.domain.history;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import reactor.core.publisher.Mono;
import tenpo.domain.history.model.HistoryQuery;
import tenpo.domain.history.model.HistoryEntry;
import tenpo.domain.history.model.HistoryPage;

class DefaultHistoryQueryServiceTest {

    private final HistoryPersistence historyPersistence = mock(HistoryPersistence.class);
    private final DefaultHistoryQueryService historyQueryService = new DefaultHistoryQueryService(historyPersistence);

    @Test
    void shouldReturnHistoryPageFromPersistence() {
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

        when(historyPersistence.findHistory(new HistoryQuery(0, 20))).thenReturn(Mono.just(historyPage));

        StepVerifier.create(
                historyQueryService.findHistory(
                        new HistoryQuery(0, 20))
                )
                .expectNextMatches(page -> !page.getItems().isEmpty()
                        && page.getPage() == 0
                        && page.getSize() == 20
                        && page.getTotalElements() == 1
                        && page.getTotalPages() == 1)
                .verifyComplete();
    }
}
