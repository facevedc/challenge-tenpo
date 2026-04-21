package tenpo.domain.history;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import tenpo.domain.history.model.HistoryLogCommand;

@Slf4j
@Service
public class DefaultHistoryRegistrationService implements HistoryRegistrationService {

    private static final Scheduler HISTORY_REGISTRATION_SCHEDULER =
            Schedulers.newSingle("history-registration");

    private final HistoryPersistence historyPersistence;

    public DefaultHistoryRegistrationService(HistoryPersistence historyPersistence) {
        this.historyPersistence = historyPersistence;
    }

    @Override
    public void register(HistoryLogCommand historyLogCommand) {
        log.info("Starting async history registration for endpoint={} status={}",
                historyLogCommand.endpoint(), historyLogCommand.responseStatus());

        historyPersistence.save(historyLogCommand)
                .subscribeOn(HISTORY_REGISTRATION_SCHEDULER)
                .doOnSuccess(ignored -> log.info("Completed async history registration for endpoint={} status={}",
                        historyLogCommand.endpoint(), historyLogCommand.responseStatus()))
                .onErrorResume(throwable -> {
                    log.warn("Failed async history registration for endpoint={} status={}",
                            historyLogCommand.endpoint(), historyLogCommand.responseStatus(), throwable);
                    return Mono.empty();
                })
                .subscribe();
    }
}
