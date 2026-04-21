CREATE SCHEMA IF NOT EXISTS tenpo;

CREATE TABLE IF NOT EXISTS tenpo.api_call_history (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    endpoint VARCHAR(255) NOT NULL,
    http_method VARCHAR(16) NOT NULL,
    query_params TEXT,
    request_body TEXT,
    response_status INTEGER NOT NULL,
    response_body TEXT,
    error_message TEXT,
    duration_ms BIGINT
);

CREATE INDEX IF NOT EXISTS idx_api_call_history_created_at
    ON tenpo.api_call_history (created_at DESC);
