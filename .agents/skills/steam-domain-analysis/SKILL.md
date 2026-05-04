---
name: steam-domain-analysis
description: Lootwise의 Steam 게임, 할인, 가격, 관심 게임, 알림, 랭킹 도메인을 분석하는 스킬입니다.
---

# Steam 도메인 분석 스킬

Lootwise의 Steam 관련 개념을 모델링할 때 이 스킬을 사용합니다.

항상 repository 루트의 `AGENTS.md` 규칙을 따릅니다.

## 프로젝트 컨텍스트

- 프로젝트: lootwise
- 도메인: Steam 게임 할인, 가격, 관심 게임, 알림, 랭킹
- 기존 기능: 기본 Steam 할인 목록 조회
- 예정 기능: 관심 게임, 가격 히스토리, 할인 알림, 랭킹

## 역할

- Steam 원천 데이터와 Lootwise 소유 사용자 데이터를 분리합니다.
- 도메인 모델과 불변 조건을 식별합니다.
- 크롤링, Steam API 호출, 가격 수집은 core domain logic 밖에 둡니다.

## 출력 형식

### Game

- Steam app identity
- 제목과 metadata
- store 노출 여부와 source update 규칙

### Discount

- 할인율
- 정가와 최종가
- 통화
- 알 수 있는 경우 할인 기간
- snapshot 모델과 current-state 모델 구분

### PriceHistory

- 가격 snapshot 시각
- 통화
- 정가, 최종가, 할인율
- 멱등성과 중복 제거 규칙

### Wishlist

- 사용자가 추적하는 게임의 소유권
- 중복 방지
- 노출 방식과 정렬

### AlertCondition

- 목표 할인율 또는 목표 가격
- 통화 처리
- trigger 주기와 중복 알림 방지

### Ranking

- 할인율, 인기도, 최저가, 최근 변동, 관심 게임 수 같은 ranking 기준
- 집계 window
- tie-breaking rule

## 규칙

- Steam 데이터 가용성에 대한 가정을 명시합니다.
- 현재 가격과 과거 가격을 구분합니다.
- 사용자별 기능은 public Steam game data와 분리해서 모델링합니다.
- PostgreSQL constraint 또는 index가 필요한 지점을 식별합니다.
