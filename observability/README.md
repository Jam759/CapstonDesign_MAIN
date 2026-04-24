# Observability Stack

This project ships a local observability stack for:

- Spring structured logs and metrics
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

Grafana provisions these dashboards:

- `Capstone / Capstone Observability Overview`
- `Capstone / Capstone Main Server`
- `Capstone / Capstone Worker Server`

Common dashboard filters:

- `traceId`
- `minDurationMs`

Main server dashboard filters:

- `mainUriRegex`
- `mainPathFilter`
- `mainLogContainsText`

Worker server dashboard filters:

- `workerEventTypeRegex`
- `workerContainsText`

## Log Jobs

- Main structured HTTP logs: `job="capstone-structured-http"`
- Main structured app logs: `job="capstone-structured-app"`
- GitWorker structured HTTP logs: `job="git-worker-structured-http"`

## Metrics Targets

- Spring Prometheus target: `job="capstone-spring"`
- GitWorker Prometheus target: `job="git-worker"`

## Notes

- Spring writes structured logs to `./.logs/structured-http.log` and `./.logs/structured-app.log`.
- GitWorker writes logs to `./.worker-logs/structured-http.log`.
- Alloy reads `./.worker-logs` directly from the Windows workspace.
- Prometheus scrapes Spring from `host.docker.internal:8080/actuator/prometheus`.
- Prometheus scrapes GitWorker from the current WSL IP configured in `observability/prometheus/prometheus.yml`.
- Logging policy and SQS trace rules are documented in [LOGGING_POLICY.md](./LOGGING_POLICY.md).
