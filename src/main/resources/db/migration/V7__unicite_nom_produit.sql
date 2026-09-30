-- Preserve every product and its references; resolve existing duplicates explicitly.
DO $$
BEGIN
    IF EXISTS (
        SELECT LOWER(name) FROM product GROUP BY LOWER(name) HAVING COUNT(*) > 1
    ) THEN
        RAISE EXCEPTION 'Duplicate product names exist. Rename the conflicting products before applying V7; no products have been deleted.';
    END IF;
END $$;

CREATE UNIQUE INDEX uq_product_name_lower ON product (LOWER(name));
