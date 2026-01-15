import React, { useEffect, useState, useRef, useCallback } from 'react';
import {
    View,
    Text,
    TextInput,
    TouchableOpacity,
    FlatList,
    StyleSheet,
    Alert,
    Modal,
    Image,
    Dimensions,
    KeyboardAvoidingView,
    Platform,
} from 'react-native';
import { WS_URL } from '../services/api';
import api from '../services/api';
import * as protobuf from 'protobufjs';
import { Message, MessageType, User, EMOJI_LIST } from '../types';

const { width: SCREEN_WIDTH, height: SCREEN_HEIGHT } = Dimensions.get('window');

// Protobuf 消息定义
const protoRoot = protobuf.Root.fromJSON({
    nested: {
        ChatMessage: {
            fields: {
                senderId: { type: 'int64', id: 1 },
                receiverId: { type: 'int64', id: 2 },
                content: { type: 'string', id: 3 },
                type: { type: 'int32', id: 4 },
                timestamp: { type: 'string', id: 5 },
                fileUrl: { type: 'string', id: 6 },
                fileName: { type: 'string', id: 7 },
                fileSize: { type: 'int64', id: 8 },
                senderNickname: { type: 'string', id: 9 },
                senderAvatar: { type: 'string', id: 10 },
            },
        },
    },
});
const ChatMessageProto = protoRoot.lookupType('ChatMessage');

interface Props {
    route: {
        params: {
            user: User;
            targetUser?: User; // 私聊目标用户
        };
    };
    navigation: any;
}

