-- ============================================================
-- V3__normalize_shared_transactions.sql
-- Remove transitive redundancy in shared_transactions:
--   Before: sheet_id → shared_sheets  AND  profile_id → profiles
--           (profile reachable via shared_sheet_members)
--   After:  member_id → shared_sheet_members
--           (sheet and profile both derived from the member row)
-- ============================================================

-- 1. Add the new FK column (nullable first so we can populate it)
ALTER TABLE shared_transactions
    ADD COLUMN member_id BIGINT;

-- 2. Populate member_id by matching on (sheet_id, profile_id)
UPDATE shared_transactions st
SET member_id = ssm.id
FROM shared_sheet_members ssm
WHERE ssm.sheet_id  = st.sheet_id
  AND ssm.profile_id = st.profile_id;

-- 3. Enforce NOT NULL and FK constraint
ALTER TABLE shared_transactions
    ALTER COLUMN member_id SET NOT NULL;

ALTER TABLE shared_transactions
    ADD CONSTRAINT fk_shared_tx_member
        FOREIGN KEY (member_id) REFERENCES shared_sheet_members(id);

-- 4. Drop the now-redundant columns and their old index
DROP INDEX IF EXISTS idx_shared_transactions_sheet;

ALTER TABLE shared_transactions DROP COLUMN sheet_id;
ALTER TABLE shared_transactions DROP COLUMN profile_id;

-- 5. New index on member_id for fast queries by sheet
CREATE INDEX idx_shared_transactions_member ON shared_transactions(member_id);
