CREATE TABLE IF NOT EXISTS reviews (
    id BIGSERIAL PRIMARY KEY,
    request_id VARCHAR(128) NOT NULL UNIQUE,
    repository VARCHAR(512) NOT NULL,
    pr_number INTEGER NOT NULL,
    dedupe_key VARCHAR(256) NOT NULL,
    quality_score INTEGER NOT NULL,
    model_used VARCHAR(128) NOT NULL,
    summary VARCHAR(4000),
    normalized_diff TEXT NOT NULL,
    result_json TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_reviews_repository ON reviews(repository);
CREATE INDEX IF NOT EXISTS idx_reviews_created_at ON reviews(created_at DESC);
