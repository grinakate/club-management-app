CREATE SCHEMA club_management;
ALTER DATABASE club_management SET search_path TO club_management;
ALTER SCHEMA club_management OWNER TO club_management;

CREATE TYPE user_role AS ENUM ('PARTICIPANT', 'ADMIN', 'MANAGER');
CREATE TYPE user_status AS ENUM ('ACTIVE', 'BLOCKED', 'PENDING');
CREATE TYPE club_status AS ENUM ('DRAFT', 'ACTIVE', 'ARCHIVED');
CREATE TYPE application_status AS ENUM ('NEW', 'APPROVED', 'REJECTED', 'CANCELLED');
CREATE TYPE membership_role AS ENUM ('MEMBER', 'MODERATOR', 'ADMIN');
CREATE TYPE membership_status AS ENUM ('ACTIVE', 'SUSPENDED', 'LEFT');
CREATE TYPE event_status AS ENUM ('DRAFT', 'PUBLISHED', 'CANCELLED', 'COMPLETED');
CREATE TYPE registration_status AS ENUM ('REGISTERED', 'CANCELLED', 'WAITLIST', 'ATTENDED');
CREATE TYPE notification_channel AS ENUM ('IN_APP', 'EMAIL', 'TELEGRAM');
CREATE TYPE delivery_status AS ENUM ('QUEUED', 'SENT', 'DELIVERED', 'FAILED');

CREATE TABLE "user"
(
    user_id       BIGINT       NOT NULL PRIMARY KEY,
    full_name     VARCHAR(150) NOT NULL,
    email         VARCHAR(255) UNIQUE,
    phone         VARCHAR(20) UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    city          VARCHAR(100),
    interests     TEXT,
    role          user_role    NOT NULL,
    status        user_status  NOT NULL,
    birth_date    DATE         NOT NULL,
    created_at    TIMESTAMP    NOT NULL,
    CHECK (email IS NOT NULL or phone IS NOT NULL )
);
CREATE SEQUENCE user_seq START WITH 2 INCREMENT BY 1;

INSERT INTO "user" (user_id, full_name, email, password_hash, role, status, birth_date, created_at)
VALUES (1, 'Администратор', 'admin@club.ru', '$2b$10$nNvWY9bhtwjCPqNPxxyCauhxWhNrTrXHUQf1VSm/dgrOfEX4aolJS', 'ADMIN', 'ACTIVE', '1990-01-01', now());

CREATE TABLE category
(
    id   BIGINT       NOT NULL PRIMARY KEY,
    name VARCHAR(200) NOT NULL UNIQUE
);
INSERT INTO category (id, name)
VALUES (1, 'Спорт и фитнес'),
       (2, 'ИТ и программирование'),
       (3, 'Настольные игры'),
       (4, 'Иностранные языки'),
       (5, 'Творчество и искусство'),
       (6, 'Музыка и вокал'),
       (7, 'Наука и технологии'),
       (8, 'Кино и литература'),
       (9, 'Волонтерство'),
       (10, 'Карьера и бизнес');
CREATE SEQUENCE category_seq START WITH 11 INCREMENT BY 1;

