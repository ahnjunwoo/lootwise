# Steam Deals Hub

Steam 할인 게임 정보를 수집하고, 할인율과 리뷰 지표를 기준으로 구매할 만한 게임을 빠르게 찾을 수 있게 도와주는 백엔드 서비스입니다.

## 목표

- 현재 할인 중인 Steam 게임 목록 제공
- 할인율, 리뷰 요약, 가격 정보를 기반으로 정렬/조회
- 주기적으로 Steam 데이터를 수집하여 최신 할인 정보를 유지
- 이후 프론트엔드 또는 알림 기능과 연결 가능한 API 제공

## MVP 범위

- 할인 중인 게임 목록 조회 API
- 게임 상세 조회 API
- Steam 앱 목록 수집
- 할인 정보 및 가격 스냅샷 저장
- 리뷰 요약 정보 저장
- 3시간 주기 데이터 수집 배치

## 기술 스택

- Kotlin
- Spring Boot
- Spring Web
- Spring Data JPA
- PostgreSQL
- Flyway
- Kotest

## 프로젝트 구조

```text
src/main/kotlin/com/junwoo/steamdealshub
├── common        # 공통 설정, 예외, 유틸
├── steam         # Steam API 연동
├── deal          # 할인 게임 조회/추천 도메인
└── batch         # 스케줄러 및 수집 작업