package tenpo.settings;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import tenpo.api.calculation.handler.CalculationHandler;
import tenpo.api.history.handler.HistoryHandler;

@Configuration
public class RouterConfig {

    @Bean
    public RouterFunction<ServerResponse> calculationRoutes(CalculationHandler calculationHandler) {
        return route(GET("/api/v1/calculations")
                .and(request -> request.headers().accept().isEmpty()
                        || request.headers().accept().stream()
                        .anyMatch(mediaType -> mediaType.isCompatibleWith(MediaType.APPLICATION_JSON))), calculationHandler::calculate);
    }

    @Bean
    public RouterFunction<ServerResponse> historyRoutes(HistoryHandler historyHandler) {
        return route(GET("/api/v1/history")
                .and(request -> request.headers().accept().isEmpty()
                        || request.headers().accept().stream()
                        .anyMatch(mediaType -> mediaType.isCompatibleWith(MediaType.APPLICATION_JSON))), historyHandler::getHistory);
    }
}
