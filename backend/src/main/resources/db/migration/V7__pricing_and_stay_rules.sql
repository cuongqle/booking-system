ALTER TABLE resources ADD COLUMN price_per_hour NUMERIC(12, 2) NOT NULL DEFAULT 0;
ALTER TABLE resources ADD COLUMN currency VARCHAR(3) NOT NULL DEFAULT 'USD';
ALTER TABLE resources ADD COLUMN min_duration_minutes INTEGER NOT NULL DEFAULT 30;
ALTER TABLE resources ADD COLUMN max_duration_minutes INTEGER;
ALTER TABLE resources ADD COLUMN buffer_minutes INTEGER NOT NULL DEFAULT 0;

ALTER TABLE resources ADD CONSTRAINT chk_resources_price_per_hour CHECK (price_per_hour >= 0);
ALTER TABLE resources ADD CONSTRAINT chk_resources_min_duration CHECK (min_duration_minutes >= 1);
ALTER TABLE resources ADD CONSTRAINT chk_resources_max_duration CHECK (
    max_duration_minutes IS NULL OR max_duration_minutes >= min_duration_minutes
);
ALTER TABLE resources ADD CONSTRAINT chk_resources_buffer CHECK (buffer_minutes >= 0);

UPDATE resources
SET price_per_hour = CASE type
        WHEN 'MEETING_ROOM' THEN 40.00
        WHEN 'DESK' THEN 15.00
        WHEN 'EQUIPMENT' THEN 25.00
        ELSE 10.00
    END,
    min_duration_minutes = 30,
    max_duration_minutes = CASE type
        WHEN 'MEETING_ROOM' THEN 480
        ELSE 240
    END,
    buffer_minutes = CASE type
        WHEN 'MEETING_ROOM' THEN 15
        ELSE 0
    END;

ALTER TABLE bookings ADD COLUMN total_amount NUMERIC(12, 2) NOT NULL DEFAULT 0;
ALTER TABLE bookings ADD COLUMN currency VARCHAR(3) NOT NULL DEFAULT 'USD';
