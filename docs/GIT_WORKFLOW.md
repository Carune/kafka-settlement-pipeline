# Git 작업 규칙

이 프로젝트는 기능 단위 브랜치 전략을 사용합니다.

## 브랜치 구성

- `main`: 배포 기준 스냅샷만 반영
- `develop`: 완료된 기능 통합 브랜치
- `feature/<scope>-<short-summary>`: 기능 1개당 브랜치 1개
- `bugfix/<scope>-<short-summary>`: 버그 수정 브랜치

## 작업 원칙

1. `develop`에서 분기합니다.
2. 브랜치당 하나의 기능 목표만 다룹니다.
3. 커밋은 작고 의도가 분명하게 작성합니다.
4. 로컬 검증 후 `develop` 대상으로 PR을 엽니다.
5. 릴리스 시점에만 `develop`을 `main`으로 반영합니다.

## 포트폴리오 Step 브랜치 규칙

- 권장 브랜치명: `feature/step-<nn>-<summary>`
- 예시:
  - `feature/step-01-consumer-idempotency`
  - `feature/step-02-retry-backoff`
  - `feature/step-03-dlq-replay`

## Step 작업 템플릿

1. Step 목표를 README에 3줄로 정의
2. 코드 변경은 가능한 한 한 모듈에서 시작
3. 테스트/실행 검증 결과를 PR 본문에 첨부
4. 다음 Step을 위한 TODO를 1~2개만 남기기
