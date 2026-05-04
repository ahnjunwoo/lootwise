---
name: frontend-api-doc-generator
description: Generate frontend-facing API documentation from implemented Lootwise Controllers and DTOs.
---

# Frontend API Doc Generator

## Goal

FE 개발자가 바로 사용할 수 있는 API 공유 문서를 생성한다.

## When To Use

- API가 설계 또는 구현되었을 때
- Controller/DTO 변경 후 FE에 계약을 공유해야 할 때
- 인증 header, request/response JSON, error response를 명확히 전달해야 할 때

## Rules

- 실제 Controller endpoint 기준으로 작성한다.
- Entity를 문서에 직접 노출하지 않는다.
- request/response DTO 기준으로 JSON field를 작성한다.
- `password`, `passwordHash`, token secret 같은 민감 정보는 response 예시에 포함하지 않는다.
- 인증 필요 여부를 endpoint별로 명시한다.
- JWT 인증이 필요한 API는 `Authorization: Bearer <accessToken>` header를 명시한다.
- HTTP status code와 error response를 정리한다.
- FE가 바로 복사해 쓸 수 있는 sample request/response를 포함한다.
- 현재 구현과 문서가 다르면 차이점을 먼저 적는다.

## Output Targets

프로젝트 상황에 맞춰 필요한 문서를 갱신한다.

- `docs/API_SPEC.md`: 사람이 읽기 쉬운 FE 공유 문서
- `docs/openapi.yaml`: OpenAPI 계약 문서
- `docs/frontend-types.ts`: FE 참고용 TypeScript 타입

기본 우선순위는 다음과 같다.

1. `docs/API_SPEC.md`
2. `docs/openapi.yaml`
3. `docs/frontend-types.ts`

## Required Sections

### 변경/검토한 Controller

- Controller class
- base path
- endpoint 목록

### API 스펙

각 endpoint별로 작성한다.

- Method
- Path
- Auth 필요 여부
- Request headers
- Path variables
- Query parameters
- Request body
- Response body
- Status codes
- Error responses
- FE 구현 메모

### TypeScript 타입

필요하면 FE가 사용할 수 있는 타입을 제공한다.

```ts
export interface ExampleResponse {
  id: number;
}
```

### 보안 확인

- 민감 필드가 response에 포함되지 않는지 확인한다.
- 인증이 필요한 API가 permitAll로 열려 있지 않은지 확인한다.

## Final Response

- 생성/수정한 문서 파일 목록
- 핵심 문서화 내용
- FE에 전달할 주의사항
- 남은 불일치 또는 TODO
