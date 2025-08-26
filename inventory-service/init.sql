CREATE TABLE IF NOT EXISTS inventories (
    id SERIAL PRIMARY KEY,
    product_id INT UNIQUE NOT NULL,
    quantity INT DEFAULT 0
);