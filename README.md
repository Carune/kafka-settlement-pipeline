# Kafka 정산 파이프라인 (프로젝트 골격)

일 정산 배치와 Kafka 기반 보정 흐름을 빠르게 시작하기 위한 포트폴리오용 기본 구조입니다.

## 모듈 구성

- `apps/settlement-batch`: 일 정산 계산과 아웃박스 저장 지점
- `apps/outbox-publisher`: 아웃박스 테이블을 조회해 Kafka로 발행
- `apps/adjustment-consumer`: 보정 이벤트를 소비하고 재시도/DLQ 흐름 처리

## 로컬 인프라

`docker-compose.yml`로 아래 구성을 실행합니다.

- PostgreSQL (`localhost:5432`)
- Kafka (KRaft, 외부 리스너 `localhost:19092`)
- Kafka UI (`http://localhost:8080`)

## 권장 토픽

- `settlement.adjustment.v1`
- `settlement.adjustment.retry.5m.v1`
- `settlement.adjustment.dlq.v1`
- `settlement.completed.v1`

## 브랜치 전략

기능 단위 브랜치 전략은 `docs/GIT_WORKFLOW.md`에 정리되어 있습니다.

## 현재 구현 범위 (1차)

- `settlement-batch`가 `businessDate` 기준으로 거래 데이터를 가맹점 단위 집계
- 정산 원장(`settlement_ledger`) 저장
- 차액이 있는 건만 아웃박스(`outbox_event`) 생성
- 실행 API: `POST /batch/settlement?businessDate=YYYY-MM-DD`

## 로컬 테스트 순서

1. `docker compose up -d`
2. IDE에서 `settlement-batch` 애플리케이션 실행
3. `POST http://localhost:8081/batch/settlement?businessDate=<어제 날짜>`
4. PostgreSQL에서 `settlement_ledger`, `outbox_event` 테이블 확인
