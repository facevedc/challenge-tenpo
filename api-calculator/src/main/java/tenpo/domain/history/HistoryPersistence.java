package tenpo.domain.history;

import reactor.core.publisher.Mono;
import tenpo.domain.history.model.HistoryLogCommand;
import tenpo.domain.history.model.HistoryPage;
import tenpo.domain.history.model.HistoryQuery;

public interface HistoryPersistence {

    Mono<Void> save(HistoryLogCommand historyLogCommand);

    Mono<HistoryPage> findHistory(HistoryQuery historyQuery);
}
