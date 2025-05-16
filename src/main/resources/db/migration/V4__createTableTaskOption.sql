CREATE TABLE task_option (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    `option` varchar(255) NOT NULL,
    isCorrect BOOLEAN NOT NULL,
    task_id  bigint(20) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT FK_task FOREIGN KEY (task_id) REFERENCES Task(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;