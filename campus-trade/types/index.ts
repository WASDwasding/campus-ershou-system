export type ConditionGrade = 'like_new' | '90_new' | '80_new' | '70_new';

export type ProductCategory =
  | 'electronics'
  | 'books'
  | 'daily'
  | 'fashion'
  | 'sports'
  | 'beauty'
  | 'other';

export type OrderStatus =
  | 'pending_reserve'
  | 'reserved'
  | 'meetup_scheduled'
  | 'completed'
  | 'cancelled'
  | 'rated';

export interface FlawBox {
  id: string;
  x: number; // 0-1 relative
  y: number;
  width: number;
  height: number;
  label: string;
}

export interface User {
  id: string;
  studentId: string;
  name: string;
  dormBuilding: string;
  avatar: string;
  creditScore: number;
  bio?: string;
  verified: boolean;
}

export interface Product {
  id: string;
  title: string;
  description: string;
  price: number;
  category: ProductCategory;
  condition: ConditionGrade;
  images: string[];
  flaws: FlawBox[];
  sellerId: string;
  dormBuilding: string;
  createdAt: string;
  status: 'available' | 'reserved' | 'sold';
  favoriteCount: number;
  viewCount: number;
}

export interface Conversation {
  id: string;
  peerId: string;
  productId?: string;
  lastMessage: string;
  lastAt: string;
  unread: number;
}

export interface ChatMessage {
  id: string;
  conversationId: string;
  senderId: string;
  text: string;
  createdAt: string;
  blocked?: boolean;
  originalText?: string;
}

export interface Order {
  id: string;
  productId: string;
  buyerId: string;
  sellerId: string;
  status: OrderStatus;
  price: number;
  createdAt: string;
  updatedAt: string;
  meetupNote?: string;
  buyerRating?: number;
  sellerRating?: number;
  buyerComment?: string;
  sellerComment?: string;
}

export interface RecognitionResult {
  enhancedUri: string;
  originalUri: string;
  category: ProductCategory;
  categoryConfidence: number;
  condition: ConditionGrade;
  conditionConfidence: number;
  flaws: FlawBox[];
  suggestedTitle: string;
  suggestedDescription: string;
  processingSteps: RecognitionStep[];
}

export interface RecognitionStep {
  id: string;
  label: string;
  status: 'pending' | 'running' | 'done';
  detail?: string;
}

export interface PostDraft {
  title: string;
  description: string;
  price: string;
  category: ProductCategory;
  condition: ConditionGrade;
  images: string[];
  flaws: FlawBox[];
  fromSmart: boolean;
}
