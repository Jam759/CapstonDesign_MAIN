# Logging Policy

## 목적

이 문서는 현재 백엔드 애플리케이션에서 사용하는 로깅 정책, 메트릭 정책, trace 전파 정책, SQS 경계에서의 메시지 계약을 정리한다.

대상 범위:

- HTTP 요청/응답 구조화 로깅
- 일반 애플리케이션 로그
- 디버그 aspect 로깅
- Loki / Grafana / Prometheus 시각화
- SQS 기반 비동기 처리에서의 trace 연계

## 현재 구성 요약

현재 로깅/관측 구성은 아래 흐름으로 동작한다.

- HTTP 요청 진입
- `traceId` 생성 또는 복원
- 구조화 HTTP 로그 기록
- 일반 애플리케이션 로그 기록
- `./.logs` 아래 파일 저장
- Alloy가 로그 파일 수집
- Loki로 전송
- Grafana에서 조회
- Spring Actuator 메트릭 노출
- Prometheus가 `/actuator/prometheus` 스크랩
- Grafana에서 메트릭 조회

관련 파일:

- [ControllerLoggingAspect.java](/C:/Users/3379p/OneDrive/Desktop/intelliJ/CapstoneDesign/src/main/java/com/Hoseo/CapstoneDesign/global/logging/ControllerLoggingAspect.java)
- [StructuredHttpLogger.java](/C:/Users/3379p/OneDrive/Desktop/intelliJ/CapstoneDesign/src/main/java/com/Hoseo/CapstoneDesign/global/logging/StructuredHttpLogger.java)
- [TraceMdcFilter.java](/C:/Users/3379p/OneDrive/Desktop/intelliJ/CapstoneDesign/src/main/java/com/Hoseo/CapstoneDesign/security/filter/TraceMdcFilter.java)
- [DebugLoggingAspect.java](/C:/Users/3379p/OneDrive/Desktop/intelliJ/CapstoneDesign/src/main/java/com/Hoseo/CapstoneDesign/global/logging/DebugLoggingAspect.java)
- [logback-spring.xml](/C:/Users/3379p/OneDrive/Desktop/intelliJ/CapstoneDesign/src/main/resources/logback-spring.xml)
- [application.yaml](/C:/Users/3379p/OneDrive/Desktop/intelliJ/CapstoneDesign/src/main/resources/application.yaml)
- [Docker-Compose.yaml](/C:/Users/3379p/OneDrive/Desktop/intelliJ/CapstoneDesign/Docker-Compose.yaml)

## 로그 종류와 정책

### 1. 운영용 구조화 HTTP 로그

목적:

- 요청 단위 집계
- 상태 코드, 에러율, 응답 시간 시각화
- 특정 `traceId` 로 요청 추적

특징:

- JSON 한 줄 형식
- 파일 출력 전용
- Loki 수집 대상
- Grafana 집계 대상

출력 파일:

- `./.logs/structured-http.log`

기록 이벤트:

- `HTTP_REQUEST`
- `HTTP_RESPONSE`
- `HTTP_ERROR`

기본 필드:

- `timestamp`
- `level`
- `service`
- `serverType`
- `category`
- `eventType`
- `traceId`
- `className`
- `method`
- `message`
- `args`
- `http.method`
- `http.path`
- `http.status`
- `durationMs`
- `error.message`
- `error.code`
- `error.httpStatus`

기록 기준:

- 컨트롤러 진입 시 `HTTP_REQUEST`
- 정상 종료 시 `HTTP_RESPONSE`
- 예외 발생 시 `HTTP_ERROR`
- SSE는 완료가 아니라 `"Streaming response started"` 로 기록

### 2. 일반 애플리케이션 로그

목적:

- 개발/운영 중 일반 메시지 확인
- Hibernate SQL, 서비스 내부 로그, SQS 처리 로그 등 확인

출력 위치:

- 콘솔
- `./.logs/application.log`

형식:

- 사람이 읽기 쉬운 일반 텍스트 로그
- 현재 패턴에 `traceId` 포함

현재 패턴 예시:

