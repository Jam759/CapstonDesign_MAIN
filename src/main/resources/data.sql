-- 공통 코드 시드
-- created_by / deleted_by 는 nullable 이므로 seed 시 NULL 로 둔다.

-- ----------------------------------------------------------------
-- 0) 레벨 규칙 (level_rules)
--    required_total_exp: 해당 레벨에 도달하기 위한 누적 경험치
-- ----------------------------------------------------------------
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

-- ----------------------------------------------------------------
-- 1) 공통 코드 그룹
-- ----------------------------------------------------------------
INSERT IGNORE INTO common_group
  (common_group_id, description, created_by, deleted_by, created_at, deleted_at)
VALUES
  ('PROJECT_TECH_STACK',     '프로젝트와 사용자 프로필에서 사용하는 기술 스택 공통 코드 그룹', NULL, NULL, NOW(), NULL),
  ('PROJECT_POSITION',       '프로젝트 멤버와 사용자 프로필에서 사용하는 포지션 공통 코드 그룹', NULL, NULL, NOW(), NULL),
  ('USER_GOAL',              '사용자 프로필 목표 공통 코드 그룹', NULL, NULL, NOW(), NULL),
  ('NOTIFICATION_LINK_TYPE', 'SSE 알림 링크 타입 공통 코드 그룹', NULL, NULL, NOW(), NULL),
  ('QUEST_CATEGORY',         'AI 퀘스트 카테고리 공통 코드 그룹', NULL, NULL, NOW(), NULL);

-- ----------------------------------------------------------------
-- 2) 기술 스택 공통 코드
-- ----------------------------------------------------------------
INSERT IGNORE INTO common_group_detail
  (common_group_detail_id, common_group_id, description, created_by, deleted_by, created_at, deleted_at)
VALUES
  ('JavaScript', 'PROJECT_TECH_STACK', 'JavaScript 기술 스택', NULL, NULL, NOW(), NULL),
  ('TypeScript', 'PROJECT_TECH_STACK', 'TypeScript 기술 스택', NULL, NULL, NOW(), NULL),
  ('Python',     'PROJECT_TECH_STACK', 'Python 기술 스택', NULL, NULL, NOW(), NULL),
  ('Java',       'PROJECT_TECH_STACK', 'Java 기술 스택', NULL, NULL, NOW(), NULL),
  ('Kotlin',     'PROJECT_TECH_STACK', 'Kotlin 기술 스택', NULL, NULL, NOW(), NULL),
  ('Go',         'PROJECT_TECH_STACK', 'Go 기술 스택', NULL, NULL, NOW(), NULL),
  ('Cpp',        'PROJECT_TECH_STACK', 'C/C++ 기술 스택', NULL, NULL, NOW(), NULL),
  ('Cs',         'PROJECT_TECH_STACK', 'C#/.NET 기술 스택', NULL, NULL, NOW(), NULL),
  ('Rust',       'PROJECT_TECH_STACK', 'Rust 기술 스택', NULL, NULL, NOW(), NULL),
  ('Swift',      'PROJECT_TECH_STACK', 'Swift 기술 스택', NULL, NULL, NOW(), NULL),
  ('Php',        'PROJECT_TECH_STACK', 'PHP 기술 스택', NULL, NULL, NOW(), NULL),
  ('Ruby',       'PROJECT_TECH_STACK', 'Ruby 기술 스택', NULL, NULL, NOW(), NULL),
  ('Dart',       'PROJECT_TECH_STACK', 'Dart/Flutter 기술 스택', NULL, NULL, NOW(), NULL),
  ('Sql',        'PROJECT_TECH_STACK', 'SQL 기술 스택', NULL, NULL, NOW(), NULL),
  ('Shell',      'PROJECT_TECH_STACK', 'Shell/Bash 기술 스택', NULL, NULL, NOW(), NULL),
  ('Spring',     'PROJECT_TECH_STACK', 'Spring 프레임워크 기술 스택', NULL, NULL, NOW(), NULL),
  ('React',      'PROJECT_TECH_STACK', 'React 프레임워크 기술 스택', NULL, NULL, NOW(), NULL),
  ('Nextjs',     'PROJECT_TECH_STACK', 'Next.js 프레임워크 기술 스택', NULL, NULL, NOW(), NULL),
  ('Aws',        'PROJECT_TECH_STACK', 'AWS 클라우드 기술 스택', NULL, NULL, NOW(), NULL),
  ('Other',      'PROJECT_TECH_STACK', '기타 기술 스택', NULL, NULL, NOW(), NULL);

-- ----------------------------------------------------------------
-- 3) 프로젝트 포지션 공통 코드
-- ----------------------------------------------------------------
INSERT IGNORE INTO common_group_detail
  (common_group_detail_id, common_group_id, description, created_by, deleted_by, created_at, deleted_at)
