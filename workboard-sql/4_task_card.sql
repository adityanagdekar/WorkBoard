
CREATE TABLE task_card (
    id SERIAL PRIMARY KEY,
    list_id INTEGER NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    is_completed BOOLEAN DEFAULT FALSE,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_card_list FOREIGN KEY (list_id) REFERENCES board_list(id) ON DELETE CASCADE
);
