UPDATE coin_ledger
SET balance_after = balnace_after
WHERE balnace_after IS NOT NULL;

-- 3) 오타 컬럼 삭제
ALTER TABLE coin_ledger
    DROP COLUMN balnace_after;