```text
2026-04-02 18:52:23.710 DEBUG [tomcat-handler-18] [traceId=23c17112-5410-455d-89ca-8a58c4a195a9] org.hibernate.SQL - ...
```

이 로그는 Loki로도 수집되지만, 주 집계 대상은 구조화 HTTP 로그가 우선이다.

### 3. 디버그 aspect 로그

목적:

- 서비스/파사드/리포지토리/매퍼 레벨의 흐름 추적
- 특정 장애 상황에서 메서드 진입/종료/예외 확인

현재 정책:

- 기본 비활성
- 프로퍼티로 토글
- 운영 구조화 로그와 분리
- 일반 애플리케이션 로그 경로로 남음

활성화 프로퍼티:

```yaml
app:
  logging:
    debug-aspect:
      enabled: true
      max-string-length: 120
      max-collection-size: 10
      log-return-value: true
```

적용 범위:

- `service` 패키지
- `repository` 패키지
- `mapper` 패키지
- `@Service`
- `@Repository`
- `@Mapper`
- `@Facade`

로그 형태:

- `ENTER`
- `EXIT`
- `THROW`

예시:

```text
[ENTER] ProjectService.getById args={projectId=12}
[EXIT] ProjectService.getById durationMs=3 result={_type=Projects, projectId=12, ...}
[THROW] RefreshTokenServiceImpl.rotate durationMs=1 exception=JwtUtilException message=...
```

## 마스킹 정책

### 운영용 구조화 HTTP 로그

운영용 구조화 로그는 민감 정보 노출 방지가 우선이다.

민감 키는 완전 마스킹:

- `password`
- `passwd`
- `secret`
- `token`
- `authorization`
- `cookie`
- `signature`
- `state`
- `credential`

예시:

- `refreshTokenRaw` -> `<redacted>`
- `authorizationHeader` -> `<redacted>`
- `signature256` -> `<redacted>`

비민감 값은 원문 또는 간단한 안전 변환만 기록한다.

### 디버그 aspect 로그

디버그 로그는 운영 로그보다 더 많은 문맥을 남기지만, 민감값은 그대로 남기지 않는다.

정책:

- 민감 키는 완전 마스킹
- 일반 문자열은 길이 제한으로 축약
- 컬렉션은 최대 개수까지만 기록
- 애플리케이션 내부 객체는 필드 일부만 펼쳐서 기록

즉, 디버그용이라고 해도 토큰/비밀번호/secret 류는 절대 앞부분만 남기지 않는다.

이유:

- 토큰 일부 노출도 재현 공격이나 로그 유출 사고의 원인이 될 수 있음
- 디버그 목적은 값의 "형태" 와 "흐름" 확인이지 비밀값 원문 확인이 아님

## traceId 정책

### HTTP 요청 진입 시 traceId 생성 규칙

우선순위:

1. `X-GitHub-Delivery`
2. `X-Trace-Id`
3. 새 UUID 생성

즉 GitHub webhook 요청은 기본적으로:

- `traceId == GitHub deliveryId`

이 값은:

- MDC에 저장
- 응답 헤더 `X-Trace-Id` 에 기록
- 구조화 HTTP 로그에 기록
- 일반 애플리케이션 로그 패턴에도 포함

### 현재 traceId 의미

일반 API:

- 요청 단위 상관관계 ID

GitHub webhook:

- GitHub가 제공한 delivery ID
- 멱등성 키와 사실상 같은 출발점

## SQS 정책

### 이번에 반영된 SQS 수정 사항

이번 변경으로 이 레포에는 아래 수정이 반영됐다.

1. `SqsBaseMessage` 에 `traceId` 필드 추가
2. `SqsMessageSender` 에서 현재 MDC의 `traceId` 자동 주입
3. `NotificationQueueBaseMessage` 에 `traceId` 필드 추가
4. `NotificationQueueListener` 에서 수신 메시지의 `traceId` 를 MDC에 바인딩
5. notification 처리 후 MDC 정리
6. 일반 로그 패턴에 `[traceId=...]` 추가

관련 파일:

