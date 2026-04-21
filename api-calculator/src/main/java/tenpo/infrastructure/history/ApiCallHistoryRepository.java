package tenpo.infrastructure.history;

import java.time.LocalDateTime;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ApiCallHistoryRepository extends ReactiveCrudRepository<ApiCallHistoryEntity, Long> {

    @Modifying
    @Query("""
            INSERT INTO tenpo.api_call_history (
                created_at,
                endpoint,
                http_method,
                query_params,
                request_body,
                response_status,
                response_body,
                error_message,
                duration_ms
            ) VALUES (
                :createdAt,
                :endpoint,
                :httpMethod,
                :queryParams,
                :requestBody,
                :responseStatus,
                :responseBody,
                :errorMessage,
                :durationMs
            )
            """)
    Mono<Integer> insertHistory(
            @Param("createdAt") LocalDateTime createdAt,
            @Param("endpoint") String endpoint,
            @Param("httpMethod") String httpMethod,
            @Param("queryParams") String queryParams,
            @Param("requestBody") String requestBody,
            @Param("responseStatus") Integer responseStatus,
            @Param("responseBody") String responseBody,
            @Param("errorMessage") String errorMessage,
            @Param("durationMs") Long durationMs
    );

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
