import React, { useEffect, useState, useCallback } from 'react';
import {
    View,
    Text,
    FlatList,
    TouchableOpacity,
    StyleSheet,
    RefreshControl,
    Image,
    Alert,
} from 'react-native';
import api, { clearUserStorage } from '../services/api';
import { Novel, User, ReadingProgress } from '../types';

interface Props {
    navigation: any;
    route: {
        params: {
            user: User;
        };
    };
}

const NovelListScreen: React.FC<Props> = ({ navigation, route }) => {
    const [novels, setNovels] = useState<Novel[]>([]);
    const [readingHistory, setReadingHistory] = useState<{ [key: number]: ReadingProgress }>({});
    const [refreshing, setRefreshing] = useState(false);
    const user = route.params?.user;

    useEffect(() => {
        if (!user) {
            navigation.replace('Login');
            return;
        }
        
        // 设置导航栏
        navigation.setOptions({
            headerRight: () => (
                <View style={styles.headerRight}>
                    <TouchableOpacity
                        style={styles.headerBtn}
                        onPress={() => navigation.navigate('Chat', { user })}
                    >
                        <Text style={styles.headerBtnText}>💬</Text>
                    </TouchableOpacity>
                    <TouchableOpacity
                        style={styles.headerBtn}
                        onPress={handleLogout}
                    >
                        <Text style={styles.headerBtnText}>退出</Text>
                    </TouchableOpacity>
                </View>
            ),
            headerLeft: () => (
                <View style={styles.headerLeft}>
                    <View style={styles.userAvatar}>
                        <Text style={styles.userAvatarText}>
                            {(user.nickname || user.username).charAt(0).toUpperCase()}
                        </Text>
                    </View>
                    <Text style={styles.userName} numberOfLines={1}>
                        {user.nickname || user.username}
                    </Text>
                </View>
            ),
        });

        loadData();
    }, [user]);

    const loadData = async () => {
        await Promise.all([fetchNovels(), fetchReadingHistory()]);
    };

    const fetchNovels = async () => {
        try {
            const data = await api.get('/novels/list');
            setNovels(data || []);
        } catch (error) {
            console.error('获取小说列表失败:', error);
        }
    };

    const fetchReadingHistory = async () => {
        if (!user) return;
        try {
            const data = await api.get(`/reading-progress/history/${user.id}`);
            if (data && Array.isArray(data)) {
                const historyMap: { [key: number]: ReadingProgress } = {};
                data.forEach((item: ReadingProgress) => {
                    historyMap[item.novelId] = item;
                });
                setReadingHistory(historyMap);
            }
        } catch (error) {
            console.error('获取阅读历史失败:', error);
        }
    };

    const handleRefresh = useCallback(async () => {
        setRefreshing(true);
        await loadData();
        setRefreshing(false);
    }, [user]);

    const handleLogout = async () => {
        Alert.alert(
            '确认退出',
            '确定要退出登录吗？',
            [
                { text: '取消', style: 'cancel' },
                {
                    text: '退出',
                    style: 'destructive',
                    onPress: async () => {
                        try {
                            await api.post(`/auth/logout/${user.id}`);
                        } catch (e) {
                            // 忽略错误
                        }
                        await clearUserStorage();
                        navigation.replace('Login');
                    },
                },
            ]
        );
    };

    const openNovel = (novel: Novel) => {
        navigation.navigate('NovelReader', {
            novelId: novel.id,
            title: novel.title,
            userId: user.id,
        });
    };

    const renderNovelItem = ({ item }: { item: Novel }) => {
        const progress = readingHistory[item.id];
        
        return (
            <TouchableOpacity style={styles.novelItem} onPress={() => openNovel(item)}>
                {/* 封面图 */}
                <View style={styles.coverContainer}>
                    {item.coverUrl ? (
                        <Image source={{ uri: item.coverUrl }} style={styles.cover} />
                    ) : (
                        <View style={styles.coverPlaceholder}>
                            <Text style={styles.coverPlaceholderText}>📚</Text>
                        </View>
                    )}
                </View>
                
                {/* 小说信息 */}
                <View style={styles.novelInfo}>
                    <Text style={styles.novelTitle} numberOfLines={1}>{item.title}</Text>
                    <Text style={styles.novelAuthor} numberOfLines={1}>
                        {item.author || '未知作者'}
                    </Text>
                    <Text style={styles.novelDesc} numberOfLines={2}>
                        {item.description || '暂无简介'}
                    </Text>
                    
                    {/* 阅读进度 */}
                    {progress && (
                        <View style={styles.progressContainer}>
                            <Text style={styles.progressText}>
                                📖 上次读到：{progress.chapterTitle || `第${progress.chapterNo}章`}
                            </Text>
                        </View>
                    )}
                </View>
                
                {/* 箭头 */}
                <Text style={styles.arrow}>›</Text>
            </TouchableOpacity>
        );
    };

    const ListEmptyComponent = () => (
        <View style={styles.emptyContainer}>
            <Text style={styles.emptyIcon}>📚</Text>
            <Text style={styles.emptyText}>暂无小说</Text>
            <Text style={styles.emptySubText}>下拉刷新试试</Text>
        </View>
    );

    return (
        <View style={styles.container}>
            <FlatList
                data={novels}
                renderItem={renderNovelItem}
                keyExtractor={(item) => item.id.toString()}
                contentContainerStyle={styles.listContent}
                refreshControl={
                    <RefreshControl
                        refreshing={refreshing}
                        onRefresh={handleRefresh}
                        colors={['#8b4513']}
                        tintColor="#8b4513"
                    />
                }
                ListEmptyComponent={ListEmptyComponent}
            />
        </View>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#f5f5dc',
    },
    headerRight: {
        flexDirection: 'row',
        alignItems: 'center',
    },
    headerBtn: {
        marginRight: 15,
        padding: 5,
    },
    headerBtnText: {
        fontSize: 16,
        color: '#8b4513',
    },
    headerLeft: {
        flexDirection: 'row',
        alignItems: 'center',
        marginLeft: 15,
    },
    userAvatar: {
        width: 32,
        height: 32,
        borderRadius: 16,
        backgroundColor: '#8b4513',
        justifyContent: 'center',
        alignItems: 'center',
    },
    userAvatarText: {
        color: '#fff',
        fontSize: 14,
        fontWeight: 'bold',
    },
    userName: {
        marginLeft: 8,
        fontSize: 16,
        color: '#8b4513',
        fontWeight: '500',
        maxWidth: 100,
    },
    listContent: {
        padding: 15,
    },
    novelItem: {
        flexDirection: 'row',
        backgroundColor: '#fff',
        borderRadius: 12,
        padding: 12,
        marginBottom: 12,
        shadowColor: '#000',
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.1,
        shadowRadius: 4,
        elevation: 3,
    },
    coverContainer: {
        width: 80,
        height: 110,
        borderRadius: 8,
        overflow: 'hidden',
    },
    cover: {
        width: '100%',
        height: '100%',
    },
    coverPlaceholder: {
        width: '100%',
        height: '100%',
        backgroundColor: '#d2691e',
        justifyContent: 'center',
        alignItems: 'center',
    },
    coverPlaceholderText: {
        fontSize: 36,
    },
    novelInfo: {
        flex: 1,
        marginLeft: 12,
        justifyContent: 'space-between',
    },
    novelTitle: {
        fontSize: 18,
        fontWeight: 'bold',
        color: '#333',
    },
    novelAuthor: {
        fontSize: 14,
        color: '#8b4513',
        marginTop: 4,
    },
    novelDesc: {
        fontSize: 13,
        color: '#666',
        marginTop: 6,
        lineHeight: 18,
    },
    progressContainer: {
        marginTop: 8,
        paddingTop: 8,
        borderTopWidth: 1,
        borderTopColor: '#f0f0f0',
    },
    progressText: {
        fontSize: 12,
        color: '#52c41a',
    },
    arrow: {
        fontSize: 24,
        color: '#ccc',
        alignSelf: 'center',
    },
    emptyContainer: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        paddingTop: 100,
    },
    emptyIcon: {
        fontSize: 60,
        marginBottom: 20,
    },
    emptyText: {
        fontSize: 18,
        color: '#666',
        marginBottom: 8,
    },
    emptySubText: {
        fontSize: 14,
        color: '#999',
    },
});

export default NovelListScreen;
