-- 1. request_id 컬럼 추가 (nullable)
ALTER TABLE summary_requests
ADD COLUMN request_id VARCHAR(36);

-- 2. status 컬럼 추가
ALTER TABLE summary_requests
ADD COLUMN status VARCHAR(20);

-- 3. 기존 데이터 보정
-- 이미 존재하는 row는 DONE 처리 + 임시 request_id 부여
UPDATE summary_requests
SET
    request_id = CONCAT('legacy-', id),
    status = 'DONE'
WHERE request_id IS NULL;

-- 4. NOT NULL 제약 추가
ALTER TABLE summary_requests
MODIFY request_id VARCHAR(36) NOT NULL;

ALTER TABLE summary_requests
MODIFY status VARCHAR(20) NOT NULL;

-- 5. UNIQUE 제약 추가 (멱등성 핵심)
ALTER TABLE summary_requests
ADD CONSTRAINT uq_summary_requests_request_id UNIQUE (request_id);
