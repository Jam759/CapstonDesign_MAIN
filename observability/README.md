# Observability Stack

이 프로젝트는 로컬에서 실행 중인 Spring 애플리케이션과 WSL `GitWorker` 애플리케이션의 파일 로그와 메트릭을 수집해 Loki, Prometheus, Grafana에서 조회하는 구성을 포함합니다.

## 구성

- Spring Boot: `./.logs/application.log`, `./.logs/structured-http.log` 로 로그 출력
- GitWorker: `~/workSpace/CapstonDesign_Worker/.logs/application.log`, `~/workSpace/CapstonDesign_Worker/.logs/structured-http.log` 로 로그 출력
- Alloy: 로컬 로그 파일 수집 후 Loki로 전송
- Loki: 로그 저장소
- Prometheus: 로컬 Spring 앱의 `/actuator/prometheus` 와 GitWorker의 `/metrics` 스크랩
- Grafana: Loki/Prometheus 데이터소스와 기본 대시보드 자동 프로비저닝

## 실행

1. Spring 애플리케이션을 로컬에서 실행합니다.
2. WSL `GitWorker` 애플리케이션을 로컬에서 실행합니다.
3. 저장소 루트에서 아래 명령으로 관측 스택만 띄웁니다.

```powershell
docker compose -f .\Docker-Compose.yaml up -d loki alloy prometheus grafana
```

4. Spring 애플리케이션을 재시작해 `/actuator/prometheus` endpoint 노출 설정을 반영합니다.
5. 두 애플리케이션에 요청 또는 작업을 발생시켜 로그 파일이 생성되도록 합니다.

## 접속 정보

- Grafana: `http://localhost:3001`
- Loki: `http://localhost:3100`
- Prometheus: `http://localhost:9090`
- Alloy UI: `http://localhost:12345`

Grafana 기본 계정:

- ID: `admin`
- Password: `admin`

## 수집 로그

- Spring 구조화 HTTP 로그: `job="capstone-structured-http"`
- Spring 일반 애플리케이션 로그: `job="capstone-application"`
- GitWorker 구조화 HTTP 로그: `job="git-worker-structured-http"`
- GitWorker 일반 애플리케이션 로그: `job="git-worker-application"`
- Prometheus scrape target: `job="capstone-spring"`
- Prometheus scrape target: `job="git-worker"`

## 참고

- Grafana 대시보드는 `Capstone / Capstone Logging Overview`, `Capstone / Capstone Metrics Overview`, `Capstone / GitWorker Logging Overview`, `Capstone / GitWorker Metrics Overview`, `Capstone / Capstone Unified Overview` 로 자동 생성됩니다.
- 로깅 정책과 SQS trace 계약은 [LOGGING_POLICY.md](/C:/Users/3379p/OneDrive/Desktop/intelliJ/CapstoneDesign/observability/LOGGING_POLICY.md) 에 정리되어 있습니다.
- 로그 파일이 비어 있으면 Alloy는 새 로그가 생길 때부터 수집합니다.
- Prometheus는 기본적으로 `host.docker.internal:8080/actuator/prometheus` 를 15초 간격으로 스크랩합니다.
- Prometheus는 GitWorker도 `host.docker.internal:3000/metrics` 로 15초 간격 스크랩합니다.
- Prometheus target이 `401` 이면, 로컬 Spring 앱을 재시작해 새 actuator/security 설정을 반영해야 합니다.
- GitWorker는 WSL `GitWorker` 배포판의 `~/workSpace/CapstonDesign_Worker` 경로를 Docker가 read-only로 마운트해 `.logs` 를 수집합니다.
- 대시보드 상단 변수로 바로 필터링할 수 있습니다.
- `methodRegex`: `GET`, `POST|PATCH`, `.*`
- `statusRegex`: `2..`, `4..`, `500`, `.*`
- `pathFilter`: `/api/v1/auth` 같은 경로 부분 문자열
- `traceId`: 특정 요청 추적용 trace ID
- `minDurationMs`: Slow Requests 패널 기준값

## Metrics 예시

- 전체 요청률: `sum(rate(http_server_requests_seconds_count{application="capstone-main"}[5m]))`
- 5xx 비율: `100 * sum(rate(http_server_requests_seconds_count{application="capstone-main",status=~"5.."}[5m])) / sum(rate(http_server_requests_seconds_count{application="capstone-main"}[5m]))`
- P95 지연시간: `1000 * histogram_quantile(0.95, sum by (le) (rate(http_server_requests_seconds_bucket{application="capstone-main"}[5m])))`
- JVM heap 사용률: `100 * sum(jvm_memory_used_bytes{application="capstone-main",area="heap"}) / sum(jvm_memory_max_bytes{application="capstone-main",area="heap"} > 0)`

## Explore 예시

- 구조화 HTTP 로그 전체: `{job="capstone-structured-http"}`
- 에러 로그만: `{job="capstone-structured-http", level="ERROR"}`
- 특정 traceId 조회: `{job="capstone-structured-http"} |= "trace-123"`
- 일반 애플리케이션 로그: `{job="capstone-application"}`
- GitWorker 일반 애플리케이션 로그: `{job="git-worker-application"}`
- GitWorker 구조화 HTTP 로그: `{job="git-worker-structured-http"}`
