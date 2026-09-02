ALTER TABLE bookings
    ADD CONSTRAINT fk_bookings_user
    FOREIGN KEY (user_id) REFERENCES users (id);
