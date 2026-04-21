package tenpo.domain.history;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import tenpo.domain.history.model.HistoryPage;
import tenpo.domain.history.model.HistoryQuery;

@Service
public class DefaultHistoryQueryService implements HistoryQueryService {

    private final HistoryPersistence historyPersistence;

    public DefaultHistoryQueryService(HistoryPersistence historyPersistence) {
        this.historyPersistence = historyPersistence;
    }

    @Override
    public Mono<HistoryPage> findHistory(HistoryQuery historyQuery) {
        return historyPersistence.findHistory(historyQuery);
    }
}
