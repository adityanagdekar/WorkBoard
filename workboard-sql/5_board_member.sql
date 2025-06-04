
CREATE TABLE board_member (
    board_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    PRIMARY KEY (board_id, user_id),
    CONSTRAINT fk_board_member_board FOREIGN KEY (board_id) REFERENCES board(id) ON DELETE CASCADE,
    CONSTRAINT fk_board_member_user FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE
);
