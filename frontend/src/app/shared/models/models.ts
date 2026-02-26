// ─── User Models ───────────────────────────────────────────────────────

export type UserRole = 'PERSONAL' | 'CREATOR' | 'BUSINESS';
export type PrivacyType = 'PUBLIC' | 'PRIVATE';

export interface User {
  id: number;
  username: string;
  email: string;
  firstName?: string;
  lastName?: string;
  fullName?: string;
  bio?: string;
  profilePicture?: string;
  location?: string;
  website?: string;
  role: UserRole;
  privacy: PrivacyType;
  businessName?: string;
  category?: string;
  contactEmail?: string;
  contactPhone?: string;
  businessAddress?: string;
  businessHours?: string;
  followerCount: number;
  followingCount: number;
  connectionCount: number;
  postCount: number;
  isFollowing: boolean;
  isConnected: boolean;
  createdAt: string;
}

// ─── Auth Models ────────────────────────────────────────────────────────

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  firstName?: string;
  lastName?: string;
  role?: UserRole;
}

export interface LoginRequest {
  usernameOrEmail: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  tokenType: string;
  userId: number;
  username: string;
  email: string;
  role: string;
  profilePicture?: string;
}

// ─── Post Models ────────────────────────────────────────────────────────

export type PostType = 'TEXT' | 'IMAGE' | 'PROMOTIONAL' | 'REPOST' | 'ANNOUNCEMENT';

export interface Post {
  id: number;
  author: User;
  content: string;
  hashtags?: string;
  imageUrl?: string;
  type: PostType;
  callToActionLabel?: string;
  callToActionUrl?: string;
  pinned: boolean;
  viewCount: number;
  likeCount: number;
  commentCount: number;
  shareCount: number;
  likedByCurrentUser: boolean;
  originalPost?: Post;
  createdAt: string;
  updatedAt: string;
}

export interface CreatePostRequest {
  content: string;
  hashtags?: string;
  imageUrl?: string;
  type?: PostType;
  callToActionLabel?: string;
  callToActionUrl?: string;
  scheduledAt?: string;
  originalPostId?: number;
}

// ─── Comment Models ──────────────────────────────────────────────────────

export interface Comment {
  id: number;
  post: { id: number };
  user: User;
  content: string;
  createdAt: string;
}

// ─── Notification Models ─────────────────────────────────────────────────

export type NotificationType =
  | 'CONNECTION_REQUEST' | 'CONNECTION_ACCEPTED' | 'NEW_FOLLOWER'
  | 'POST_LIKED' | 'POST_COMMENTED' | 'POST_SHARED';

export interface Notification {
  id: number;
  sender: User;
  type: NotificationType;
  referenceId?: number;
  message: string;
  read: boolean;
  createdAt: string;
}

// ─── Connection Models ───────────────────────────────────────────────────

export type ConnectionStatus = 'PENDING' | 'ACCEPTED' | 'REJECTED';

export interface Connection {
  id: number;
  requester: User;
  addressee: User;
  status: ConnectionStatus;
  createdAt: string;
}

// ─── API Response Wrapper ────────────────────────────────────────────────

export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data: T;
  statusCode: number;
  timestamp: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

// ─── Analytics ───────────────────────────────────────────────────────────

export interface PostAnalytics {
  postId: number;
  viewCount: number;
  likeCount: number;
  commentCount: number;
  shareCount: number;
  engagementRate: number;
}


export interface Follow {
  id: number;
  followerId: number;
  followingId: number;
}
