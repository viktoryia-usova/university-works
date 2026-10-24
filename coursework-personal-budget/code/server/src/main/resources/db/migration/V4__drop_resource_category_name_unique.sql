-- Unique constraint on resource_categories.name conflicts with soft-delete:
-- deleted rows keep their name in the table, so re-creating a same-named category fails.
-- Application-level uniqueness (existsByNameIgnoreCaseAndDeletedFalse) is sufficient.
ALTER TABLE resource_categories DROP CONSTRAINT IF EXISTS resource_categories_name_key;
