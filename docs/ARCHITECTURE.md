# 아키텍처 골격

## 처리 흐름

1. `settlement-batch`가 일 정산을 계산하고 정산 원장 + 아웃박스 이벤트를 같은 트랜잭션으로 저장합니다.
2. `outbox-publisher`가 미발행 아웃박스 이벤트를 조회해 Kafka 토픽으로 발행합니다.
3. `adjustment-consumer`가 `settlement.adjustment.v1`을 구독해 보정 로직을 수행합니다.
4. 보정 실패 건은 재시도 토픽으로 이동하고, 재시도 한도를 넘기면 DLQ로 이동합니다.

## 초기 범위

- 인프라는 단순하게 유지: Postgres 1개 + Kafka 브로커 1개
- 토픽 수를 작게 유지
- 전달 보장은 `at-least-once`, 소비자는 `idempotent` 기준으로 구현
