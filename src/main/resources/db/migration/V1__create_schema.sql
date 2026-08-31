CREATE TABLE TB_ROLE
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE TB_USER
(
    id       BIGSERIAL PRIMARY KEY,
    email    VARCHAR(50)  NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    enabled  BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE user_role
(
    user_id BIGINT NOT NULL REFERENCES TB_USER (id),
    role_id BIGINT NOT NULL REFERENCES TB_ROLE (id),
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE TB_DOCTOR
(
    id        BIGSERIAL PRIMARY KEY,
    name      VARCHAR(100) NOT NULL,
    crm       VARCHAR(20)  NOT NULL UNIQUE,
    specialty VARCHAR(50)  NOT NULL,
    phone     VARCHAR(20),
    user_id   BIGINT UNIQUE REFERENCES TB_USER (id)
);

CREATE TABLE TB_RECEPTIONIST
(
    id      BIGSERIAL PRIMARY KEY,
    name    VARCHAR(100) NOT NULL,
    phone   VARCHAR(20),
    user_id BIGINT       NOT NULL UNIQUE REFERENCES TB_USER (id)
);

CREATE TABLE TB_PATIENT
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    cpf        VARCHAR(14)  NOT NULL UNIQUE,
    birth_date DATE         NOT NULL,
    phone      VARCHAR(20),
    email      VARCHAR(100),
    gender     VARCHAR(10),
    street     VARCHAR(255),
    city       VARCHAR(255),
    state      VARCHAR(255),
    zip_code   VARCHAR(255)
);

CREATE TABLE TB_APPOINTMENT
(
    id         BIGSERIAL PRIMARY KEY,
    patient_id BIGINT      NOT NULL REFERENCES TB_PATIENT (id),
    doctor_id  BIGINT      NOT NULL REFERENCES TB_DOCTOR (id),
    date_time  TIMESTAMP   NOT NULL,
    status     VARCHAR(20) NOT NULL,
    notes      VARCHAR(500),
    created_at TIMESTAMP   NOT NULL
);

CREATE TABLE TB_MEDICAL_RECORD
(
    id             BIGSERIAL PRIMARY KEY,
    appointment_id BIGINT        NOT NULL UNIQUE REFERENCES TB_APPOINTMENT (id),
    diagnosis      VARCHAR(1000) NOT NULL,
    prescription   VARCHAR(1000),
    notes          VARCHAR(1000),
    created_at     TIMESTAMP     NOT NULL
);

CREATE UNIQUE INDEX idx_appointment_doctor_datetime ON TB_APPOINTMENT (doctor_id, date_time);