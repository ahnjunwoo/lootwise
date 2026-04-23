# Render Deployment Guide

이 문서는 Render로 Lootwise 백엔드를 배포하기 위한 가이드입니다.

구성:
- Backend: Render Web Service
- Database: Render PostgreSQL
- Build: Dockerfile
- Config: `render.yaml`
- CI: GitHub Actions `backend-ci.yml`

## 1. Render 계정 준비

Render에 가입하고 GitHub 저장소를 연결합니다.

## 2. Blueprint로 배포

Render Dashboard에서:

1. `New`
2. `Blueprint`
3. GitHub 저장소 선택
4. 루트의 `render.yaml` 사용
5. Apply

`render.yaml`은 아래 리소스를 생성합니다.

- `lootwise-backend`: Docker 기반 Web Service
- `lootwise-postgres`: PostgreSQL 16 database

## 3. Render 환경 변수

`render.yaml`에서 대부분 자동 설정합니다.

자동 연결:
- `DB_HOST`
- `DB_PORT`
- `DB_NAME`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

직접 기본값 지정:
- `SPRING_PROFILES_ACTIVE=render`
- `STEAM_API_BASE_URL=https://store.steampowered.com`
- `STEAM_API_REVIEWS_BASE_URL=https://store.steampowered.com`
- `STEAM_API_COUNTRY_CODE=KR`
- `STEAM_API_LANGUAGE=koreana`
- `STEAM_API_REVIEW_SYNC_LIMIT=20`
- `STEAM_BATCH_COLLECT_CRON=0 0 */3 * * *`

## 4. Spring Boot 연결 방식

Render에서는 `application-render.yml`이 사용됩니다.

```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}
    username: ${SPRING_DATASOURCE_USERNAME}
    password: ${SPRING_DATASOURCE_PASSWORD}
```

Render Blueprint는 PostgreSQL의 host, port, database, user, password를 각각 환경 변수로 주입합니다.

## 5. 배포 후 확인

Render Web Service URL에서 확인합니다.

```bash
curl https://<your-render-service>.onrender.com/actuator/health
curl https://<your-render-service>.onrender.com/api/v1/deals
```

## 6. 주의사항

- Free plan은 cold start가 있을 수 있습니다.
- 첫 요청이 느릴 수 있습니다.
- 서버 기동 직후 `StartupDealSyncRunner`가 Steam 데이터를 1회 수집합니다.
- 데이터 수집이 끝나기 전에는 `/api/v1/deals`가 빈 배열일 수 있습니다.
- Render PostgreSQL free plan은 제한이 있으므로 장기 운영 전에는 정책을 확인해야 합니다.

## 7. 로컬 개발과 차이

로컬:
- `docker-compose.yml`로 PostgreSQL 실행
- `application.yml` 사용

Render:
- Render PostgreSQL 사용
- `application-render.yml` 사용
- `SPRING_PROFILES_ACTIVE=render`

## 8. GitHub Actions 역할

현재 GitHub Actions는 CI만 담당합니다.

- PR 생성 시 `./gradlew test build`
- `main` push 시 `./gradlew test build`

Render 배포는 Render가 GitHub 저장소 변경을 감지해서 처리합니다.
