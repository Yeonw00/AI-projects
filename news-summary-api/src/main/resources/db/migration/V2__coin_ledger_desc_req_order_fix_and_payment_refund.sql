-- ==============================================================
-- V2__coin_ledger_desc_req_order_fix_and_payment_refund.sql
-- 호환성 최우선(ANY MySQL 8.x / MariaDB에서도 동작하도록)
-- ==============================================================

-- 0) 안전을 위해 변수 초기화
SET @sql := NULL;

-- 1) balnaceAfter -> balance_after (있을 때만)
SET @col_exists := (
  SELECT COUNT(*)
  FROM information_schema.columns
  WHERE table_schema = DATABASE()
    AND table_name = 'coin_ledger'
    AND column_name = 'balnaceAfter'
);
SET @sql := IF(@col_exists = 1,
  'ALTER TABLE `coin_ledger` RENAME COLUMN `balnaceAfter` TO `balance_after`',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2) coin_ledger 컬럼 조건부 추가 (IF NOT EXISTS 대신 존재 체크)
-- description
SET @col_exists := (
  SELECT COUNT(*) FROM information_schema.columns
  WHERE table_schema = DATABASE()
    AND table_name = 'coin_ledger'
    AND column_name = 'description'
);
SET @sql := IF(@col_exists = 0,
  'ALTER TABLE `coin_ledger` ADD COLUMN `description` VARCHAR(255) NULL COMMENT ''설명''',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- request_id
SET @col_exists := (
  SELECT COUNT(*) FROM information_schema.columns
  WHERE table_schema = DATABASE()
    AND table_name = 'coin_ledger'
    AND column_name = 'request_id'
);
SET @sql := IF(@col_exists = 0,
  'ALTER TABLE `coin_ledger` ADD COLUMN `request_id` VARCHAR(64) NULL',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- order_id
SET @col_exists := (
  SELECT COUNT(*) FROM information_schema.columns
  WHERE table_schema = DATABASE()
    AND table_name = 'coin_ledger'
    AND column_name = 'order_id'
);
SET @sql := IF(@col_exists = 0,
  'ALTER TABLE `coin_ledger` ADD COLUMN `order_id` VARCHAR(64) NULL',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 3) coin_ledger 인덱스 조건부 추가 (CREATE/ALTER에 IF NOT EXISTS 쓰지 않음)
-- UNIQUE uq_coin_ledger_request_id(request_id)
SET @idx_exists := (
  SELECT COUNT(*) FROM information_schema.statistics
  WHERE table_schema = DATABASE()
    AND table_name = 'coin_ledger'
    AND index_name = 'uq_coin_ledger_request_id'
);
SET @sql := IF(@idx_exists = 0,
  'ALTER TABLE `coin_ledger` ADD UNIQUE INDEX `uq_coin_ledger_request_id` (`request_id`)',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- INDEX idx_ledger_order_id(order_id)
SET @idx_exists := (
  SELECT COUNT(*) FROM information_schema.statistics
  WHERE table_schema = DATABASE()
    AND table_name = 'coin_ledger'
    AND index_name = 'idx_ledger_order_id'
);
SET @sql := IF(@idx_exists = 0,
  'ALTER TABLE `coin_ledger` ADD INDEX `idx_ledger_order_id` (`order_id`)',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 4) payment_order.refunded_amount 추가 (조건부)
SET @col_exists := (
  SELECT COUNT(*) FROM information_schema.columns
  WHERE table_schema = DATABASE()
    AND table_name = 'payment_order'
    AND column_name = 'refunded_amount'
);
SET @sql := IF(@col_exists = 0,
  'ALTER TABLE `payment_order` ADD COLUMN `refunded_amount` BIGINT NOT NULL DEFAULT 0',
  'DO 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 5) 오타 데이터 수정
UPDATE `payment_order`
   SET `status` = 'CANCELED'
 WHERE `status` = 'CANCELD';
