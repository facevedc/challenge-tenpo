package tenpo.domain.history;

import reactor.core.publisher.Mono;
import tenpo.domain.history.model.HistoryPage;
import tenpo.domain.history.model.HistoryQuery;

public interface HistoryQueryService {

    Mono<HistoryPage> findHistory(HistoryQuery historyQuery);
}
