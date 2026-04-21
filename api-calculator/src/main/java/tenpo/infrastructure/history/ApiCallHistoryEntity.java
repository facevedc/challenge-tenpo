package tenpo.infrastructure.history;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "api_call_history", schema = "tenpo")
public class ApiCallHistoryEntity {

    @Id
    private Long id;

    @Column("created_at")
    private LocalDateTime createdAt;

    private String endpoint;

    @Column("http_method")
    private String httpMethod;

    @Column("query_params")
    private String queryParams;

    @Column("request_body")
    private String requestBody;

    @Column("response_status")
    private Integer responseStatus;

    @Column("response_body")
    private String responseBody;

    @Column("error_message")
    private String errorMessage;

    @Column("duration_ms")
    private Long durationMs;
}