- [SqsBaseMessage.java](/C:/Users/3379p/OneDrive/Desktop/intelliJ/CapstoneDesign/src/main/java/com/Hoseo/CapstoneDesign/global/aws/sqs/SqsBaseMessage.java)
- [SqsMessageSender.java](/C:/Users/3379p/OneDrive/Desktop/intelliJ/CapstoneDesign/src/main/java/com/Hoseo/CapstoneDesign/global/aws/sqs/SqsMessageSender.java)
- [NotificationQueueBaseMessage.java](/C:/Users/3379p/OneDrive/Desktop/intelliJ/CapstoneDesign/src/main/java/com/Hoseo/CapstoneDesign/notification/dto/application/NotificationQueueBaseMessage.java)
- [NotificationQueueListener.java](/C:/Users/3379p/OneDrive/Desktop/intelliJ/CapstoneDesign/src/main/java/com/Hoseo/CapstoneDesign/notification/listener/NotificationQueueListener.java)
- [logback-spring.xml](/C:/Users/3379p/OneDrive/Desktop/intelliJ/CapstoneDesign/src/main/resources/logback-spring.xml)

### 현재 레포의 책임 범위

이 레포에서 책임지는 비동기 경계는 아래 두 개다.

- `analysisQueue` 발행
- `notificationQueue` 소비

외부 분석 워커는 별도 프로세스이며, 이 레포에 포함되지 않는다.

따라서 이 레포의 목표는:

- HTTP ingress의 `traceId` 를 `analysisQueue` 메시지에 실어 보내기
- 외부 워커가 그 `traceId` 를 유지해 `notificationQueue` 로 되돌려 보내면
- 알림 소비 시 다시 MDC에 복원하기

### SQS envelope 정책

현재 공통 envelope:

```json
{
  "traceId": "23c17112-5410-455d-89ca-8a58c4a195a9",
  "jobId": "123",
  "type": "NORMAL_ANALYSIS_REQUEST",
  "data": {}
}
```

관련 파일:

- [SqsBaseMessage.java](/C:/Users/3379p/OneDrive/Desktop/intelliJ/CapstoneDesign/src/main/java/com/Hoseo/CapstoneDesign/global/aws/sqs/SqsBaseMessage.java)
- [SqsMessageSender.java](/C:/Users/3379p/OneDrive/Desktop/intelliJ/CapstoneDesign/src/main/java/com/Hoseo/CapstoneDesign/global/aws/sqs/SqsMessageSender.java)

필드 의미:

- `traceId`: 분산 추적용 상관관계 ID
- `jobId`: 분석 작업 ID
- `type`: 분석 요청 종류
- `data`: 실제 작업 payload

### analysisQueue 발행 정책

현재 변경된 정책:

- `SqsMessageSender` 가 `SqsBaseMessage` 를 보낼 때
- 메시지에 `traceId` 가 비어 있으면
- 현재 MDC의 `traceId` 를 자동 주입

즉 webhook 요청에서 queue 발행 시:

- `traceId == deliveryId`
- 이 값이 메시지에 같이 실린다

정리하면:

- `deliveryId` 는 DB의 `AnalysisJob.deliveryId` 에 저장
- `traceId` 는 SQS envelope에도 별도 저장
- webhook 계열에서는 둘 값이 사실상 동일
- full scan 계열에서는 `traceId` 는 HTTP 요청 trace, `deliveryId` 는 현재 idempotency 문자열

### notificationQueue 소비 정책

현재 변경된 정책:

- 소비 시 envelope의 `traceId` 를 읽음
- 값이 있으면 `MDC.put(traceId, ...)`
- 이후 알림 처리 로그는 같은 trace로 남음
- 처리 후 `MDC.remove(traceId)` 로 정리

관련 파일:

- [NotificationQueueBaseMessage.java](/C:/Users/3379p/OneDrive/Desktop/intelliJ/CapstoneDesign/src/main/java/com/Hoseo/CapstoneDesign/notification/dto/application/NotificationQueueBaseMessage.java)
- [NotificationQueueListener.java](/C:/Users/3379p/OneDrive/Desktop/intelliJ/CapstoneDesign/src/main/java/com/Hoseo/CapstoneDesign/notification/listener/NotificationQueueListener.java)

현재 로그 예시:

