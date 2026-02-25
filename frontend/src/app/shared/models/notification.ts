export interface Notification {
  id: number;
  userId: number;
  type: string;
  sourceId: number;
  message: string;
  isRead: boolean;
  createdAt: string;
}
