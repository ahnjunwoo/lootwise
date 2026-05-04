# Lootwise API Spec

프론트엔드 개발 전달용 문서입니다.

## Base URL

- Local: `http://localhost:8080`

## Endpoints

### 1. Sign Up

- Method: `POST`
- Path: `/api/v1/auth/signup`
- Auth: not required

#### Request Body

```json
{
  "email": "test-user@example.com",
  "password": "Password123!",
  "nickname": "loot_user"
}
```

#### Validation

- `email`: required, email format, max length `255`
- `password`: required, min length `8`, max length `72`
- `nickname`: required, length `2` to `30`
- `nickname` allowed characters: English letters, numbers, Korean characters, underscore

#### Response `201 Created`

```json
{
  "userId": 1,
  "email": "test-user@example.com",
  "nickname": "loot_user"
}
```

#### Error `400 Bad Request`

```json
{
  "message": "Password must be between 8 and 72 characters",
  "timestamp": "2026-04-30T08:30:00Z"
}
```

#### Error `409 Conflict`

```json
{
  "message": "Email already exists: test-user@example.com",
  "timestamp": "2026-04-30T08:30:00Z"
}
```

#### FE Notes

- `password` and `passwordHash` are never returned.
- Backend normalizes email with trim and lowercase before saving.

### 2. Login

- Method: `POST`
- Path: `/api/v1/auth/login`
- Auth: not required

#### Request Body

```json
{
  "email": "test-user@example.com",
  "password": "Password123!"
}
```

#### Validation

- `email`: required, email format, max length `255`
- `password`: required, max length `72`

#### Response `200 OK`

```json
{
  "accessToken": "jwt-access-token",
  "tokenType": "Bearer",
  "expiresInSeconds": 900
}
```

#### Error `401 Unauthorized`

```json
{
  "message": "Invalid email or password",
  "timestamp": "2026-04-30T08:30:00Z"
}
```

#### FE Notes

- Email not found, password mismatch, and withdrawn user all return the same error.
- Send authenticated API requests with `Authorization: Bearer <accessToken>`.
- Refresh token is not implemented yet.

### 3. Get Me

- Method: `GET`
- Path: `/api/v1/users/me`
- Auth: required

#### Request Headers

```http
Authorization: Bearer <accessToken>
```

#### Response `200 OK`

```json
{
  "userId": 1,
  "email": "test-user@example.com",
  "nickname": "loot_user",
  "role": "USER",
  "status": "ACTIVE"
}
```

#### Response Fields

- `userId`: `number`
- `email`: `string`
- `nickname`: `string`
- `role`: `UserRole`
  - values: `USER`
- `status`: `UserStatus`
  - values: `ACTIVE`, `WITHDRAWN`

#### Error `401 Unauthorized`

Missing token:

```json
{
  "message": "Authentication is required",
  "timestamp": "2026-04-30T08:30:00Z"
}
```

Invalid or expired token:

```json
{
  "message": "Invalid or expired access token",
  "timestamp": "2026-04-30T08:30:00Z"
}
```

#### Error `403 Forbidden`

```json
{
  "message": "User is withdrawn: 1",
  "timestamp": "2026-04-30T08:30:00Z"
}
```

#### Error `404 Not Found`

```json
{
  "message": "User not found: 1",
  "timestamp": "2026-04-30T08:30:00Z"
}
```

#### FE Notes

- `password` and `passwordHash` are never returned.
- Store only `accessToken` on the frontend side. Do not infer current user fields from token payload; call this endpoint after login or page refresh.

### 4. Get Top Deals

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

### 5. Get Deal Detail

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

export type UserRole = 'USER';

export type UserStatus = 'ACTIVE' | 'WITHDRAWN';

export type SignUpRequest = {
  email: string;
  password: string;
  nickname: string;
};

export type SignUpResponse = {
  userId: number;
  email: string;
  nickname: string;
};

export type LoginRequest = {
  email: string;
  password: string;
};

export type LoginResponse = {
  accessToken: string;
  tokenType: 'Bearer';
  expiresInSeconds: number;
};

export type UserMeResponse = {
  userId: number;
  email: string;
  nickname: string;
  role: UserRole;
  status: UserStatus;
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
- POST http://localhost:8080/api/v1/auth/signup
- POST http://localhost:8080/api/v1/auth/login
- GET http://localhost:8080/api/v1/users/me
- GET http://localhost:8080/api/v1/deals?limit=20
- GET http://localhost:8080/api/v1/deals?keyword=dark&minDiscountPercent=50&maxFinalPrice=20000&sort=PRICE_ASC&limit=20
- GET http://localhost:8080/api/v1/deals/{appId}

Types:
type UserRole = 'USER';
type UserStatus = 'ACTIVE' | 'WITHDRAWN';

type LoginResponse = {
  accessToken: string;
  tokenType: 'Bearer';
  expiresInSeconds: number;
};

type UserMeResponse = {
  userId: number;
  email: string;
  nickname: string;
  role: UserRole;
  status: UserStatus;
};

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
- sign up page
- login page
- authenticated current user request using Authorization Bearer token
- deals list page
- deal filters: keyword, minimum discount, maximum price, review score, review text, sort
- deal detail page
- loading/error/empty states
- fetch from real backend
- do not mock unless API is unavailable
```
