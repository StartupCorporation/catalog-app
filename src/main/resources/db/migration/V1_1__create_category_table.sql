CREATE TABLE category
(
    id          uuid         not null,
    name        varchar(50)  not null unique,
    description varchar(255) not null,
    image_id    uuid         not null,
    FOREIGN KEY (image_id) REFERENCES file (id),
    PRIMARY KEY (id)
);