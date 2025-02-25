-- noinspection SqlDialectInspectionForFile

-- noinspection SqlNoDataSourceInspectionForFile

--liquibase formatted sql
--changeset renatodam:test-users logicalFilePath:test-users

INSERT INTO users (full_name, document_number, email, password, role, balance, version)
    VALUES
    ('John Doe', '00000000001', 'alice.johnson@email.com', '$2a$12$lGKzbiC6bZ3bDu6cwjtOd.8WM0ee7S7ZECD20tVfsHtzqXk2bJtbe', 'COMMON', 1000.00, 1),
    ('Bob Smith', '00000000002', 'bob.smith@email.com', '$2a$12$TuCqf3AlVa/Ddbps8Y8aMumdMcDu7mbVYEfQo2wNhmDGdMdpEssPC', 'COMMON', 500.00, 1),
    ('Charlie Brown', '00000000003', 'charlie.brown@email.com', '$2a$12$YIMvhddyaPXgaG/vYNB2xunnVg27y5gRttHXA3MHUN7K/xl.qjzOW', 'MERCHANT', 750.50, 1)