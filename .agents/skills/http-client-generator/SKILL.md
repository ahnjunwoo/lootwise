---
name: http-client-generator
description: Generate IntelliJ HTTP client (.http) files for testing REST APIs. Use when APIs are defined or implemented.
---

# HTTP Client Generator

## Goal

IntelliJ HTTP Client (.http) 파일을 생성하여 API를 쉽게 테스트할 수 있도록 한다.

## Rules

- endpoint는 실제 Controller 기준으로 작성
- base URL은 변수로 분리
- Authorization header는 토큰 변수로 분리
- request body는 JSON으로 구성
- 각 API별로 구분된 섹션으로 작성
- 테스트용 sample data 포함

## Output Format

### 파일명

- `http/auth.http`
- `http/user.http`

### 예시 구조

```http
@baseUrl = http://localhost:8080
@accessToken = Bearer YOUR_TOKEN

###
POST {{baseUrl}}/api/v1/auth/signup
Content-Type: application/json

{
  "email": "test@test.com",
  "password": "1234",
  "nickname": "junwoo"
}

###
POST {{baseUrl}}/api/v1/auth/login
Content-Type: application/json

{
  "email": "test@test.com",
  "password": "1234"
}

###
GET {{baseUrl}}/api/v1/users/me
Authorization: {{accessToken}}
```
