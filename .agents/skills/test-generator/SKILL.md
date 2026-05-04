---
name: test-generator
description: Lootwise Kotlin/Spring Boot 백엔드 기능의 Kotest 기반 테스트를 설계하고 작성하는 스킬입니다.
---

# 테스트 생성 스킬

Lootwise 백엔드 기능의 테스트를 설계하거나 작성할 때 이 스킬을 사용합니다.

항상 repository 루트의 `AGENTS.md` 규칙을 따릅니다.

## 프로젝트 컨텍스트

- 프로젝트: lootwise
- 백엔드: Kotlin, Spring Boot
- 선호 테스트 스타일: Kotest
- DB: PostgreSQL

## 역할

- 의미 있는 unit, slice, integration test를 식별합니다.
- 정상 케이스, 예외 케이스, 실패 동작을 검증합니다.
- 외부 Steam API, 크롤링, 알림 제공자, clock은 필요하면 mock 처리합니다.

## 출력 형식

### 테스트 대상

- 테스트할 class, function, endpoint, repository, job을 정리합니다.

### 정상 케이스

- 성공 흐름과 assertion을 작성합니다.

### 예외 케이스

- 잘못된 입력, 중복 record, 누락 데이터, null 값, 외부 장애, 권한 문제를 포함합니다.

### Mock 대상

- external adapter, repository, clock, event publisher, notification sender를 식별합니다.

### 통합 테스트 필요 여부

- repository/API/container 기반 integration test가 필요한지 판단합니다.
- 필요한 이유 또는 생략 가능한 이유를 설명합니다.

## 규칙

- 프로젝트가 Kotest를 지원하거나 Kotlin 코드를 테스트한다면 Kotest style을 선호합니다.
- 구현 세부사항에 과하게 의존하는 fragile assertion은 피합니다.
- 의미 있는 위험이 있으면 transaction과 DB constraint 동작을 검증합니다.
- 버그 수정에는 regression test를 포함합니다.
