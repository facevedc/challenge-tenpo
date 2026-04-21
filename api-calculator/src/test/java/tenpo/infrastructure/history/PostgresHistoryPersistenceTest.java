package tenpo.infrastructure.history;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tenpo.domain.history.model.HistoryLogCommand;
import tenpo.domain.history.model.HistoryQuery;

class PostgresHistoryPersistenceTest {

    private final ApiCallHistoryRepository apiCallHistoryRepository = mock(ApiCallHistoryRepository.class);
    private final PostgresHistoryPersistence postgresHistoryPersistence =
            new PostgresHistoryPersistence(apiCallHistoryRepository);

    @Test
    void shouldPersistHistoryLogCommand() {
        when(apiCallHistoryRepository.save(any(ApiCallHistoryEntity.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(postgresHistoryPersistence.save(new HistoryLogCommand(
                        OffsetDateTime.parse("2026-04-19T10:15:30Z"),
                        "/api/v1/calculations",
                        "GET",
                        "{\"num1\":\"5\",\"num2\":\"7\"}",
                        200,
                        "{\"final_amount\":13.2}",
                        null,
                        15L
                )))
                .verifyComplete();
    }

    @Test
    void shouldReturnPagedHistoryFromRepository() {
        ApiCallHistoryEntity apiCallHistoryEntity = new ApiCallHistoryEntity(
                1L,
                java.time.LocalDateTime.parse("2026-04-19T10:15:30"),
                "/api/v1/calculations",
                "GET",
                "{\"num1\":\"5\",\"num2\":\"7\"}",
                null,
                200,
                "{\"final_amount\":13.2}",
                null,
                15L
        );

        when(apiCallHistoryRepository.findPage(20, 0))
                .thenReturn(Flux.fromIterable(List.of(apiCallHistoryEntity)));
        when(apiCallHistoryRepository.countAllEntries()).thenReturn(Mono.just(1L));

        StepVerifier.create(postgresHistoryPersistence.findHistory(new HistoryQuery(0, 20)))
                .assertNext(historyPage -> {
                    assertEquals(1, historyPage.getItems().size());
                    assertEquals(1L, historyPage.getTotalElements());
                    assertEquals(1, historyPage.getTotalPages());
                    assertEquals("/api/v1/calculations", historyPage.getItems().get(0).getEndpoint());
                    assertEquals(200, historyPage.getItems().get(0).getResponseStatus());
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyPagedHistoryWhenRepositoryHasNoEntries() {
        when(apiCallHistoryRepository.findPage(20, 0)).thenReturn(Flux.empty());
        when(apiCallHistoryRepository.countAllEntries()).thenReturn(Mono.just(0L));

        StepVerifier.create(postgresHistoryPersistence.findHistory(new HistoryQuery(0, 20)))
                .assertNext(historyPage -> {
                    assertEquals(0, historyPage.getItems().size());
                    assertEquals(0L, historyPage.getTotalElements());
                    assertEquals(0, historyPage.getTotalPages());
                })
                .verifyComplete();
    }
}
