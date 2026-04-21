package tenpo.infrastructure.client.mock;

import java.util.Optional;
import reactor.util.context.ContextView;
import tenpo.settings.HttpHeaderConstants;

public final class MockScenarioContext {

    private MockScenarioContext() {
    }

    public static Optional<String> getScenario(ContextView contextView) {
        return contextView.getOrEmpty(HttpHeaderConstants.MOCK_SCENARIO)
                .map(String.class::cast)
                .filter(value -> !value.isBlank());
    }
}
