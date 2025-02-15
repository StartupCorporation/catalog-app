CREATE TABLE ATTRIBUTE
(
    id          UUID PRIMARY KEY,
    name        VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(255),
    metadata    JSONB
);

CREATE TABLE CATEGORY_ATTRIBUTE
(
    id            UUID PRIMARY KEY,
    category_id   UUID    NOT NULL,
    attribute_id  UUID    NOT NULL,
    is_required   BOOLEAN NOT NULL,
    is_filterable BOOLEAN NOT NULL,
    CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES CATEGORY (id) ON DELETE CASCADE,
    CONSTRAINT fk_attribute FOREIGN KEY (attribute_id) REFERENCES ATTRIBUTE (id) ON DELETE CASCADE
);

CREATE TABLE ATTRIBUTE_PRODUCT_VALUE
(
    id           UUID PRIMARY KEY,
    product_id   UUID  NOT NULL,
    attribute_id UUID  NOT NULL,
    value        JSONB NOT NULL,
    CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES PRODUCT (id) ON DELETE CASCADE,
    CONSTRAINT fk_attribute FOREIGN KEY (attribute_id) REFERENCES ATTRIBUTE (id) ON DELETE CASCADE
);