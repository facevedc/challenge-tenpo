package tenpo.api.history.mapper;

import org.springframework.stereotype.Component;
import tenpo.api.history.dto.HistoryApiRequest;
import tenpo.domain.history.model.HistoryQuery;

@Component
public class HistoryDomainMapper {

    public HistoryQuery toModel(HistoryApiRequest request) {
        return new HistoryQuery(request.getPage(), request.getSize());
    }
}

