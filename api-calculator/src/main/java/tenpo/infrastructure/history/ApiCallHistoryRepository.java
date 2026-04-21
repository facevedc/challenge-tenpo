package tenpo.infrastructure.history;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ApiCallHistoryRepository extends ReactiveCrudRepository<ApiCallHistoryEntity, Long> {

    @Query("""
            SELECT id, created_at, endpoint, http_method, query_params, request_body, response_status, response_body, error_message, duration_ms
            FROM tenpo.api_call_history
            WHERE endpoint = '/api/v1/calculations'
            ORDER BY created_at DESC, id DESC
            LIMIT :limit OFFSET :offset
            """)
    Flux<ApiCallHistoryEntity> findPage(@Param("limit") int limit, @Param("offset") long offset);

    @Query("SELECT COUNT(*) FROM tenpo.api_call_history WHERE endpoint = '/api/v1/calculations'")
    Mono<Long> countAllEntries();
}
