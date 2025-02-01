ALTER TABLE file
    ADD COLUMN product_id UUID,
    ADD CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES product (id) ON DELETE CASCADE;
