
CREATE TABLE card_member (
    card_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    PRIMARY KEY (card_id, user_id),
    CONSTRAINT fk_card_member_card FOREIGN KEY (card_id) REFERENCES task_card(id) ON DELETE CASCADE,
    CONSTRAINT fk_card_member_user FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE
);
