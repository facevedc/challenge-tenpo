package tenpo.infrastructure.history;

import java.time.ZoneOffset;
import java.util.List;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import tenpo.domain.history.HistoryPersistence;
import tenpo.domain.history.model.HistoryEntry;
import tenpo.domain.history.model.HistoryLogCommand;
import tenpo.domain.history.model.HistoryPage;
import tenpo.domain.history.model.HistoryQuery;

@Component
public class PostgresHistoryPersistence implements HistoryPersistence {

    private final ApiCallHistoryRepository apiCallHistoryRepository;

    public PostgresHistoryPersistence(ApiCallHistoryRepository apiCallHistoryRepository) {
        this.apiCallHistoryRepository = apiCallHistoryRepository;
    }

    @Override
    public Mono<Void> save(HistoryLogCommand historyLogCommand) {
        return apiCallHistoryRepository.save(toEntity(historyLogCommand)).then();
    }

    @Override
    public Mono<HistoryPage> findHistory(HistoryQuery historyQuery) {
        long offset = (long) historyQuery.page() * historyQuery.size();

        return Mono.zip(
                apiCallHistoryRepository.findPage(historyQuery.size(), offset)
                        .map(this::toModel)
                        .collectList(),
                apiCallHistoryRepository.countAllEntries().defaultIfEmpty(0L)
        ).map(tuple -> toPage(tuple.getT1(), historyQuery, tuple.getT2()));
    }

    private ApiCallHistoryEntity toEntity(HistoryLogCommand historyLogCommand) {
        return new ApiCallHistoryEntity(
                null,
                historyLogCommand.createdAt().toLocalDateTime(),
                historyLogCommand.endpoint(),
                historyLogCommand.httpMethod(),
                historyLogCommand.queryParams(),
                null,
                historyLogCommand.responseStatus(),
                historyLogCommand.responseBody(),
                historyLogCommand.errorMessage(),
                historyLogCommand.durationMs()
        );
    }

    private HistoryEntry toModel(ApiCallHistoryEntity entity) {
        return new HistoryEntry(
                entity.getId(),
                entity.getCreatedAt().atOffset(ZoneOffset.UTC),
                entity.getEndpoint(),
                entity.getHttpMethod(),
                entity.getQueryParams(),
                entity.getRequestBody(),
                entity.getResponseStatus(),
                entity.getResponseBody(),
                entity.getErrorMessage(),
                entity.getDurationMs()
        );
    }

    private HistoryPage toPage(List<HistoryEntry> items, HistoryQuery historyQuery, long totalElements) {
        int totalPages = totalElements == 0
                ? 0
                : (int) Math.ceil((double) totalElements / historyQuery.size());

        return new HistoryPage(items, historyQuery.page(), historyQuery.size(), totalElements, totalPages);
    }
}
