import { CharacterSummaryDTO, ChatSessionDTO } from "freechat-sdk";

export function openMessagesPane(): void {
  if (typeof window !== 'undefined') {
    document.body.style.overflow = 'hidden';
    document.documentElement.style.setProperty('--MessagesPane-slideIn', '1');
  }
}

export function closeMessagesPane(): void {
  if (typeof window !== 'undefined') {
    document.documentElement.style.removeProperty('--MessagesPane-slideIn');
    document.body.style.removeProperty('overflow');
  }
}

export function toggleMessagesPane(): void {
  if (typeof window !== 'undefined' && typeof document !== 'undefined') {
    const slideIn = window
      .getComputedStyle(document.documentElement)
      .getPropertyValue('--MessagesPane-slideIn');
    if (slideIn) {
      closeMessagesPane();
    } else {
      openMessagesPane();
    }
  }
}

export function getSenderStatus(session?: ChatSessionDTO): 'online' | 'invisible' | 'offline' {
  if (!session) {
    return 'offline';
  }
  
  switch(session.senderStatus) {
    case 'online': return 'online';
    case 'invisible': return 'invisible';
    default: return 'offline';
  }
}

export function getSenderStatusColor(status: 'online' | 'offline' | 'invisible'): 'success' | 'warning' | 'neutral' {
  switch(status) {
    case 'online': return 'success';
    case 'invisible': return 'warning';
    default: return 'neutral';
  }
}

export function getSenderName(sender?: CharacterSummaryDTO): string {
  return sender?.nickname ?? sender?.name ?? 'You';
}
