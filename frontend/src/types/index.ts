/**
 * 用户信息类型
 */
export interface User {
    id: number;
    username: string;
    nickname: string;
    avatar?: string;
    token?: string;
    online?: boolean;
    lastLoginTime?: string;
    createTime?: string;
}

/**
 * 小说信息类型
 */
export interface Novel {
    id: number;
    title: string;
    author?: string;
    description?: string;
    coverUrl?: string;
}

/**
 * 章节信息类型
 */
export interface Chapter {
    id: number;
    novelId: number;
    title: string;
    content?: string;
    chapterNo: number;
    wordCount?: number;
}

/**
 * 阅读进度类型
 */
export interface ReadingProgress {
    id?: number;
    userId: number;
    novelId: number;
    novelTitle?: string;
    chapterId: number;
    chapterTitle?: string;
    chapterNo: number;
    scrollPosition: number;
    ttsPosition: number;
    lastReadTime?: string;
}

/**
 * 消息类型
 */
export interface Message {
    id?: number;
    senderId: number;
    senderNickname?: string;
    senderAvatar?: string;
    receiverId?: number;
    conversationId?: number;
    content: string;
    type: MessageType;
    fileUrl?: string;
    fileName?: string;
    fileSize?: number;
    isRead?: boolean;
    createTime?: string;
    timestamp?: string;
}

/**
 * 消息类型枚举
 */
export enum MessageType {
    TEXT = 0,      // 文本消息
    IMAGE = 1,     // 图片消息
    EMOJI = 2,     // 表情消息
    FILE = 3,      // 文件消息
}

/**
 * 会话类型
 */
export interface Conversation {
    id: number;
    targetUserId: number;
    targetNickname?: string;
    targetAvatar?: string;
    targetOnline?: boolean;
    lastMessageContent?: string;
    lastMessageType?: number;
    lastMessageTime?: string;
    unreadCount?: number;
}

/**
 * 表情数据
 */
export const EMOJI_LIST = [
    '😀', '😁', '😂', '🤣', '😃', '😄', '😅', '😆', '😉', '😊',
    '😋', '😎', '😍', '😘', '🥰', '😗', '😙', '😚', '🙂', '🤗',
    '🤩', '🤔', '🤨', '😐', '😑', '😶', '🙄', '😏', '😣', '😥',
    '😮', '🤐', '😯', '😪', '😫', '🥱', '😴', '😌', '😛', '😜',
    '😝', '🤤', '😒', '😓', '😔', '😕', '🙃', '🤑', '😲', '☹️',
    '🙁', '😖', '😞', '😟', '😤', '😢', '😭', '😦', '😧', '😨',
    '😩', '🤯', '😬', '😰', '😱', '🥵', '🥶', '😳', '🤪', '😵',
    '🥴', '😠', '😡', '🤬', '😷', '🤒', '🤕', '🤢', '🤮', '🤧',
    '👍', '👎', '👏', '🙌', '👐', '🤲', '🤝', '🙏', '✌️', '🤞',
    '❤️', '🧡', '💛', '💚', '💙', '💜', '🖤', '💔', '💕', '💖',
];
