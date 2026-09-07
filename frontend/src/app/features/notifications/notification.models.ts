export type NotificationType = 'BOOKING_CREATED' | 'BOOKING_UPDATED';

export interface AppNotification {
  id: number;
  userId: number;
  type: NotificationType;
  title: string;
  message: string;
  link: string | null;
  readAt: string | null;
  read: boolean;
  createdAt: string;
}
