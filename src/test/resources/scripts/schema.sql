DROP SCHEMA PUBLIC CASCADE;

CREATE TABLE owner
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    first_name VARCHAR(255)                            NOT NULL,
    last_name  VARCHAR(255)                            NOT NULL,
    address    VARCHAR(255)                            NOT NULL,
    city       VARCHAR(255)                            NOT NULL,
    email      VARCHAR(255),
    telephone  VARCHAR(255),
    CONSTRAINT pk_owner PRIMARY KEY (id)
);

CREATE TABLE pet
(
    id                    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    identification_number VARCHAR(255)                            NOT NULL,
    birth_date            date,
    type_id               BIGINT,
    owner_id              BIGINT,
    CONSTRAINT pk_pet PRIMARY KEY (id)
);

CREATE TABLE pet_type
(
    id   BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(255)                            NOT NULL,
    CONSTRAINT pk_pet_type PRIMARY KEY (id)
);

CREATE TABLE visit
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    pet_id      BIGINT                                  NOT NULL,
    visit_start TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    visit_end   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    description VARCHAR(4000),
    CONSTRAINT pk_visit PRIMARY KEY (id)
);

ALTER TABLE pet
    ADD CONSTRAINT FK_PET_ON_OWNER FOREIGN KEY (owner_id) REFERENCES owner (id);

ALTER TABLE pet
    ADD CONSTRAINT FK_PET_ON_TYPE FOREIGN KEY (type_id) REFERENCES pet_type (id);

ALTER TABLE visit
    ADD CONSTRAINT FK_VISIT_ON_PET FOREIGN KEY (pet_id) REFERENCES pet (id);