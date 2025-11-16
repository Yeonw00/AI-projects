UPDATE coin_ledger
SET request_id = ref_key
WHERE id = 1;

ALTER TABLE coin_ledger
	DROP COLUMN ref_key;