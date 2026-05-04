---
name: cicd-review
description: Lootwise 프로젝트의 GitHub Actions, Docker, 배포 파이프라인을 검토하는 스킬입니다.
---

# CI/CD 리뷰 스킬

Lootwise의 CI/CD, Docker, 배포 workflow를 점검하거나 설계할 때 이 스킬을 사용합니다.

항상 repository 루트의 `AGENTS.md` 규칙을 따릅니다.

## 프로젝트 컨텍스트

- 프로젝트: lootwise
- CI/CD: GitHub Actions
- Infra: Docker 기반 배포
- 백엔드: Kotlin, Spring Boot

## 역할

- build, test, packaging, deploy 단계를 검토합니다.
- secret 관리와 환경 분리를 확인합니다.
- rollback과 실패 원인 분석의 빈틈을 찾습니다.

## 출력 형식

### Build 단계 점검

- Gradle 설정
- JDK version
- dependency caching
- artifact 생성

### Test 단계 점검

- unit test
- integration test
- PostgreSQL service container 또는 test container
- test report 노출 여부

### Deploy 단계 점검

- Docker image build와 push
- 환경별 deployment
- health check
- rollback 전략

### Secret 관리

- 필요한 secret
- least privilege
- log masking과 노출 위험

### Rollback

- image tagging
- 이전 version 복구
- DB migration rollback 주의점

### 실패 원인 분석

- 흔한 실패 지점
- 확인할 log 또는 artifact
- 권장 debugging step

## 규칙

- secret 값을 노출하지 않습니다.
- 재현 가능한 build를 선호합니다.
- DB migration은 deployment risk로 취급합니다.
- health check와 rollback 경로가 없으면 명시적으로 지적합니다.
