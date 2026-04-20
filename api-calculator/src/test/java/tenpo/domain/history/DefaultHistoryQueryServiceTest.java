package tenpo.domain.history;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import tenpo.domain.history.model.HistoryQuery;

class DefaultHistoryQueryServiceTest {

    private final DefaultHistoryQueryService historyQueryService = new DefaultHistoryQueryService();

    @Test
    void shouldReturnEmptyHistoryPageDuringBootstrap() {
        StepVerifier.create(
                historyQueryService.findHistory(
                        new HistoryQuery(0, 20))
                )
                .expectNextMatches(page -> page.getItems().isEmpty()
                        && page.getPage() == 0
                        && page.getSize() == 20
                        && page.getTotalElements() == 0
                        && page.getTotalPages() == 0)
                .verifyComplete();
    }
}
