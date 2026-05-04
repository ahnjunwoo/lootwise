---
name: code-review
description: Lootwise Kotlin/Spring Boot와 PostgreSQL 변경사항을 시니어 백엔드 개발자 관점으로 리뷰하는 스킬입니다.
---

# 코드 리뷰 스킬

Lootwise 백엔드 코드, pull request, local diff를 리뷰할 때 이 스킬을 사용합니다.

항상 repository 루트의 `AGENTS.md` 규칙을 따릅니다.

## 리뷰 우선순위

1. 버그와 동작 회귀
2. 트랜잭션과 동시성 문제
3. nullable 처리 문제
4. 성능과 query 위험
5. PostgreSQL index와 constraint 누락
6. 테스트 누락 또는 약한 테스트
7. 운영 리스크

## 출력 형식

### Findings

- 심각도 순서로 finding을 먼저 작성합니다.
- 가능하면 파일과 line reference를 포함합니다.
- 영향과 구체적인 수정 방향을 설명합니다.

### Open Questions

- 정확성 또는 배포 판단에 영향을 주는 질문만 작성합니다.

### Summary

- 리뷰한 변경사항을 간단히 요약합니다.

### Test Gaps

- 누락된 테스트 또는 남은 위험을 작성합니다.

## 규칙

- finding이 없으면 명확히 없다고 말합니다.
- correctness나 maintainability에 영향이 없는 단순 style 지적은 피합니다.
- Controller에 비즈니스 로직이 들어갔는지 확인합니다.
- DTO와 Entity가 분리되어 있는지 확인합니다.
- `first()`와 `get(0)`을 직접 사용하지 않았는지 확인합니다.
- 외부 Steam 로직이 domain logic과 분리되어 있는지 확인합니다.
