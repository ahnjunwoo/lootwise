# AGENTS.md

## Project
Lootwise는 Steam 게임 할인/가격 정보를 수집하고 게임에 대한 공략 및 정보를 제공하는 개인 토이프로젝트이다.

## Stack
- Backend: Kotlin, Spring Boot
- DB: PostgreSQL
- CI/CD: GitHub Actions
- Infra: Docker 기반 배포
- Frontend: 추후 개발 예정

## Coding Rules
- Kotlin idiomatic style 사용
- Controller에는 비즈니스 로직 금지
- Service는 유스케이스 중심으로 작성
- Entity와 API DTO 분리
- nullable은 명시적으로 처리
- first(), get(0) 직접 사용 금지
- DB 쿼리는 PostgreSQL 기준으로 작성
- 테스트는 가능하면 Kotest 기준

## Architecture Rules
- 기능 추가 전 요구사항을 먼저 정리한다
- 도메인 모델 → API → DB → 테스트 순서로 설계한다
- 외부 API 연동은 adapter 계층으로 분리한다
- Steam API / 할인 크롤링 / 가격 수집 로직은 도메인 로직과 분리한다

## Output Rules
- 변경 전 설계 요약
- 수정 파일 목록
- 핵심 변경 이유
- 테스트 방법
- 위험 요소