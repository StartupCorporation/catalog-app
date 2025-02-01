CREATE TABLE product
(
    id             uuid         not null,
    name           varchar(50)  not null unique,
    description    varchar(255) not null,
    price          float        not null,
    stock_quantity int          not null,
    category_id    uuid         not null,
    FOREIGN KEY (category_id) REFERENCES category (id),
    PRIMARY KEY (id)
);