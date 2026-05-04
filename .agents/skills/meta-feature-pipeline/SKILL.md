---
name: meta-feature-pipeline
description: Lootwise 기능 작업을 요구사항 분석부터 설계, 구현, 테스트, 리뷰까지 순서대로 진행하는 오케스트레이션 스킬입니다.
---

# 메타 기능 파이프라인 스킬

Lootwise 기능을 end-to-end로 진행해야 할 때 이 스킬을 사용합니다.

항상 repository 루트의 `AGENTS.md` 규칙을 따릅니다.

## 목적

기능 작업을 다음 순서로 조율합니다.

1. 요구사항 분석
2. 백엔드 아키텍처 설계
3. API 설계
4. PostgreSQL DB 설계
5. Spring Kotlin 구현
6. 테스트 생성
7. 코드 리뷰
8. HTTP client 생성
9. FE API 문서 생성

## 필수 동작

- 실제 코드 수정 전에는 반드시 구현 계획을 먼저 출력합니다.
- 구현 계획을 보여주기 전에는 source code를 수정하지 않습니다.
- 외부 Steam API, 크롤링, 가격 수집 로직은 adapter 경계에 둡니다.
- Controller에는 비즈니스 로직을 넣지 않습니다.
- Entity와 API DTO를 분리합니다.
- nullable 값을 명시적으로 처리합니다.
- `first()` 또는 `get(0)`을 직접 사용하지 않습니다.

## 코드 수정 전 출력 형식

### 요구사항 요약

- 기능 목표
- 사용자 시나리오
- 정책
- 예외 케이스
- 모호한 점과 가정

### 설계 요약

- 도메인 모델
- Service/use-case 구조
- API 형태
- DB 변경
- 외부 adapter 영향
- 트랜잭션 경계

### 구현 계획

- 생성 또는 수정할 파일
- class별 책임
- validation과 error handling
- test plan
- rollout 또는 migration 주의점

## 코드 수정 후 출력 형식

### 수정 파일 목록

- 변경된 파일을 나열합니다.

### 핵심 변경 이유

- 주요 기술적 결정을 설명합니다.

### 테스트 방법

- 실행한 command와 결과를 작성합니다.

### 위험 요소

- 남은 위험, 가정, migration 주의점을 작성합니다.

## 스킬 실행 순서

- `requirement-analysis`로 시작합니다.
- 기능이 Steam 게임, 할인, 가격, 관심 게임, 알림, 랭킹 개념을 다루면 `steam-domain-analysis`를 사용합니다.
- 그다음 `backend-architecture`를 사용합니다.
- 그다음 `api-design`를 사용합니다.
- persistence 변경이 필요하면 `postgres-db-design`를 사용합니다.
- 구현 계획이 출력된 뒤에만 `spring-kotlin-implementation`을 사용합니다.
- 그다음 `test-generator`를 사용합니다.
- 마지막으로 `code-review`를 사용합니다.
- 코드 리뷰 후 API endpoint가 생성 또는 변경되었다면 `http-client-generator`를 사용합니다.
- HTTP client 생성 후 FE 공유가 필요한 API라면 `frontend-api-doc-generator`를 사용합니다.
- build, Docker, GitHub Actions, 배포, secret, runtime operation에 영향이 있을 때만 `cicd-review`를 사용합니다.
