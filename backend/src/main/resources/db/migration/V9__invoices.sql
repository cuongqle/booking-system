CREATE TABLE invoices (
    id BIGSERIAL PRIMARY KEY,
    booking_id BIGINT NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    amount NUMERIC(12, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    status VARCHAR(20) NOT NULL,
    method VARCHAR(20) NOT NULL DEFAULT 'STUB',
    paid_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_invoices_booking FOREIGN KEY (booking_id) REFERENCES bookings (id),
    CONSTRAINT fk_invoices_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT chk_invoices_amount CHECK (amount >= 0),
    CONSTRAINT chk_invoices_status CHECK (status IN ('UNPAID', 'PAID', 'CANCELLED', 'REFUNDED'))
);

CREATE INDEX idx_invoices_user_created ON invoices (user_id, created_at DESC);

INSERT INTO invoices (booking_id, user_id, amount, currency, status, method, paid_at, created_at, updated_at)
SELECT
    b.id,
    b.user_id,
    b.total_amount,
    b.currency,
    CASE
        WHEN b.status = 'CONFIRMED' THEN 'PAID'
        WHEN b.status = 'CANCELLED' THEN 'CANCELLED'
        WHEN b.status = 'COMPLETED' THEN 'PAID'
        ELSE 'UNPAID'
    END,
    'STUB',
    CASE
        WHEN b.status IN ('CONFIRMED', 'COMPLETED') THEN b.updated_at
        ELSE NULL
    END,
    b.created_at,
    b.updated_at
FROM bookings b;