VALUES
  ('Frontend',  'PROJECT_POSITION', '프론트엔드 포지션', NULL, NULL, NOW(), NULL),
  ('Backend',   'PROJECT_POSITION', '백엔드 포지션', NULL, NULL, NOW(), NULL),
  ('Fullstack', 'PROJECT_POSITION', '풀스택 포지션', NULL, NULL, NOW(), NULL),
  ('Devops',    'PROJECT_POSITION', 'DevOps/인프라 포지션', NULL, NULL, NOW(), NULL),
  ('AiMl',      'PROJECT_POSITION', 'AI/ML 포지션', NULL, NULL, NOW(), NULL),
  ('Mobile',    'PROJECT_POSITION', '모바일 포지션', NULL, NULL, NOW(), NULL),
  ('Design',    'PROJECT_POSITION', '디자인 포지션', NULL, NULL, NOW(), NULL),
  ('Pm',        'PROJECT_POSITION', '기획(Product Manager) 포지션', NULL, NULL, NOW(), NULL),
  ('Qa',        'PROJECT_POSITION', 'QA/테스트 포지션', NULL, NULL, NOW(), NULL),
  ('Security',  'PROJECT_POSITION', '보안 포지션', NULL, NULL, NOW(), NULL),
  ('Data',      'PROJECT_POSITION', '데이터 엔지니어링 포지션', NULL, NULL, NOW(), NULL);

-- ----------------------------------------------------------------
-- 4) 사용자 목표 공통 코드
-- ----------------------------------------------------------------
INSERT IGNORE INTO common_group_detail
  (common_group_detail_id, common_group_id, description, created_by, deleted_by, created_at, deleted_at)
VALUES
  ('Job',         'USER_GOAL', '취업 준비 목표', NULL, NULL, NOW(), NULL),
  ('Improve',     'USER_GOAL', '실력 향상 및 자기계발 목표', NULL, NULL, NOW(), NULL),
  ('SideProject', 'USER_GOAL', '사이드 프로젝트 목표', NULL, NULL, NOW(), NULL),
  ('Career',      'USER_GOAL', '이직 준비 목표', NULL, NULL, NOW(), NULL),
  ('OpenSource',  'USER_GOAL', '오픈소스 기여 목표', NULL, NULL, NOW(), NULL),
  ('Leadership',  'USER_GOAL', '팀 리드 및 아키텍처 역량 강화 목표', NULL, NULL, NOW(), NULL);

-- ----------------------------------------------------------------
-- 5) 알림(SSE) 링크 타입 공통 코드
--    NotificationFacadeImpl 에서 PROJECT 를 우선 사용한다.
--    NotificationController 예시 mock 에서 QUEST / INVITE 를 참조한다.
-- ----------------------------------------------------------------
INSERT IGNORE INTO common_group_detail
  (common_group_detail_id, common_group_id, description, created_by, deleted_by, created_at, deleted_at)
VALUES
  ('PROJECT', 'NOTIFICATION_LINK_TYPE', '프로젝트 화면으로 이동하는 알림 링크', NULL, NULL, NOW(), NULL),
  ('QUEST',   'NOTIFICATION_LINK_TYPE', '퀘스트 화면으로 이동하는 알림 링크', NULL, NULL, NOW(), NULL),
  ('INVITE',  'NOTIFICATION_LINK_TYPE', '초대 관련 화면으로 이동하는 알림 링크', NULL, NULL, NOW(), NULL);

-- ----------------------------------------------------------------
-- 6) AI 퀘스트 카테고리 공통 코드
--    AI 리포트의 scorecard.categories[].key 값과 일치해야 한다.
-- ----------------------------------------------------------------
INSERT IGNORE INTO common_group_detail
  (common_group_detail_id, common_group_id, description, created_by, deleted_by, created_at, deleted_at)
VALUES
  ('ARCHITECTURE',          'QUEST_CATEGORY', '아키텍처 관련 퀘스트 카테고리', NULL, NULL, NOW(), NULL),
  ('PERFORMANCE_READINESS', 'QUEST_CATEGORY', '성능 준비도 관련 퀘스트 카테고리', NULL, NULL, NOW(), NULL),
  ('CODE_QUALITY',          'QUEST_CATEGORY', '코드 품질 관련 퀘스트 카테고리', NULL, NULL, NOW(), NULL),
  ('TESTABILITY',           'QUEST_CATEGORY', '테스트 용이성 관련 퀘스트 카테고리', NULL, NULL, NOW(), NULL),
  ('RELIABILITY',           'QUEST_CATEGORY', '신뢰성 관련 퀘스트 카테고리', NULL, NULL, NOW(), NULL);

-- ----------------------------------------------------------------
-- 7) 학습 자료 검색 플랫폼 공통 코드
-- ----------------------------------------------------------------
INSERT IGNORE INTO common_group
  (common_group_id, description, created_by, deleted_by, created_at, deleted_at)
VALUES
  ('SEARCH_KEYWORD_PLATFORM', '프로젝트 학습 자료 검색 키워드 플랫폼 공통 코드 그룹', NULL, NULL, NOW(), NULL);

INSERT IGNORE INTO common_group_detail
  (common_group_detail_id, common_group_id, description, created_by, deleted_by, created_at, deleted_at)
VALUES
  ('KOCW',    'SEARCH_KEYWORD_PLATFORM', '대학 공개강의 플랫폼 (이론/과목명 중심)', NULL, NULL, NOW(), NULL),
  ('KMOOC',   'SEARCH_KEYWORD_PLATFORM', '온라인 공개강좌 플랫폼 (커리큘럼/강좌명 중심)', NULL, NULL, NOW(), NULL),
  ('YOUTUBE', 'SEARCH_KEYWORD_PLATFORM', '영상 플랫폼 (실습/튜토리얼/최신 기술 중심)', NULL, NULL, NOW(), NULL);
