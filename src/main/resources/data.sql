-- 공통 코드 시드
-- created_by / deleted_by 는 nullable 이므로 seed 시 NULL 로 둔다.

-- ----------------------------------------------------------------
-- 1) 공통 코드 그룹
-- ----------------------------------------------------------------
INSERT IGNORE INTO common_group
  (use_yn, common_group_id, description, created_by, deleted_by, created_at, deleted_at)
VALUES
  ('y','PROJECT_TECH_STACK',      '프로젝트와 사용자 프로필에서 사용하는 기술 스택 공통 코드 그룹', NULL, NULL, NOW(), NULL),
  ('y','PROJECT_POSITION',        '프로젝트 멤버와 사용자 프로필에서 사용하는 포지션 공통 코드 그룹', NULL, NULL, NOW(), NULL),
  ('y','USER_GOAL',               '사용자 프로필 목표 공통 코드 그룹', NULL, NULL, NOW(), NULL),
  ('y','NOTIFICATION_LINK_TYPE',  'SSE 알림 링크 타입 공통 코드 그룹', NULL, NULL, NOW(), NULL),
  ('y','QUEST_CATEGORY',          'AI 퀘스트 카테고리 공통 코드 그룹', NULL, NULL, NOW(), NULL),
  ('y','LEVEL_RULE',              '각 레벨별 경험치 요구치 공통 코드 그룹 ref1 : 레벨(int) ref2 : 필요누적 경험치(long)', NULL, NULL, NOW(), NULL),
  ('y','SEARCH_KEYWORD_PLATFORM', '프로젝트 학습 자료 검색 키워드 플랫폼 공통 코드 그룹', NULL, NULL, NOW(), NULL);
;

-- ----------------------------------------------------------------
-- 2) 기술 스택 공통 코드
-- ----------------------------------------------------------------
INSERT IGNORE INTO common_group_detail
  (use_yn, common_group_detail_id, common_group_id, description, created_by, deleted_by, created_at, deleted_at)
VALUES
  ('y','JavaScript', 'PROJECT_TECH_STACK', 'JavaScript 기술 스택', NULL, NULL, NOW(), NULL),
  ('y','TypeScript', 'PROJECT_TECH_STACK', 'TypeScript 기술 스택', NULL, NULL, NOW(), NULL),
  ('y','Python',     'PROJECT_TECH_STACK', 'Python 기술 스택', NULL, NULL, NOW(), NULL),
  ('y','Java',       'PROJECT_TECH_STACK', 'Java 기술 스택', NULL, NULL, NOW(), NULL),
  ('y','Kotlin',     'PROJECT_TECH_STACK', 'Kotlin 기술 스택', NULL, NULL, NOW(), NULL),
  ('y','Go',         'PROJECT_TECH_STACK', 'Go 기술 스택', NULL, NULL, NOW(), NULL),
  ('y','Cpp',        'PROJECT_TECH_STACK', 'C/C++ 기술 스택', NULL, NULL, NOW(), NULL),
  ('y','Cs',         'PROJECT_TECH_STACK', 'C#/.NET 기술 스택', NULL, NULL, NOW(), NULL),
  ('y','Rust',       'PROJECT_TECH_STACK', 'Rust 기술 스택', NULL, NULL, NOW(), NULL),
  ('y','Swift',      'PROJECT_TECH_STACK', 'Swift 기술 스택', NULL, NULL, NOW(), NULL),
  ('y','Php',        'PROJECT_TECH_STACK', 'PHP 기술 스택', NULL, NULL, NOW(), NULL),
  ('y','Ruby',       'PROJECT_TECH_STACK', 'Ruby 기술 스택', NULL, NULL, NOW(), NULL),
  ('y','Dart',       'PROJECT_TECH_STACK', 'Dart/Flutter 기술 스택', NULL, NULL, NOW(), NULL),
  ('y','Sql',        'PROJECT_TECH_STACK', 'SQL 기술 스택', NULL, NULL, NOW(), NULL),
  ('y','Shell',      'PROJECT_TECH_STACK', 'Shell/Bash 기술 스택', NULL, NULL, NOW(), NULL),
  ('y','Spring',     'PROJECT_TECH_STACK', 'Spring 프레임워크 기술 스택', NULL, NULL, NOW(), NULL),
  ('y','React',      'PROJECT_TECH_STACK', 'React 프레임워크 기술 스택', NULL, NULL, NOW(), NULL),
  ('y','Nextjs',     'PROJECT_TECH_STACK', 'Next.js 프레임워크 기술 스택', NULL, NULL, NOW(), NULL),
  ('y','Aws',        'PROJECT_TECH_STACK', 'AWS 클라우드 기술 스택', NULL, NULL, NOW(), NULL),
  ('y','Other',      'PROJECT_TECH_STACK', '기타 기술 스택', NULL, NULL, NOW(), NULL);

