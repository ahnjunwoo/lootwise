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
