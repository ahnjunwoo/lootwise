# Lootwise API Spec

프론트엔드 개발 전달용 문서입니다.

## Base URL

- Local: `http://localhost:8080`

## Endpoints

### 1. Get Top Deals

- Method: `GET`
- Path: `/api/v1/deals`

#### Query Parameters

- `limit`: `number`
  - optional
  - default: `20`
  - min: `1`
  - max: `100`

#### Response `200 OK`

```json
[
  {
    "appId": 570,
    "name": "Dota 2",
    "originalPrice": 19900,
    "finalPrice": 9900,
    "discountPercent": 50,
    "reviewScoreDesc": "Very Positive",
    "steamUrl": "https://store.steampowered.com/app/570",
    "capsuleImageUrl": "https://cdn.cloudflare.steamstatic.com/steam/apps/570/header.jpg"
  }
]
```

#### Response Fields

- `appId`: `number`
- `name`: `string`
- `originalPrice`: `number | null`
- `finalPrice`: `number`
- `discountPercent`: `number`
- `reviewScoreDesc`: `string | null`
- `steamUrl`: `string`
- `capsuleImageUrl`: `string | null`

#### Error `400 Bad Request`

```json
{
  "message": "must be greater than or equal to 1",
  "timestamp": "2026-04-20T08:30:00Z"
}
```

### 2. Get Deal Detail

- Method: `GET`
- Path: `/api/v1/deals/{appId}`

#### Path Parameters

- `appId`: `number`
  - required
  - positive only

#### Response `200 OK`

```json
{
  "appId": 570,
  "name": "Dota 2",
  "originalPrice": 19900,
  "finalPrice": 9900,
  "discountPercent": 50,
  "reviewScoreDesc": "Very Positive",
  "steamUrl": "https://store.steampowered.com/app/570",
  "capsuleImageUrl": "https://cdn.cloudflare.steamstatic.com/steam/apps/570/header.jpg"
}
```

#### Error `404 Not Found`

```json
{
  "message": "Discounted deal not found for appId=999999999",
  "timestamp": "2026-04-20T08:30:00Z"
}
```

## Frontend Types

```ts
export type DealSummary = {
  appId: number;
  name: string;
  originalPrice: number | null;
  finalPrice: number;
  discountPercent: number;
  reviewScoreDesc: string | null;
  steamUrl: string;
  capsuleImageUrl: string | null;
};

export type DealDetail = DealSummary;

export type ApiErrorResponse = {
  message: string;
  timestamp: string;
};
```

## Local Testing

- IntelliJ HTTP client: [http/deals-api.http](/Users/codegun/lootwise/http/deals-api.http:1)
- Postman collection: [postman/lootwise-api.postman_collection.json](/Users/codegun/lootwise/postman/lootwise-api.postman_collection.json:1)

## Frontend Prompt Example

```txt
Create a frontend page for the Lootwise deals API.

API:
- GET http://localhost:8080/api/v1/deals?limit=20
- GET http://localhost:8080/api/v1/deals/{appId}

Types:
type DealSummary = {
  appId: number;
  name: string;
  originalPrice: number | null;
  finalPrice: number;
  discountPercent: number;
  reviewScoreDesc: string | null;
  steamUrl: string;
  capsuleImageUrl: string | null;
};

type DealDetail = DealSummary;

Requirements:
- deals list page
- deal detail page
- loading/error/empty states
- fetch from real backend
- do not mock unless API is unavailable
```