-- ----------------------------------------------------------------
-- 3) 프로젝트 포지션 공통 코드
-- ----------------------------------------------------------------
INSERT IGNORE INTO common_group_detail
  (use_yn,common_group_detail_id, common_group_id, description, created_by, deleted_by, created_at, deleted_at)
VALUES
  ('y','Frontend',  'PROJECT_POSITION', '프론트엔드 포지션', NULL, NULL, NOW(), NULL),
  ('y','Backend',   'PROJECT_POSITION', '백엔드 포지션', NULL, NULL, NOW(), NULL),
  ('y','Fullstack', 'PROJECT_POSITION', '풀스택 포지션', NULL, NULL, NOW(), NULL),
  ('y','Devops',    'PROJECT_POSITION', 'DevOps/인프라 포지션', NULL, NULL, NOW(), NULL),
  ('y','AiMl',      'PROJECT_POSITION', 'AI/ML 포지션', NULL, NULL, NOW(), NULL),
  ('y','Mobile',    'PROJECT_POSITION', '모바일 포지션', NULL, NULL, NOW(), NULL),
  ('y','Design',    'PROJECT_POSITION', '디자인 포지션', NULL, NULL, NOW(), NULL),
  ('y','Pm',        'PROJECT_POSITION', '기획(Product Manager) 포지션', NULL, NULL, NOW(), NULL),
  ('y','Qa',        'PROJECT_POSITION', 'QA/테스트 포지션', NULL, NULL, NOW(), NULL),
  ('y','Security',  'PROJECT_POSITION', '보안 포지션', NULL, NULL, NOW(), NULL),
  ('y','Data',      'PROJECT_POSITION', '데이터 엔지니어링 포지션', NULL, NULL, NOW(), NULL);

-- ----------------------------------------------------------------
-- 4) 사용자 목표 공통 코드
-- ----------------------------------------------------------------
INSERT IGNORE INTO common_group_detail
  (use_yn, common_group_detail_id, common_group_id, description, created_by, deleted_by, created_at, deleted_at)
VALUES
  ('y', 'Job',         'USER_GOAL', '취업 준비 목표', NULL, NULL, NOW(), NULL),
  ('y', 'Improve',     'USER_GOAL', '실력 향상 및 자기계발 목표', NULL, NULL, NOW(), NULL),
  ('y', 'SideProject', 'USER_GOAL', '사이드 프로젝트 목표', NULL, NULL, NOW(), NULL),
  ('y', 'Career',      'USER_GOAL', '이직 준비 목표', NULL, NULL, NOW(), NULL),
  ('y', 'OpenSource',  'USER_GOAL', '오픈소스 기여 목표', NULL, NULL, NOW(), NULL),
  ('y', 'Leadership',  'USER_GOAL', '팀 리드 및 아키텍처 역량 강화 목표', NULL, NULL, NOW(), NULL);

-- ----------------------------------------------------------------
-- 5) 알림(SSE) 링크 타입 공통 코드
--    NotificationFacadeImpl 에서 PROJECT 를 우선 사용한다.
--    NotificationController 예시 mock 에서 QUEST / INVITE 를 참조한다.
-- ----------------------------------------------------------------
INSERT IGNORE INTO common_group_detail
  (use_yn, common_group_detail_id, common_group_id, description, created_by, deleted_by, created_at, deleted_at)
VALUES
  ('y', 'PROJECT', 'NOTIFICATION_LINK_TYPE', '프로젝트 화면으로 이동하는 알림 링크', NULL, NULL, NOW(), NULL),
  ('y', 'QUEST',   'NOTIFICATION_LINK_TYPE', '퀘스트 화면으로 이동하는 알림 링크', NULL, NULL, NOW(), NULL),
  ('y', 'INVITE',  'NOTIFICATION_LINK_TYPE', '초대 관련 화면으로 이동하는 알림 링크', NULL, NULL, NOW(), NULL);

-- ----------------------------------------------------------------
-- 6) AI 퀘스트 카테고리 공통 코드
--    AI 리포트의 scorecard.categories[].key 값과 일치해야 한다.
-- ----------------------------------------------------------------
INSERT IGNORE INTO common_group_detail
  (use_yn, common_group_detail_id, common_group_id, description, created_by, deleted_by, created_at, deleted_at)
