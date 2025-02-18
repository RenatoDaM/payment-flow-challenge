-- noinspection SqlDialectInspectionForFile

-- noinspection SqlNoDataSourceInspectionForFile

--liquibase formatted sql
--changeset renatodam:initial-database-setup logicalFilePath:initial-setup

CREATE TABLE users (
                       id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                       full_name VARCHAR(255) NOT NULL,
                       document_number VARCHAR(40) NOT NULL UNIQUE,
                       email VARCHAR (255) NOT NULL UNIQUE,
                       password VARCHAR (255) NOT NULL,
                       role VARCHAR(40) NOT NULL,
                       balance NUMERIC(10, 2) NOT NULL,
                       version BIGINT NOT NULL
);

CREATE TABLE transfers (
                           id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                           payer BIGINT NOT NULL,
                           payee BIGINT NOT NULL,
                           value NUMERIC(10, 2) NOT NULL,
                           transfer_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                           FOREIGN KEY (payer) REFERENCES users(id),
                           FOREIGN KEY (payee) REFERENCES users(id)
);
