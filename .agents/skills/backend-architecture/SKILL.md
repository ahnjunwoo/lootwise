---
name: backend-architecture
description: Lootwise 도메인, 서비스, 어댑터, 트랜잭션 경계를 Kotlin/Spring Boot 기준으로 설계하는 스킬입니다.
---

# 백엔드 아키텍처 스킬

Lootwise 기능의 API, DB, 구현 작업 전에 백엔드 구조를 설계할 때 이 스킬을 사용합니다.

항상 repository 루트의 `AGENTS.md` 규칙을 따릅니다.

## 프로젝트 컨텍스트

- 프로젝트: lootwise
- 백엔드: Kotlin, Spring Boot
- DB: PostgreSQL
- 외부 의존성 후보: Steam API, Steam store 페이지, 크롤링 작업, 알림 제공자

## 역할

- 유스케이스 중심의 Service를 설계합니다.
- Controller를 얇게 유지합니다.
- Entity, 도메인 모델, API DTO의 책임을 분리합니다.
- 외부 API, 크롤링, 가격 수집 로직은 adapter 경계로 격리합니다.

## 출력 형식

### 도메인 모델

- aggregate 또는 모델 후보를 정의합니다.
- 주요 필드, 불변 조건, 관계를 설명합니다.
- Steam 원천 데이터와 Lootwise 내부 사용자 데이터를 명확히 분리합니다.

### 패키지 구조

- 기존 코드베이스와 일관된 패키지 구조를 제안합니다.
- 필요한 경우에만 controller, service, domain, repository, adapter, dto, config 패키지를 포함합니다.

### 트랜잭션 경계

- 읽기 전용 트랜잭션과 쓰기 트랜잭션의 경계를 정의합니다.
- 멱등성, 중복 방지, 동시성 처리가 필요한 지점을 식별합니다.

### 외부 API 의존성

- Steam API, 크롤링, 알림 등 외부 의존성을 나열합니다.
- adapter interface와 fallback 동작을 정의합니다.

### 장애/재시도 전략

- timeout, retry, circuit breaker, 부분 실패, 로깅 전략을 설명합니다.
- 즉시 실패해야 하는 작업과 비동기로 재시도 가능한 작업을 구분합니다.

## 규칙

- Controller에는 비즈니스 로직을 넣지 않습니다.
- Service는 단순 CRUD 묶음이 아니라 유스케이스 중심으로 나눕니다.
- 외부 Steam 연동 로직은 core domain logic과 섞지 않습니다.
- Service, Adapter, Repository, API 계층별 테스트 경계를 언급합니다.
