/**
 * Reserved backend API layer.
 * All methods currently return mock data / in-memory updates.
 * Replace implementations with real HTTP calls when the campus API is ready.
 */

import {
  CURRENT_USER_ID,
  mockConversations,
  mockFavorites,
  mockMessages,
  mockOrders,
  mockProducts,
  mockUsers,
  studentRegistry,
} from '@/data/mock';
import { sanitizeOutbound } from '@/services/chatFilter';
import type {
  ChatMessage,
  ConditionGrade,
  Conversation,
  Order,
  OrderStatus,
  Product,
  ProductCategory,
  RecognitionResult,
  User,
} from '@/types';

const delay = (ms = 400) => new Promise((r) => setTimeout(r, ms));

let products = [...mockProducts];
let conversations = [...mockConversations];
let messages = [...mockMessages];
let orders = [...mockOrders];
let favorites = [...mockFavorites];
let users = [...mockUsers];

/** Relative dorm proximity: same building = 0, else hashed distance 1–5. */
export function dormDistance(a: string, b: string): number {
  if (a === b) return 0;
  let h = 0;
  const s = a + '|' + b;
  for (let i = 0; i < s.length; i++) h = (h * 31 + s.charCodeAt(i)) >>> 0;
  return 1 + (h % 5);
}

export const api = {
  // ——— Auth ———
  async verifyStudent(studentId: string, name: string): Promise<{ ok: true; profile: typeof studentRegistry[string] } | { ok: false; reason: string }> {
    await delay(600);
    const record = studentRegistry[studentId.trim()];
    if (!record) {
      return { ok: false, reason: 'Student ID not found in university registry.' };
    }
    if (record.name.toLowerCase() !== name.trim().toLowerCase()) {
      return { ok: false, reason: 'Name does not match student ID records.' };
    }
    return { ok: true, profile: record };
  },

  async login(studentId: string, name: string): Promise<User> {
    const result = await this.verifyStudent(studentId, name);
    if (!result.ok) throw new Error(result.reason);
    let user = users.find((u) => u.studentId === studentId);
    if (!user) {
      user = {
        id: `u_${studentId}`,
        studentId,
        name: result.profile.name,
        dormBuilding: result.profile.dormBuilding,
        avatar: `https://i.pravatar.cc/150?u=${studentId}`,
        creditScore: 90,
        verified: true,
      };
      users = [...users, user];
    }
    return user;
  },

  // ——— Products ———
  async getProducts(params?: {
    category?: ProductCategory | 'all';
    query?: string;
    dormBuilding?: string;
  }): Promise<Product[]> {
    await delay(300);
    let list = products.filter((p) => p.status !== 'sold');
    if (params?.category && params.category !== 'all') {
      list = list.filter((p) => p.category === params.category);
    }
    if (params?.query) {
      const q = params.query.toLowerCase();
      list = list.filter(
        (p) =>
          p.title.toLowerCase().includes(q) ||
          p.description.toLowerCase().includes(q),
      );
    }
    const dorm = params?.dormBuilding;
    if (dorm) {
      list = [...list].sort(
        (a, b) =>
          dormDistance(a.dormBuilding, dorm) - dormDistance(b.dormBuilding, dorm) ||
          new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime(),
      );
    } else {
      list = [...list].sort(
        (a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime(),
      );
    }
    return list;
  },

  async getNearbyProducts(dormBuilding: string, limit = 6): Promise<Product[]> {
    const all = await this.getProducts({ dormBuilding });
    return all.filter((p) => p.dormBuilding === dormBuilding).slice(0, limit);
  },

  async getProduct(id: string): Promise<Product | undefined> {
    await delay(200);
    return products.find((p) => p.id === id);
  },

  async createProduct(input: Omit<Product, 'id' | 'createdAt' | 'status' | 'favoriteCount' | 'viewCount'>): Promise<Product> {
    await delay(500);
    // POST /api/products
    const product: Product = {
      ...input,
      id: `p_${Date.now()}`,
      createdAt: new Date().toISOString(),
      status: 'available',
      favoriteCount: 0,
      viewCount: 0,
    };
    products = [product, ...products];
    return product;
  },

  async getUserProducts(userId: string): Promise<Product[]> {
    await delay(200);
    return products.filter((p) => p.sellerId === userId);
  },

  // ——— Users ———
  async getUser(id: string): Promise<User | undefined> {
    await delay(150);
    return users.find((u) => u.id === id);
  },

  async updateCredit(userId: string, delta: number): Promise<User> {
    await delay(200);
    // POST /api/users/:id/credit
    users = users.map((u) =>
      u.id === userId
        ? { ...u, creditScore: Math.min(100, Math.max(0, u.creditScore + delta)) }
        : u,
    );
    return users.find((u) => u.id === userId)!;
  },

  // ——— Favorites ———
  async getFavorites(userId: string = CURRENT_USER_ID): Promise<Product[]> {
    await delay(200);
    void userId;
    return products.filter((p) => favorites.includes(p.id));
  },

  async toggleFavorite(productId: string): Promise<boolean> {
    await delay(150);
    // POST /api/favorites/toggle
    const on = favorites.includes(productId);
    favorites = on
      ? favorites.filter((id) => id !== productId)
      : [...favorites, productId];
    products = products.map((p) =>
      p.id === productId
        ? { ...p, favoriteCount: p.favoriteCount + (on ? -1 : 1) }
        : p,
    );
    return !on;
  },

  isFavorite(productId: string): boolean {
    return favorites.includes(productId);
  },

  // ——— Messages ———
  async getConversations(): Promise<Conversation[]> {
    await delay(250);
    return [...conversations].sort(
      (a, b) => new Date(b.lastAt).getTime() - new Date(a.lastAt).getTime(),
    );
  },

  async getMessages(conversationId: string): Promise<ChatMessage[]> {
    await delay(200);
    return messages
      .filter((m) => m.conversationId === conversationId)
      .sort((a, b) => new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime());
  },

  async sendMessage(
    conversationId: string,
    senderId: string,
    text: string,
  ): Promise<ChatMessage> {
    await delay(200);
    // POST /api/messages
    const sanitized = sanitizeOutbound(text);
    const msg: ChatMessage = {
      id: `m_${Date.now()}`,
      conversationId,
      senderId,
      text: sanitized.text,
      createdAt: new Date().toISOString(),
      blocked: sanitized.blocked,
      originalText: sanitized.blocked ? text : undefined,
    };
    messages = [...messages, msg];
    conversations = conversations.map((c) =>
      c.id === conversationId
        ? { ...c, lastMessage: msg.text, lastAt: msg.createdAt }
        : c,
    );
    return msg;
  },

  async openOrCreateConversation(peerId: string, productId?: string): Promise<Conversation> {
    await delay(200);
    let conv = conversations.find(
      (c) => c.peerId === peerId && (!productId || c.productId === productId),
    );
    if (!conv) {
      conv = {
        id: `c_${Date.now()}`,
        peerId,
        productId,
        lastMessage: '',
        lastAt: new Date().toISOString(),
        unread: 0,
      };
      conversations = [conv, ...conversations];
    }
    return conv;
  },

  // ——— Orders ———
  async getOrders(userId: string = CURRENT_USER_ID): Promise<Order[]> {
    await delay(250);
    return orders
      .filter((o) => o.buyerId === userId || o.sellerId === userId)
      .sort((a, b) => new Date(b.updatedAt).getTime() - new Date(a.updatedAt).getTime());
  },

  async getOrder(id: string): Promise<Order | undefined> {
    await delay(150);
    return orders.find((o) => o.id === id);
  },

  async reserveProduct(productId: string, buyerId: string): Promise<Order> {
    await delay(400);
    // POST /api/orders/reserve
    const product = products.find((p) => p.id === productId);
    if (!product || product.status !== 'available') {
      throw new Error('Product is not available for reservation.');
    }
    products = products.map((p) =>
      p.id === productId ? { ...p, status: 'reserved' } : p,
    );
    const order: Order = {
      id: `o_${Date.now()}`,
      productId,
      buyerId,
      sellerId: product.sellerId,
      status: 'reserved',
      price: product.price,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    };
    orders = [order, ...orders];
    return order;
  },

  async updateOrderStatus(orderId: string, status: OrderStatus, meetupNote?: string): Promise<Order> {
    await delay(300);
    // PATCH /api/orders/:id
    orders = orders.map((o) =>
      o.id === orderId
        ? {
            ...o,
            status,
            meetupNote: meetupNote ?? o.meetupNote,
            updatedAt: new Date().toISOString(),
          }
        : o,
    );
    const order = orders.find((o) => o.id === orderId)!;
    if (status === 'completed') {
      products = products.map((p) =>
        p.id === order.productId ? { ...p, status: 'sold' } : p,
      );
    }
    if (status === 'cancelled') {
      products = products.map((p) =>
        p.id === order.productId ? { ...p, status: 'available' } : p,
      );
    }
    return order;
  },

  async rateOrder(
    orderId: string,
    fromUserId: string,
    rating: number,
    comment: string,
  ): Promise<Order> {
    await delay(350);
    // POST /api/orders/:id/rate
    const order = orders.find((o) => o.id === orderId);
    if (!order) throw new Error('Order not found');
    const isBuyer = fromUserId === order.buyerId;
    const targetId = isBuyer ? order.sellerId : order.buyerId;
    orders = orders.map((o) => {
      if (o.id !== orderId) return o;
      return {
        ...o,
        buyerRating: isBuyer ? rating : o.buyerRating,
        sellerRating: !isBuyer ? rating : o.sellerRating,
        buyerComment: isBuyer ? comment : o.buyerComment,
        sellerComment: !isBuyer ? comment : o.sellerComment,
        status: 'rated',
        updatedAt: new Date().toISOString(),
      };
    });
    const delta = rating >= 4 ? 2 : rating === 3 ? 0 : -3;
    await this.updateCredit(targetId, delta);
    return orders.find((o) => o.id === orderId)!;
  },

  // ——— Vision (reserved) ———
  async analyzeProductImage(_uri: string): Promise<RecognitionResult> {
    // POST /api/vision/analyze — implemented client-side in services/vision.ts for demo
    throw new Error('Use runSmartRecognition() from services/vision.ts');
  },
};

export type CreateProductInput = {
  title: string;
  description: string;
  price: number;
  category: ProductCategory;
  condition: ConditionGrade;
  images: string[];
  flaws: Product['flaws'];
  sellerId: string;
  dormBuilding: string;
};
