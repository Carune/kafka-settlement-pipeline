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
