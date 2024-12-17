CREATE TABLE file
(
    id   uuid         not null,
    name varchar(255) not null unique,
    PRIMARY KEY (id)
);