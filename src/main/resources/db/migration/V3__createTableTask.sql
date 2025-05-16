CREATE TABLE Task (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    statement varchar(255) NOT NULL,
    `order` int NOT NULL,
    opentext TEXT DEFAULT NULL,
    course_id bigint(20) NOT NULL,
    type enum('OPENTEXT', 'SINGLECHOICE', 'MULTIPLECHOICE') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    createdAt datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT FK_task_course FOREIGN KEY (course_id) REFERENCES Course(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;