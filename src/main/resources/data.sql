-- 공통 코드 시드 (매 부팅 시 실행, INSERT IGNORE 로 idempotent)
-- created_by / deleted_by 는 nullable 이므로 seed 는 NULL 로 둔다.

-- ──────────────────────────────────────────────────────────
-- 0) 레벨 규칙 (level_rules)
--    required_total_exp: 해당 레벨에 도달하기 위한 누적 경험치
-- ──────────────────────────────────────────────────────────
INSERT IGNORE INTO level_rules (level, required_total_exp)
VALUES
  (1,     0),
  (2,   100),
  (3,   300),
  (4,   600),
  (5,  1000),
  (6,  1500),
  (7,  2500),
  (8,  4000),
  (9,  6000),
  (10, 10000);

-- ──────────────────────────────────────────────────────────
-- 1) 공통 코드 그룹
-- ──────────────────────────────────────────────────────────
INSERT IGNORE INTO common_group
  (common_group_id, created_by, deleted_by, created_at, deleted_at)
VALUES
  ('PROJECT_TECH_STACK',    NULL, NULL, NOW(), NULL),
  ('PROJECT_POSITION',      NULL, NULL, NOW(), NULL),
  ('NOTIFICATION_LINK_TYPE', NULL, NULL, NOW(), NULL),
  ('QUEST_CATEGORY',        NULL, NULL, NOW(), NULL);

-- ──────────────────────────────────────────────────────────
-- 2) 기술 스택 코드
-- ──────────────────────────────────────────────────────────
INSERT IGNORE INTO common_group_detail
  (common_group_detail_id, common_group_id, created_by, deleted_by, created_at, deleted_at)
VALUES
  ('JavaScript',      'PROJECT_TECH_STACK', NULL, NULL, NOW(), NULL),
  ('TypeScript',      'PROJECT_TECH_STACK', NULL, NULL, NOW(), NULL),
  ('Python',  'PROJECT_TECH_STACK', NULL, NULL, NOW(), NULL),
  ('Java',    'PROJECT_TECH_STACK', NULL, NULL, NOW(), NULL),
  ('Spring',  'PROJECT_TECH_STACK', NULL, NULL, NOW(), NULL),
  ('React',   'PROJECT_TECH_STACK', NULL, NULL, NOW(), NULL),
  ('Nextjs',  'PROJECT_TECH_STACK', NULL, NULL, NOW(), NULL),
  ('Aws',     'PROJECT_TECH_STACK', NULL, NULL, NOW(), NULL);

-- ──────────────────────────────────────────────────────────
-- 3) 프로젝트 역할 코드
-- ──────────────────────────────────────────────────────────
INSERT IGNORE INTO common_group_detail
  (common_group_detail_id, common_group_id, created_by, deleted_by, created_at, deleted_at)
VALUES
  ('Frontend',  'PROJECT_POSITION', NULL, NULL, NOW(), NULL),
  ('Backend',   'PROJECT_POSITION', NULL, NULL, NOW(), NULL),
  ('Fullstack', 'PROJECT_POSITION', NULL, NULL, NOW(), NULL),
  ('Devops',    'PROJECT_POSITION', NULL, NULL, NOW(), NULL),
  ('Design',    'PROJECT_POSITION', NULL, NULL, NOW(), NULL),
  ('Pm',        'PROJECT_POSITION', NULL, NULL, NOW(), NULL);

-- ──────────────────────────────────────────────────────────
-- 4) 알림(SSE) 링크 타입 코드
--    NotificationFacadeImpl 에서 PROJECT 를 우선 사용.
--    NotificationController 임시 mock 에서 QUEST / INVITE 참조.
-- ──────────────────────────────────────────────────────────
INSERT IGNORE INTO common_group_detail
  (common_group_detail_id, common_group_id, created_by, deleted_by, created_at, deleted_at)
VALUES
  ('PROJECT', 'NOTIFICATION_LINK_TYPE', NULL, NULL, NOW(), NULL),
  ('QUEST',   'NOTIFICATION_LINK_TYPE', NULL, NULL, NOW(), NULL),
  ('INVITE',  'NOTIFICATION_LINK_TYPE', NULL, NULL, NOW(), NULL);

-- ──────────────────────────────────────────────────────────
-- 5) AI 퀘스트 카테고리 코드
--    AI 리포트 scorecard.categories[].key 값과 일치해야 함.
-- ──────────────────────────────────────────────────────────
INSERT IGNORE INTO common_group_detail
  (common_group_detail_id, common_group_id, created_by, deleted_by, created_at, deleted_at)
VALUES
  ('ARCHITECTURE',         'QUEST_CATEGORY', NULL, NULL, NOW(), NULL),
  ('PERFORMANCE_READINESS','QUEST_CATEGORY', NULL, NULL, NOW(), NULL),
  ('CODE_QUALITY',         'QUEST_CATEGORY', NULL, NULL, NOW(), NULL),
  ('TESTABILITY',          'QUEST_CATEGORY', NULL, NULL, NOW(), NULL),
  ('RELIABILITY',          'QUEST_CATEGORY', NULL, NULL, NOW(), NULL);
