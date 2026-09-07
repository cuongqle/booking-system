-- Generic bookable resources (rooms, desks, equipment, etc.)
CREATE TABLE resources (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    type VARCHAR(50) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

INSERT INTO resources (id, name, description, type, active, created_at, updated_at) VALUES
    ('A101', 'Harbor Meeting A101', 'Quiet meeting room for small teams', 'MEETING_ROOM', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('A102', 'Harbor Meeting A102', 'Corner meeting room near the lobby', 'MEETING_ROOM', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('B201', 'Garden Desk B201', 'Hot desk with garden view', 'DESK', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('B202', 'Garden Desk B202', 'Dedicated desk with dual monitors', 'DESK', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('C301', 'AV Kit C301', 'Portable projector and speaker set', 'EQUIPMENT', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('C302', 'AV Kit C302', 'Conference camera and mic kit', 'EQUIPMENT', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

ALTER TABLE bookings RENAME COLUMN room_id TO resource_id;

ALTER TABLE bookings
    ADD CONSTRAINT fk_bookings_resource
    FOREIGN KEY (resource_id) REFERENCES resources (id);
