package tenpo.domain.history;

import java.util.List;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import tenpo.domain.history.model.HistoryPage;
import tenpo.domain.history.model.HistoryQuery;

@Service
public class DefaultHistoryQueryService implements HistoryQueryService {

    @Override
    public Mono<HistoryPage> findHistory(HistoryQuery historyQuery) {
        return Mono.just(new HistoryPage(List.of(), historyQuery.page(), historyQuery.size(), 0, 0));
    }
}
