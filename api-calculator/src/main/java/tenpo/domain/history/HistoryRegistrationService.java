package tenpo.domain.history;

import tenpo.domain.history.model.HistoryLogCommand;

public interface HistoryRegistrationService {

    void register(HistoryLogCommand historyLogCommand);
}
