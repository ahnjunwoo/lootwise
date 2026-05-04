---
name: postgres-db-design
description: Lootwise의 게임, 가격, 할인, 관심 게임, 알림, 랭킹 기능을 PostgreSQL 기준으로 설계하는 스킬입니다.
---

# PostgreSQL DB 설계 스킬

Lootwise 기능의 PostgreSQL 테이블, 인덱스, 제약 조건, migration 전략을 설계할 때 이 스킬을 사용합니다.

항상 repository 루트의 `AGENTS.md` 규칙을 따릅니다.

## 프로젝트 컨텍스트

- 프로젝트: lootwise
- DB: PostgreSQL
- 도메인: Steam 게임, 할인, 가격 히스토리, 관심 게임, 알림, 랭킹

## 역할

- PostgreSQL 기준의 schema design을 작성합니다.
- 도메인 불변 조건을 보호하는 constraint를 정의합니다.
- 실제 조회 패턴을 기준으로 index를 설계합니다.
- migration과 backfill 위험을 식별합니다.

## 출력 형식

### DDL

- 새 테이블 또는 변경 테이블의 PostgreSQL DDL을 제공합니다.
- column type, nullability, default, timestamp 전략을 포함합니다.

### Index

- index 목록과 목적을 작성합니다.
- 각 index가 지원하는 조회 패턴을 설명합니다.

### Unique Constraint

- Steam app ID, user-game pair, price snapshot 같은 자연스러운 uniqueness를 정의합니다.

### FK

- foreign key, delete behavior, ownership constraint를 정의합니다.

### 조회 패턴

- 일반적인 read, filter, sort order, pagination, aggregation 요구를 설명합니다.

### Migration 주의점

- data backfill, lock risk, nullable-to-not-null rollout, 대용량 index 생성, rollback 이슈를 식별합니다.

## 규칙

- SQL은 PostgreSQL 기준으로 작성합니다.
- 가능하면 application-only 중복 방지보다 DB constraint를 선호합니다.
- DB 설계는 Entity/API 분리 원칙과 맞춥니다.
- 스케줄 기반 수집 테이블은 멱등 insert 필요성을 명시합니다.
