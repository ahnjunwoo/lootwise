---
name: spring-kotlin-implementation
description: Lootwise 백엔드 기능을 프로젝트 아키텍처 규칙에 맞춰 Kotlin/Spring Boot로 구현하는 스킬입니다.
---

# Spring Kotlin 구현 스킬

Lootwise 백엔드 코드를 Kotlin과 Spring Boot로 구현할 때 이 스킬을 사용합니다.

항상 repository 루트의 `AGENTS.md` 규칙을 따릅니다.

## 프로젝트 컨텍스트

- 프로젝트: lootwise
- 백엔드: Kotlin, Spring Boot
- DB: PostgreSQL
- 테스트를 추가할 때는 가능하면 Kotest를 사용합니다.

## 역할

- 유스케이스 중심의 Service를 구현합니다.
- Controller에 비즈니스 로직을 넣지 않습니다.
- Entity와 API DTO를 분리합니다.
- nullable 값을 명시적으로 처리합니다.
- 외부 Steam API, 크롤링, 가격 수집 로직은 adapter 계층에 둡니다.

## 구현 규칙

- Controller에는 비즈니스 로직을 작성하지 않습니다.
- Entity를 API DTO로 직접 노출하지 않습니다.
- `first()` 또는 `get(0)`을 직접 사용하지 않습니다.
- `firstOrNull()`, `getOrNull()`, 명시적 validation, 의도가 드러나는 repository method를 선호합니다.
- Kotlin idiomatic style을 사용합니다.
- 필요한 경우 Spring transaction annotation으로 트랜잭션 경계를 명확히 합니다.
- 요청받은 기능 범위 안에서만 변경합니다.

## 수정 전 출력 형식

### 구현 계획

- 추가 또는 수정할 파일
- 주요 class와 책임
- 트랜잭션 경계
- error handling 전략
- test plan

## 수정 후 출력 형식

### 수정 파일 목록

- 변경된 모든 파일을 나열합니다.

### 핵심 변경 이유

- 중요한 변경이 필요한 이유를 설명합니다.

### 테스트 방법

- 실행한 command와 결과를 작성합니다.

### 위험 요소

- migration, 외부 의존성, 동시성, nullability, 성능 위험을 기록합니다.
