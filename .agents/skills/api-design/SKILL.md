---
name: api-design
description: Lootwise Kotlin/Spring Boot 백엔드 기능을 위한 REST API 설계 스킬입니다.
---

# API 설계 스킬

Lootwise 기능의 REST endpoint를 설계할 때 이 스킬을 사용합니다.

항상 repository 루트의 `AGENTS.md` 규칙을 따릅니다.

## 프로젝트 컨텍스트

- 프로젝트: lootwise
- 백엔드: Kotlin, Spring Boot
- API 스타일: REST
- Entity와 API DTO는 반드시 분리합니다.

## 역할

- 사용자 행동과 백엔드 유스케이스를 반영하는 endpoint를 설계합니다.
- request/response DTO를 정의합니다.
- validation, status code, error code를 명시합니다.
- persistence entity를 API에 직접 노출하지 않습니다.

## API 스펙 관리 원칙

- OpenAPI YAML을 공식 API 계약으로 관리합니다.
- API 추가/변경 시 `docs/openapi.yaml`, `docs/API_SPEC.md`, `docs/frontend-types.ts`, `http/*.http`를 함께 갱신합니다.
- Swagger UI에서 확인 가능한 형태를 유지합니다.
- REST Docs는 필수로 도입하지 않습니다.
- HTTP 테스트는 API 계약 검증 용도로 유지합니다.

## 출력 형식

### Endpoint

- path
- method
- 목적

### Request

- path variable
- query parameter
- request body
- validation rule

### Response

- 성공 response body
- pagination 또는 sorting 필드가 필요하면 포함
- nullable 필드와 그 의미

### Status Code

- 성공 status
- client error status
- server 또는 dependency error status

### Validation

- 필수 필드
- 범위, enum, format, 소유권, 중복 제약

### Error Code

- 안정적인 application error code
- HTTP status
- 사용자 또는 개발자용 message
- 필요 시 복구 방법

## 규칙

- Controller는 request parsing, validation 위임, service 호출, response mapping만 담당합니다.
- JPA Entity 또는 persistence model을 API response로 직접 노출하지 않습니다.
- DTO의 nullability를 명시합니다.
- 예측 가능한 URL naming과 일관된 복수 resource 이름을 선호합니다.