```text
SQS analysis result received. traceId=23c17112-5410-455d-89ca-8a58c4a195a9, jobId=101, eventType=NORMAL_ANALYSIS_REQUEST, status=SUCCESS
```

## 외부 분석 워커가 반드시 맞춰야 할 계약

이 부분은 매우 중요하다.

현재 이 레포만 고쳐서는 trace가 끝까지 이어지지 않는다. 외부 워커도 아래 계약을 따라야 한다.

필수 요구사항:

1. `analysisQueue` 메시지의 `traceId` 를 읽어야 함
2. 워커 시작 시 해당 값을 MDC 또는 작업 컨텍스트에 바인딩해야 함
3. 워커 내부 로그에 같은 `traceId` 를 사용해야 함
4. `notificationQueue` 발행 시 같은 `traceId` 를 envelope에 다시 넣어야 함
5. `jobId` 는 그대로 유지해야 함

권장 예시:

```json
{
  "traceId": "23c17112-5410-455d-89ca-8a58c4a195a9",
  "jobId": "101",
  "eventType": "NORMAL_ANALYSIS_REQUEST",
  "status": "SUCCESS",
  "data": {
    "completeQuestIds": [1, 2],
    "newQuestIds": [10],
    "newProjectKBid": 55,
    "userViewReportId": 77
  }
}
```

이 계약이 지켜져야 Grafana/Loki에서 아래 흐름을 한 `traceId` 로 묶을 수 있다.

- GitHub webhook 수신
- 분석 큐 발행
- 외부 분석 워커 실행
- 알림 큐 발행
- 알림 소비

## 현재 남아 있는 한계

### 1. 외부 워커 로그는 이 레포에서 직접 보장할 수 없음

이 레포는 queue 경계 전후 계약만 맞춘다.

즉 다음은 외부 워커 구현에 달려 있다.

- 워커가 `traceId` 를 실제로 읽는지
- 워커가 로그에 MDC를 넣는지
- 워커가 알림 큐로 trace를 돌려주는지

### 2. full scan 흐름의 `deliveryId`

프로젝트 설정 기반 full scan은 GitHub webhook이 아니므로 `deliveryId` 컬럼에 현재 idempotency 문자열이 들어간다.

예:

- `projectId-userId-installationRepositoryId`

따라서 full scan에서는:

- `traceId` 는 HTTP 요청 기준
- `deliveryId` 는 webhook delivery ID가 아님

이 차이는 정상이다.

### 3. 일반 애플리케이션 로그는 아직 구조화 JSON이 아님

현재 `application.log` 는 텍스트 로그이며, `traceId` 로 grep/검색은 쉽지만 로그 스키마 기반 집계는 구조화 HTTP 로그보다 제한적이다.

## 운영 기준 권장 사용법

### 평상시 확인

- Grafana의 `Capstone Logging Overview`
- Grafana의 `Capstone Metrics Overview`
- 특정 장애 시 `traceId` 기준으로 로그 추적

### 장애 분석 시

1. HTTP 구조화 로그에서 `traceId` 확인
2. 같은 `traceId` 로 일반 로그 검색
3. SQS 발행 로그와 알림 소비 로그를 같은 `traceId` 로 연결
4. 외부 워커 로그도 같은 `traceId` 로 확인

## 권장 추가 작업

현재 수준에서도 기본 추적은 가능하지만, 아래 작업을 추가하면 더 좋아진다.

- 외부 워커 프로세스에도 동일한 MDC/trace 정책 적용
- notification 쪽도 필요하면 구조화 JSON 로그 추가
- SQS 공통 리스너/인터셉터 계층 도입
- `jobId`, `traceId`, `deliveryId` 관계를 README 또는 워커 레포에도 문서화
- 필요 시 OpenTelemetry 기반 분산 추적으로 확장

## 최종 정리

현재 정책의 핵심은 아래 한 줄로 요약할 수 있다.

- 동기 HTTP 구간은 `traceId` 로 추적하고, 비동기 SQS 구간은 그 `traceId` 를 envelope에 실어 다음 프로세스로 넘기며, `jobId` 는 비즈니스 작업 식별자로 별도 유지한다.