CREATE TABLE club
(
    club_id        BIGINT       NOT NULL PRIMARY KEY,
    name           VARCHAR(200) NOT NULL UNIQUE,
    description    TEXT,
    category_id    BIGINT       NOT NULL,
    age_limit_min  INTEGER,
    age_limit_max  INTEGER,
    membership_fee DECIMAL(10, 2),
    owner_user_id  BIGINT       NOT NULL,
    status         club_status  NOT NULL,
    created_at     TIMESTAMP    NOT NULL,
    CONSTRAINT fk_club_owner_user_id
        FOREIGN KEY (owner_user_id)
            REFERENCES "user" (user_id),
    CONSTRAINT fk_club_category_id
        FOREIGN KEY (category_id)
            REFERENCES category (id)
);
CREATE SEQUENCE club_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE membership_application
(
    application_id BIGINT             NOT NULL PRIMARY KEY,
    club_id        BIGINT             NOT NULL,
    user_id        BIGINT             NOT NULL,
    applied_at     TIMESTAMP,
    status         application_status NOT NULL,
    reviewed_by    BIGINT,
    reviewed_at    TIMESTAMP,
    comment        VARCHAR(500),
    CONSTRAINT fk_application_club_id
        FOREIGN KEY (club_id)
            REFERENCES club (club_id),
    CONSTRAINT fk_application_user_id
        FOREIGN KEY (user_id)
            REFERENCES "user" (user_id),
    CONSTRAINT fk_application_reviewed_by_user_id
        FOREIGN KEY (reviewed_by)
            REFERENCES "user" (user_id)
);
CREATE SEQUENCE application_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE club_membership
(
    membership_id BIGINT            NOT NULL PRIMARY KEY,
    club_id       BIGINT            NOT NULL,
    user_id       BIGINT            NOT NULL,
    joined_at     TIMESTAMP         NOT NULL,
    member_role   membership_role   NOT NULL,
    status        membership_status NOT NULL,
    UNIQUE (club_id, user_id),
    CONSTRAINT fk_membership_club_id
        FOREIGN KEY (club_id)
            REFERENCES club (club_id),
    CONSTRAINT fk_membership_user_id
        FOREIGN KEY (user_id)
            REFERENCES "user" (user_id)
);
CREATE SEQUENCE membership_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE event
(
    event_id          BIGINT       NOT NULL PRIMARY KEY,
    status            event_status NOT NULL,
    club_id           BIGINT       NOT NULL,
    title             VARCHAR(200) NOT NULL,
    description       TEXT,
    start_at          TIMESTAMP    NOT NULL,
    end_at            TIMESTAMP    NOT NULL,
    participant_limit INTEGER,
    price             DECIMAL(10, 2),
    created_by        BIGINT       NOT NULL,
    CONSTRAINT fk_event_club_id
        FOREIGN KEY (club_id)
            REFERENCES club (club_id),
    CONSTRAINT fk_event_created_by
        FOREIGN KEY (created_by)
            REFERENCES "user" (user_id)
);
CREATE SEQUENCE event_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE event_registration
(
    registration_id BIGINT            NOT NULL PRIMARY KEY,
    event_id        BIGINT            NOT NULL,
    user_id         BIGINT            NOT NULL,
    registered_at   TIMESTAMP         NOT NULL,
    status          registration_status NOT NULL,
    attendance_mark BOOLEAN,
    UNIQUE (event_id, user_id),
    CONSTRAINT fk_registration_event_id
        FOREIGN KEY (event_id)
            REFERENCES event (event_id),
    CONSTRAINT fk_registration_user_id
        FOREIGN KEY (user_id)
            REFERENCES "user" (user_id)
);
CREATE SEQUENCE event_registration_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE notification
(
    notification_id BIGINT            NOT NULL PRIMARY KEY,
    user_id         BIGINT            NOT NULL,
    event_id        BIGINT,
    club_id         BIGINT,
    subject         VARCHAR(200)      NOT NULL,
    message_text    TEXT,
    scheduled_at    TIMESTAMP         NOT NULL,
    sent_at         TIMESTAMP,
    channel         notification_channel NOT NULL DEFAULT 'IN_APP',
    is_read         BOOLEAN              NOT NULL DEFAULT false,
    delivery_status delivery_status      NOT NULL,
    CONSTRAINT fk_notification_event_id
        FOREIGN KEY (event_id)
            REFERENCES event (event_id),
    CONSTRAINT fk_notification_user_id
        FOREIGN KEY (user_id)
            REFERENCES "user" (user_id),
    CONSTRAINT fk_notification_club_id
        FOREIGN KEY (club_id)
            REFERENCES club (club_id)
);
CREATE SEQUENCE notification_seq START WITH 1 INCREMENT BY 1;

