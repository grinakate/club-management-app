CREATE SCHEMA club_management;
ALTER DATABASE club_management SET search_path TO club_management;
ALTER SCHEMA club_management OWNER TO club_management;

CREATE TYPE user_role AS ENUM ('participant', 'admin', 'manager');
CREATE TYPE user_status AS ENUM ('active', 'blocked', 'pending');
CREATE TYPE club_status AS ENUM ('draft', 'active', 'archived');
CREATE TYPE application_status AS ENUM ('new', 'approved', 'rejected', 'cancelled');
CREATE TYPE membership_role AS ENUM ('member', 'moderator', 'admin');
CREATE TYPE membership_status AS ENUM ('active', 'suspended', 'left');
CREATE TYPE event_status AS ENUM ('draft', 'published', 'cancelled', 'completed');
CREATE TYPE registration_status AS ENUM ('registered', 'cancelled', 'waitlist', 'attended');
CREATE TYPE notification_channel AS ENUM ('in_app', 'email', 'telegram');
CREATE TYPE delivery_status AS ENUM ('queued', 'sent', 'delivered', 'failed');

CREATE TABLE "user"
(
    user_id       BIGINT NOT NULL PRIMARY KEY,
    full_name     VARCHAR(150),
    email         VARCHAR(255),
    phone         VARCHAR(20),
    password_hash VARCHAR(255),
    city          VARCHAR(100),
    interests     TEXT,
    role          user_role,
    status        user_status
);
CREATE SEQUENCE user_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE club
(
    club_id        BIGINT NOT NULL PRIMARY KEY,
    name           VARCHAR(200),
    description    TEXT,
    category       VARCHAR(100),
    age_limit_min  INTEGER,
    age_limit_max  INTEGER,
    membership_fee DECIMAL(10, 2),
    owner_user_id  BIGINT,
    status         club_status,
    CONSTRAINT fk_club_owner_user_id
        FOREIGN KEY (owner_user_id)
            REFERENCES "user" (user_id)
);
CREATE SEQUENCE club_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE membership_application
(
    application_id BIGINT NOT NULL PRIMARY KEY,
    club_id        BIGINT,
    user_id        BIGINT,
    applied_at     TIMESTAMP,
    status         application_status,
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
    membership_id BIGINT NOT NULL PRIMARY KEY,
    club_id       BIGINT,
    user_id       BIGINT,
    joined_at     TIMESTAMP,
    member_role   membership_role,
    status        membership_status,
    CONSTRAINT fk_membership_club_id
        FOREIGN KEY (club_id)
            REFERENCES club (club_id),
    CONSTRAINT fk_membership_user_id
        FOREIGN KEY (user_id)
            REFERENCES "user" (user_id)
);
CREATE SEQUENCE membership_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE venue
(
    venue_id BIGINT NOT NULL PRIMARY KEY,
    name     VARCHAR(150),
    address  VARCHAR(255),
    room     VARCHAR(100),
    capacity INTEGER,
    comment  VARCHAR(500)
);
CREATE SEQUENCE venue_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE event
(
    event_id          BIGINT NOT NULL PRIMARY KEY,
    club_id           BIGINT,
    venue_id          BIGINT,
    created_by        TIMESTAMP,
    title             VARCHAR(200),
    description       TEXT,
    start_at          TIMESTAMP,
    end_at            TIMESTAMP,
    participant_limit INTEGER,
    price             DECIMAL(10, 2),
    is_paid           BOOLEAN,
    is_recurring      BOOLEAN,
    status            event_status,
    CONSTRAINT fk_event_club_id
        FOREIGN KEY (club_id)
            REFERENCES club (club_id),
    CONSTRAINT fk_event_venue_id
        FOREIGN KEY (venue_id)
            REFERENCES venue (venue_id)
);
CREATE SEQUENCE event_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE event_registration
(
    registration_id BIGINT NOT NULL PRIMARY KEY,
    event_id        BIGINT,
    user_id         BIGINT,
    registered_at   TIMESTAMP,
    status          membership_status,
    attendance_mark BOOLEAN,
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
    notification_id BIGINT NOT NULL PRIMARY KEY,
    user_id         BIGINT,
    event_id        BIGINT,
    channel         BIGINT,
    subject         VARCHAR(200),
    message_text    TEXT,
    scheduled_at    TIMESTAMP,
    sent_at         TIMESTAMP,
    delivery_status membership_status,
    CONSTRAINT fk_notification_event_id
        FOREIGN KEY (event_id)
            REFERENCES event (event_id),
    CONSTRAINT fk_notification_user_id
        FOREIGN KEY (user_id)
            REFERENCES "user" (user_id)
);
CREATE SEQUENCE notification_seq START WITH 1 INCREMENT BY 1;

