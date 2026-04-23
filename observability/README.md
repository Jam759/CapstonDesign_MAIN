# Observability Stack

This project ships a local observability stack for:

- Spring application logs and metrics
- GitWorker logs and metrics
- Loki, Prometheus, Alloy, and Grafana

## Start

1. Start the Spring application locally.
2. Start GitWorker locally.
3. Start the observability stack:

```powershell
docker compose -f .\Docker-Compose.yaml up -d loki alloy prometheus grafana
```

## Endpoints

- Grafana: `http://localhost:3001`
- Loki: `http://localhost:3100`
- Prometheus: `http://localhost:9090`
- Alloy UI: `http://localhost:12345`

Grafana default credentials:

- ID: `admin`
- Password: `admin`

## Dashboard

Grafana provisions a single dashboard:

- `Capstone / Capstone Observability Overview`

Use the dashboard variables at the top to filter by:

- `traceId`
- `mainUriRegex`
- `mainPathFilter`
- `workerEventTypeRegex`
- `workerContainsText`
- `minDurationMs`

## Log Jobs

- Main structured HTTP logs: `job="capstone-structured-http"`
- Main application logs: `job="capstone-application"`
- GitWorker structured HTTP logs: `job="git-worker-structured-http"`
- GitWorker application logs: `job="git-worker-application"`

## Metrics Targets

- Spring Prometheus target: `job="capstone-spring"`
- GitWorker Prometheus target: `job="git-worker"`

## Notes

- Spring writes logs to `./.logs/application.log` and `./.logs/structured-http.log`.
- GitWorker writes logs to `./.worker-logs/application.log` and `./.worker-logs/structured-http.log`.
- Alloy reads `./.worker-logs` directly from the Windows workspace.
- Alloy drops `DEBUG` from `git-worker-application` before sending logs to Loki.
- Prometheus scrapes Spring from `host.docker.internal:8080/actuator/prometheus`.
- Prometheus scrapes GitWorker from the current WSL IP configured in `observability/prometheus/prometheus.yml`.
- Logging policy and SQS trace rules are documented in [LOGGING_POLICY.md](./LOGGING_POLICY.md).