const ChatScreen: React.FC<Props> = ({ route, navigation }) => {
    const { user, targetUser } = route.params;
    const isPrivateChat = !!targetUser;
    
    // 消息相关状态
    const [messages, setMessages] = useState<Message[]>([]);
    const [inputText, setInputText] = useState('');
    
    // UI 状态
    const [showEmojiPanel, setShowEmojiPanel] = useState(false);
    const [showUserList, setShowUserList] = useState(false);
    const [userList, setUserList] = useState<User[]>([]);
    
    // WebSocket 引用
    const ws = useRef<WebSocket | null>(null);
    const flatListRef = useRef<FlatList>(null);

    useEffect(() => {
        // 设置导航标题
        navigation.setOptions({
            title: isPrivateChat ? `与 ${targetUser?.nickname || '用户'} 聊天` : '聊天室',
            headerRight: () => (
                <TouchableOpacity
                    style={styles.headerBtn}
                    onPress={() => fetchUserList()}
                >
                    <Text style={styles.headerBtnText}>👥 用户</Text>
                </TouchableOpacity>
            ),
        });

        fetchHistory();
        connectWebSocket();

        return () => {
            ws.current?.close();
        };
    }, [targetUser]);

    // 连接 WebSocket
    const connectWebSocket = () => {
        ws.current = new WebSocket(WS_URL);
        ws.current.binaryType = 'arraybuffer';

        ws.current.onopen = () => {
            console.log('WebSocket 已连接');
        };

        ws.current.onmessage = (e) => {
            try {
                const uint8Array = new Uint8Array(e.data);
                const decoded = ChatMessageProto.decode(uint8Array);
                const msg = ChatMessageProto.toObject(decoded, {
                    longs: String,
                    enums: String,
                    bytes: String,
                }) as Message;
                
                // 根据是否私聊过滤消息
                if (isPrivateChat) {
                    // 私聊模式：只接收与目标用户之间的消息
                    const senderId = Number(msg.senderId);
                    const receiverId = Number(msg.receiverId);
                    const userId = Number(user.id);
                    const targetId = Number(targetUser?.id);
                    
                    if ((senderId === userId && receiverId === targetId) ||
                        (senderId === targetId && receiverId === userId)) {
                        setMessages((prev) => [...prev, msg]);
                    }
                } else {
                    // 群聊模式：只接收群聊消息（receiverId 为 0 或 undefined）
                    if (!msg.receiverId || Number(msg.receiverId) === 0) {
                        setMessages((prev) => [...prev, msg]);
                    }
                }
                
                // 滚动到底部
                setTimeout(() => {
                    flatListRef.current?.scrollToEnd({ animated: true });
                }, 100);
            } catch (err) {
                console.error('消息解码失败:', err);
            }
        };

        ws.current.onclose = () => {
            console.log('WebSocket 已断开');
            // 尝试重连
            setTimeout(() => {
                if (ws.current?.readyState === WebSocket.CLOSED) {
                    connectWebSocket();
                }
            }, 3000);
        };

        ws.current.onerror = (error) => {
            console.error('WebSocket 错误:', error);
        };
    };

    // 获取历史消息
    const fetchHistory = async () => {
        try {
            let data;
            if (isPrivateChat && targetUser) {
                // 获取私聊历史
                data = await api.get(`/chat/private/${user.id}/${targetUser.id}?limit=100`);
            } else {
                // 获取群聊历史
                data = await api.get('/chat/history');
            }
            setMessages(data || []);
            
            // 滚动到底部
            setTimeout(() => {
                flatListRef.current?.scrollToEnd({ animated: false });
            }, 100);
        } catch (e) {
            console.error('获取历史消息失败:', e);
        }
    };

    // 获取用户列表
    const fetchUserList = async () => {
        try {
            const data = await api.get(`/chat/users/${user.id}`);
            setUserList(data || []);
            setShowUserList(true);
        } catch (e) {
            console.error('获取用户列表失败:', e);
            Alert.alert('错误', '获取用户列表失败');
        }
    };

    // 发送消息
    const sendMessage = (type: MessageType = MessageType.TEXT, content?: string) => {
        const messageContent = content || inputText.trim();
        if (!messageContent && type === MessageType.TEXT) return;
        if (!ws.current || ws.current.readyState !== WebSocket.OPEN) {
            Alert.alert('错误', '连接已断开，请稍后重试');
            return;
        }

        const payload = {
            senderId: user.id,
            receiverId: isPrivateChat ? targetUser?.id : 0,
            content: messageContent,
            type: type,
            timestamp: new Date().toISOString(),
            senderNickname: user.nickname || user.username,
            senderAvatar: user.avatar || '',
        };

        try {
            const message = ChatMessageProto.create(payload);
            const buffer = ChatMessageProto.encode(message).finish();
            ws.current.send(buffer);
            setInputText('');
            setShowEmojiPanel(false);
        } catch (e) {
            console.error('发送消息失败:', e);
            Alert.alert('错误', '发送失败');
        }
    };

    // 发送表情
    const sendEmoji = (emoji: string) => {
        sendMessage(MessageType.EMOJI, emoji);
    };

    // 选择用户进行私聊
    const startPrivateChat = (targetUser: User) => {
        setShowUserList(false);
        navigation.push('Chat', { user, targetUser });
    };

    // 渲染消息项
    const renderMessageItem = useCallback(({ item }: { item: Message }) => {
        const isMe = Number(item.senderId) === Number(user.id);
        const senderName = isMe ? '我' : (item.senderNickname || `用户${item.senderId}`);
        
        return (
            <View style={[styles.msgContainer, isMe ? styles.msgMe : styles.msgThem]}>
                {/* 头像和昵称 */}
                <View style={styles.msgHeader}>
                    <View style={[styles.avatar, isMe ? styles.avatarMe : styles.avatarThem]}>
                        <Text style={styles.avatarText}>
                            {senderName.charAt(0).toUpperCase()}
                        </Text>
                    </View>
                    <Text style={styles.senderName}>{senderName}</Text>
                </View>
                
                {/* 消息内容 */}
                <View style={[styles.msgBubble, isMe ? styles.bubbleMe : styles.bubbleThem]}>
                    {item.type === MessageType.IMAGE && item.fileUrl ? (
                        <Image
                            source={{ uri: item.fileUrl }}
                            style={styles.msgImage}
                            resizeMode="cover"
                        />
                    ) : item.type === MessageType.EMOJI ? (
                        <Text style={styles.msgEmoji}>{item.content}</Text>
                    ) : item.type === MessageType.FILE ? (
                        <View style={styles.fileContainer}>
                            <Text style={styles.fileIcon}>📎</Text>
                            <Text style={styles.fileName} numberOfLines={1}>
                                {item.fileName || '文件'}
                            </Text>
                        </View>
                    ) : (
                        <Text style={[styles.msgText, isMe ? styles.textMe : styles.textThem]}>
                            {item.content}
                        </Text>
                    )}
                </View>
                
                {/* 时间戳 */}
                <Text style={styles.msgTime}>
                    {formatTime(item.timestamp || item.createTime)}
                </Text>
            </View>
        );
    }, [user.id]);

    // 格式化时间
    const formatTime = (timeStr?: string) => {
        if (!timeStr) return '';
        const date = new Date(timeStr);
        return `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
    };

    // 渲染表情项
    const renderEmojiItem = ({ item }: { item: string }) => (
        <TouchableOpacity style={styles.emojiItem} onPress={() => sendEmoji(item)}>
            <Text style={styles.emojiText}>{item}</Text>
        </TouchableOpacity>
    );

    // 渲染用户列表项
    const renderUserItem = ({ item }: { item: User }) => (
        <TouchableOpacity style={styles.userItem} onPress={() => startPrivateChat(item)}>
            <View style={styles.userAvatar}>
                <Text style={styles.userAvatarText}>
                    {(item.nickname || item.username).charAt(0).toUpperCase()}
                </Text>
            </View>
            <View style={styles.userInfo}>
                <Text style={styles.userNickname}>{item.nickname || item.username}</Text>
                <Text style={styles.userStatus}>
                    {item.online ? '🟢 在线' : '⚪ 离线'}
                </Text>
            </View>
            <Text style={styles.chatBtn}>💬</Text>
        </TouchableOpacity>
    );

    return (
        <KeyboardAvoidingView
            style={styles.container}
            behavior={Platform.OS === 'ios' ? 'padding' : undefined}
            keyboardVerticalOffset={90}
        >
            {/* 私聊提示 */}
            {isPrivateChat && (
                <View style={styles.privateChatBanner}>
                    <Text style={styles.privateChatText}>
                        🔒 私密对话 - 与 {targetUser?.nickname || '用户'} 的聊天
                    </Text>
                </View>
            )}
            
            {/* 消息列表 */}
            <FlatList
                ref={flatListRef}
                data={messages}
                keyExtractor={(item, index) => `${item.id || index}-${item.timestamp}`}
                renderItem={renderMessageItem}
                contentContainerStyle={styles.messageList}
                onContentSizeChange={() => flatListRef.current?.scrollToEnd({ animated: false })}
            />
            
            {/* 表情面板 */}
            {showEmojiPanel && (
                <View style={styles.emojiPanel}>
                    <FlatList
                        data={EMOJI_LIST}
                        keyExtractor={(item) => item}
                        renderItem={renderEmojiItem}
                        numColumns={10}
                        contentContainerStyle={styles.emojiGrid}
                    />
                </View>
            )}
            
            {/* 输入区域 */}
            <View style={styles.inputContainer}>
                {/* 表情按钮 */}
                <TouchableOpacity
                    style={styles.iconBtn}
                    onPress={() => setShowEmojiPanel(!showEmojiPanel)}
                >
                    <Text style={styles.iconText}>😊</Text>
                </TouchableOpacity>
                
                {/* 输入框 */}
                <TextInput
                    style={styles.input}
                    value={inputText}
                    onChangeText={setInputText}
                    placeholder="输入消息..."
                    placeholderTextColor="#999"
                    multiline
                    maxLength={500}
                    onFocus={() => setShowEmojiPanel(false)}
                />
                
                {/* 发送按钮 */}
                <TouchableOpacity
                    style={[styles.sendBtn, !inputText.trim() && styles.sendBtnDisabled]}
                    onPress={() => sendMessage()}
                    disabled={!inputText.trim()}
                >
                    <Text style={styles.sendBtnText}>发送</Text>
                </TouchableOpacity>
            </View>
            
            {/* 用户列表弹窗 */}
            <Modal
                visible={showUserList}
                animationType="slide"
                transparent={true}
                onRequestClose={() => setShowUserList(false)}
            >
                <View style={styles.modalOverlay}>
                    <View style={styles.userListContainer}>
                        <View style={styles.userListHeader}>
                            <Text style={styles.userListTitle}>选择用户私聊</Text>
                            <TouchableOpacity onPress={() => setShowUserList(false)}>
                                <Text style={styles.closeBtn}>✕</Text>
                            </TouchableOpacity>
                        </View>
                        <FlatList
                            data={userList}
                            keyExtractor={(item) => item.id.toString()}
                            renderItem={renderUserItem}
                            ListEmptyComponent={
                                <View style={styles.emptyList}>
                                    <Text style={styles.emptyText}>暂无其他用户</Text>
                                </View>
                            }
                        />
                    </View>
                </View>
            </Modal>
        </KeyboardAvoidingView>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#f0f0f0',
    },
    headerBtn: {
        marginRight: 15,
        padding: 5,
    },
    headerBtnText: {
        fontSize: 16,
    },
    privateChatBanner: {
        backgroundColor: '#e6f7ff',
        padding: 10,
        alignItems: 'center',
        borderBottomWidth: 1,
        borderBottomColor: '#91d5ff',
    },
    privateChatText: {
        color: '#1890ff',
        fontSize: 14,
    },
    messageList: {
        padding: 10,
        paddingBottom: 20,
    },
    msgContainer: {
        marginVertical: 8,
        maxWidth: '80%',
    },
    msgMe: {
        alignSelf: 'flex-end',
        alignItems: 'flex-end',
    },
    msgThem: {
        alignSelf: 'flex-start',
        alignItems: 'flex-start',
    },
    msgHeader: {
        flexDirection: 'row',
        alignItems: 'center',
        marginBottom: 4,
    },
    avatar: {
        width: 28,
        height: 28,
        borderRadius: 14,
        justifyContent: 'center',
        alignItems: 'center',
        marginRight: 6,
    },
    avatarMe: {
        backgroundColor: '#52c41a',
    },
    avatarThem: {
        backgroundColor: '#1890ff',
    },
    avatarText: {
        color: '#fff',
        fontSize: 12,
        fontWeight: 'bold',
    },
    senderName: {
        fontSize: 12,
        color: '#666',
    },
    msgBubble: {
        padding: 12,
        borderRadius: 15,
        maxWidth: '100%',
    },
    bubbleMe: {
        backgroundColor: '#95ec69',
        borderBottomRightRadius: 4,
    },
    bubbleThem: {
        backgroundColor: '#fff',
        borderBottomLeftRadius: 4,
    },
    msgText: {
        fontSize: 16,
        lineHeight: 22,
    },
    textMe: {
        color: '#000',
    },
    textThem: {
        color: '#333',
    },
    msgEmoji: {
        fontSize: 36,
    },
    msgImage: {
        width: 200,
        height: 150,
        borderRadius: 10,
    },
    fileContainer: {
        flexDirection: 'row',
        alignItems: 'center',
    },
    fileIcon: {
        fontSize: 24,
        marginRight: 8,
    },
    fileName: {
        fontSize: 14,
        color: '#1890ff',
        maxWidth: 150,
    },
    msgTime: {
        fontSize: 10,
        color: '#999',
        marginTop: 4,
    },
    emojiPanel: {
        backgroundColor: '#fff',
        borderTopWidth: 1,
        borderTopColor: '#eee',
        maxHeight: 200,
    },
    emojiGrid: {
        padding: 10,
    },
    emojiItem: {
        width: (SCREEN_WIDTH - 20) / 10,
        height: 40,
        justifyContent: 'center',
        alignItems: 'center',
    },
    emojiText: {
        fontSize: 24,
    },
    inputContainer: {
        flexDirection: 'row',
        alignItems: 'flex-end',
        padding: 10,
        backgroundColor: '#fff',
        borderTopWidth: 1,
        borderTopColor: '#eee',
    },
    iconBtn: {
        padding: 8,
    },
    iconText: {
        fontSize: 24,
    },
    input: {
        flex: 1,
        backgroundColor: '#f5f5f5',
        borderRadius: 20,
        paddingHorizontal: 15,
        paddingVertical: 10,
        fontSize: 16,
        maxHeight: 100,
        marginHorizontal: 8,
    },
    sendBtn: {
        backgroundColor: '#52c41a',
        paddingHorizontal: 18,
        paddingVertical: 10,
        borderRadius: 20,
    },
    sendBtnDisabled: {
        backgroundColor: '#ccc',
    },
    sendBtnText: {
        color: '#fff',
        fontSize: 16,
        fontWeight: 'bold',
    },
    modalOverlay: {
        flex: 1,
        backgroundColor: 'rgba(0, 0, 0, 0.5)',
        justifyContent: 'flex-end',
    },
    userListContainer: {
        backgroundColor: '#fff',
        borderTopLeftRadius: 15,
        borderTopRightRadius: 15,
        maxHeight: SCREEN_HEIGHT * 0.7,
    },
    userListHeader: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
        padding: 15,
        borderBottomWidth: 1,
        borderBottomColor: '#eee',
    },
    userListTitle: {
        fontSize: 18,
        fontWeight: 'bold',
    },
    closeBtn: {
        fontSize: 20,
        color: '#666',
        padding: 5,
    },
    userItem: {
        flexDirection: 'row',
        alignItems: 'center',
        padding: 15,
        borderBottomWidth: 1,
        borderBottomColor: '#f0f0f0',
    },
    userAvatar: {
        width: 45,
        height: 45,
        borderRadius: 22.5,
        backgroundColor: '#1890ff',
        justifyContent: 'center',
        alignItems: 'center',
    },
    userAvatarText: {
        color: '#fff',
        fontSize: 18,
        fontWeight: 'bold',
    },
    userInfo: {
        flex: 1,
        marginLeft: 12,
    },
    userNickname: {
        fontSize: 16,
        fontWeight: '500',
        color: '#333',
    },
    userStatus: {
        fontSize: 12,
        color: '#999',
        marginTop: 4,
    },
    chatBtn: {
        fontSize: 24,
    },
    emptyList: {
        padding: 40,
        alignItems: 'center',
    },
    emptyText: {
        color: '#999',
        fontSize: 16,
    },
});

export default ChatScreen;