VALUES
  ('y', 'ARCHITECTURE',          'QUEST_CATEGORY', '아키텍처 관련 퀘스트 카테고리', NULL, NULL, NOW(), NULL),
  ('y', 'PERFORMANCE_READINESS', 'QUEST_CATEGORY', '성능 준비도 관련 퀘스트 카테고리', NULL, NULL, NOW(), NULL),
  ('y', 'CODE_QUALITY',          'QUEST_CATEGORY', '코드 품질 관련 퀘스트 카테고리', NULL, NULL, NOW(), NULL),
  ('y', 'TESTABILITY',           'QUEST_CATEGORY', '테스트 용이성 관련 퀘스트 카테고리', NULL, NULL, NOW(), NULL),
  ('y', 'RELIABILITY',           'QUEST_CATEGORY', '신뢰성 관련 퀘스트 카테고리', NULL, NULL, NOW(), NULL);

-- ----------------------------------------------------------------
-- 7) 학습 자료 검색 플랫폼 공통 코드
-- ----------------------------------------------------------------
INSERT IGNORE INTO common_group_detail
  (use_yn, common_group_detail_id, common_group_id, description, created_by, deleted_by, created_at, deleted_at)
VALUES
  ('y', 'KOCW',    'SEARCH_KEYWORD_PLATFORM', '대학 공개강의 플랫폼 (이론/과목명 중심)', NULL, NULL, NOW(), NULL),
  ('y', 'KMOOC',   'SEARCH_KEYWORD_PLATFORM', '온라인 공개강좌 플랫폼 (커리큘럼/강좌명 중심)', NULL, NULL, NOW(), NULL),
  ('y', 'YOUTUBE', 'SEARCH_KEYWORD_PLATFORM', '영상 플랫폼 (실습/튜토리얼/최신 기술 중심)', NULL, NULL, NOW(), NULL);

-- ----------------------------------------------------------------
-- 8) 각 레벨별 경험치 요구치 공통 코드
-- ----------------------------------------------------------------
INSERT IGNORE INTO common_group_detail
(use_yn, common_group_detail_id, common_group_id, description, ref1, ref2, created_by, deleted_by, created_at, deleted_at)
VALUES
    ('y', 'LEVEL_1',  'LEVEL_RULE', 'ref1: 해당레벨(Integer), ref2: 요구 누적 경험치(Long)',  '1',  '0',     NULL, NULL, NOW(), NULL),
    ('y', 'LEVEL_2',  'LEVEL_RULE', 'ref1: 해당레벨(Integer), ref2: 요구 누적 경험치(Long)',  '2',  '100',   NULL, NULL, NOW(), NULL),
    ('y', 'LEVEL_3',  'LEVEL_RULE', 'ref1: 해당레벨(Integer), ref2: 요구 누적 경험치(Long)',  '3',  '300',   NULL, NULL, NOW(), NULL),
    ('y', 'LEVEL_4',  'LEVEL_RULE', 'ref1: 해당레벨(Integer), ref2: 요구 누적 경험치(Long)',  '4',  '600',   NULL, NULL, NOW(), NULL),
    ('y', 'LEVEL_5',  'LEVEL_RULE', 'ref1: 해당레벨(Integer), ref2: 요구 누적 경험치(Long)',  '5',  '1000',  NULL, NULL, NOW(), NULL),
    ('y', 'LEVEL_6',  'LEVEL_RULE', 'ref1: 해당레벨(Integer), ref2: 요구 누적 경험치(Long)',  '6',  '1500',  NULL, NULL, NOW(), NULL),
    ('y', 'LEVEL_7',  'LEVEL_RULE', 'ref1: 해당레벨(Integer), ref2: 요구 누적 경험치(Long)',  '7',  '2500',  NULL, NULL, NOW(), NULL),
    ('y', 'LEVEL_8',  'LEVEL_RULE', 'ref1: 해당레벨(Integer), ref2: 요구 누적 경험치(Long)',  '8',  '4000',  NULL, NULL, NOW(), NULL),
    ('y', 'LEVEL_9',  'LEVEL_RULE', 'ref1: 해당레벨(Integer), ref2: 요구 누적 경험치(Long)',  '9',  '6000',  NULL, NULL, NOW(), NULL),
    ('y', 'LEVEL_10', 'LEVEL_RULE', 'ref1: 해당레벨(Integer), ref2: 요구 누적 경험치(Long)', '10', '10000', NULL, NULL, NOW(), NULL);