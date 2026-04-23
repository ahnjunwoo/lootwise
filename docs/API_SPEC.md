# Lootwise API Spec

프론트엔드 개발 전달용 문서입니다.

## Base URL

- Local: `http://localhost:8080`

## Endpoints

### 1. Get Top Deals

- Method: `GET`
- Path: `/api/v1/deals`

#### Query Parameters

- `keyword`: `string`
  - optional
  - game name search
- `minDiscountPercent`: `number`
  - optional
  - min: `1`
  - max: `100`
- `maxFinalPrice`: `number`
  - optional
  - min: `0`
- `minReviewScore`: `number`
  - optional
  - min: `0`
  - max: `100`
- `reviewScoreDesc`: `string`
  - optional
  - partial match, case-insensitive
- `sort`: `DealSort`
  - optional
  - default: `DISCOUNT_DESC`
  - values: `DISCOUNT_DESC`, `PRICE_ASC`, `REVIEW_COUNT_DESC`, `REVIEW_SCORE_DESC`, `LATEST_DESC`
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
    "reviewScoreDescKo": "매우 긍정적",
    "steamUrl": "https://store.steampowered.com/app/570",
    "capsuleImageUrl": "https://cdn.cloudflare.steamstatic.com/steam/apps/570/header.jpg"
  }
]
```

#### Filtered Request Example

```http
GET /api/v1/deals?keyword=dark&minDiscountPercent=50&maxFinalPrice=20000&minReviewScore=8&reviewScoreDesc=Positive&sort=PRICE_ASC&limit=20
```

#### Response Fields

- `appId`: `number`
- `name`: `string`
- `originalPrice`: `number | null`
- `finalPrice`: `number`
- `discountPercent`: `number`
- `reviewScoreDesc`: `string | null`
- `reviewScoreDescKo`: `string | null`
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
  "reviewScoreDescKo": "매우 긍정적",
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
  reviewScoreDescKo: string | null;
  steamUrl: string;
  capsuleImageUrl: string | null;
};

export type DealDetail = DealSummary;

export type ApiErrorResponse = {
  message: string;
  timestamp: string;
};

export type DealSort =
  | 'DISCOUNT_DESC'
  | 'PRICE_ASC'
  | 'REVIEW_COUNT_DESC'
  | 'REVIEW_SCORE_DESC'
  | 'LATEST_DESC';

export type DealSearchParams = {
  keyword?: string;
  minDiscountPercent?: number;
  maxFinalPrice?: number;
  minReviewScore?: number;
  reviewScoreDesc?: string;
  sort?: DealSort;
  limit?: number;
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
- GET http://localhost:8080/api/v1/deals?keyword=dark&minDiscountPercent=50&maxFinalPrice=20000&sort=PRICE_ASC&limit=20
- GET http://localhost:8080/api/v1/deals/{appId}

Types:
type DealSummary = {
  appId: number;
  name: string;
  originalPrice: number | null;
  finalPrice: number;
  discountPercent: number;
  reviewScoreDesc: string | null;
  reviewScoreDescKo: string | null;
  steamUrl: string;
  capsuleImageUrl: string | null;
};

type DealDetail = DealSummary;

type DealSort =
  | 'DISCOUNT_DESC'
  | 'PRICE_ASC'
  | 'REVIEW_COUNT_DESC'
  | 'REVIEW_SCORE_DESC'
  | 'LATEST_DESC';

type DealSearchParams = {
  keyword?: string;
  minDiscountPercent?: number;
  maxFinalPrice?: number;
  minReviewScore?: number;
  reviewScoreDesc?: string;
  sort?: DealSort;
  limit?: number;
};

Requirements:
- deals list page
- deal filters: keyword, minimum discount, maximum price, review score, review text, sort
- deal detail page
- loading/error/empty states
- fetch from real backend
- do not mock unless API is unavailable
```
