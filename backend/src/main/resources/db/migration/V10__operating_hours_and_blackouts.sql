ALTER TABLE resources ADD COLUMN open_time TIME;
ALTER TABLE resources ADD COLUMN close_time TIME;

UPDATE resources
SET open_time = CASE type
        WHEN 'MEETING_ROOM' THEN TIME '08:00:00'
        WHEN 'DESK' THEN TIME '07:00:00'
        ELSE TIME '09:00:00'
    END,
    close_time = CASE type
        WHEN 'MEETING_ROOM' THEN TIME '20:00:00'
        WHEN 'DESK' THEN TIME '19:00:00'
        ELSE TIME '18:00:00'
    END;

CREATE TABLE resource_blackouts (
    id BIGSERIAL PRIMARY KEY,
    resource_id VARCHAR(64) NOT NULL,
    start_at TIMESTAMP NOT NULL,
    end_at TIMESTAMP NOT NULL,
    reason VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_blackouts_resource FOREIGN KEY (resource_id) REFERENCES resources (id),
    CONSTRAINT chk_blackouts_range CHECK (start_at < end_at)
);

CREATE INDEX idx_blackouts_resource_range ON resource_blackouts (resource_id, start_at, end_at);
