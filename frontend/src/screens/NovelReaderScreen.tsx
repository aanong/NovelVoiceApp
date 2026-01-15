import React, { useEffect, useState, useRef, useCallback } from 'react';
import {
    View,
    Text,
    ScrollView,
    TouchableOpacity,
    StyleSheet,
    Alert,
    Modal,
    FlatList,
    NativeSyntheticEvent,
    NativeScrollEvent,
    Dimensions,
} from 'react-native';
import Tts from 'react-native-tts';
import api, { saveReadingProgressLocal, getReadingProgressLocal } from '../services/api';
import { Chapter, ReadingProgress } from '../types';

const { height: SCREEN_HEIGHT } = Dimensions.get('window');

interface Props {
    route: {
        params: {
            novelId: number;
            title: string;
            userId: number;
        };
    };
    navigation: any;
}

const NovelReaderScreen: React.FC<Props> = ({ route, navigation }) => {
    const { novelId, title, userId } = route.params;
    
    // 章节相关状态
    const [chapters, setChapters] = useState<Chapter[]>([]);
    const [currentChapterIndex, setCurrentChapterIndex] = useState(0);
    const [content, setContent] = useState('');
    const [currentChapter, setCurrentChapter] = useState<Chapter | null>(null);
    
    // TTS 相关状态
    const [isPlaying, setIsPlaying] = useState(false);
    const [ttsPosition, setTtsPosition] = useState(0);
    
    // UI 状态
    const [showChapterList, setShowChapterList] = useState(false);
    const [isLoading, setIsLoading] = useState(true);
    
    // 滚动位置
    const scrollViewRef = useRef<ScrollView>(null);
    const [scrollPosition, setScrollPosition] = useState(0);
    
    // TTS 进度跟踪
    const ttsProgressRef = useRef(0);
    const contentRef = useRef('');

    useEffect(() => {
        initTts();
        loadChaptersAndProgress();
        
        return () => {
            Tts.stop();
            // 离开页面时保存进度
            saveProgress();
        };
    }, []);
    
    // 监听 TTS 事件
    useEffect(() => {
        const onTtsStart = Tts.addEventListener('tts-start', () => {
            setIsPlaying(true);
        });
        
        const onTtsFinish = Tts.addEventListener('tts-finish', () => {
            setIsPlaying(false);
            // TTS 朗读完成，检查是否需要切换下一章
            if (currentChapterIndex < chapters.length - 1) {
                Alert.alert(
                    '朗读完成',
                    '是否继续朗读下一章？',
                    [
                        { text: '取消', style: 'cancel' },
                        { text: '继续', onPress: () => handleNextChapter(true) },
                    ]
                );
            }
        });
        
        const onTtsProgress = Tts.addEventListener('tts-progress', (event: any) => {
            // 更新 TTS 进度
            if (event.location !== undefined) {
                ttsProgressRef.current = event.location;
                setTtsPosition(event.location);
            }
        });
        
        const onTtsCancel = Tts.addEventListener('tts-cancel', () => {
            setIsPlaying(false);
        });
        
        return () => {
            onTtsStart.remove();
            onTtsFinish.remove();
            onTtsProgress.remove();
            onTtsCancel.remove();
        };
    }, [currentChapterIndex, chapters]);

    // 初始化 TTS
    const initTts = async () => {
        try {
            await Tts.setDefaultLanguage('zh-CN');
            await Tts.setDefaultRate(0.5);
        } catch (e) {
            console.log('TTS 初始化失败，使用默认设置');
        }
    };

    // 加载章节列表和阅读进度
    const loadChaptersAndProgress = async () => {
        try {
            setIsLoading(true);
            
            // 获取章节列表
            const chaptersData = await api.get(`/novels/${novelId}/chapters`);
            setChapters(chaptersData);
            
            if (chaptersData.length === 0) {
                setIsLoading(false);
                return;
            }
            
            // 尝试获取阅读进度
            let progress: ReadingProgress | null = null;
            
            // 先尝试从服务器获取
            try {
                progress = await api.get(`/reading-progress/${userId}/${novelId}`);
            } catch (e) {
                // 服务器没有进度，尝试本地获取
                progress = await getReadingProgressLocal(userId, novelId);
            }
            
            if (progress && progress.chapterId) {
                // 找到对应的章节索引
                const chapterIndex = chaptersData.findIndex(
                    (c: Chapter) => c.id === progress!.chapterId
                );
                if (chapterIndex >= 0) {
                    setCurrentChapterIndex(chapterIndex);
                    setScrollPosition(progress.scrollPosition || 0);
                    setTtsPosition(progress.ttsPosition || 0);
                    await loadChapterContent(chaptersData[chapterIndex].id, progress.scrollPosition);
                } else {
                    await loadChapterContent(chaptersData[0].id);
                }
            } else {
                // 没有阅读进度，从第一章开始
                await loadChapterContent(chaptersData[0].id);
            }
        } catch (error) {
            console.error('加载章节失败:', error);
            Alert.alert('错误', '加载章节失败');
        } finally {
            setIsLoading(false);
        }
    };

    // 加载章节内容
    const loadChapterContent = async (chapterId: number, restoreScrollPosition?: number) => {
        try {
            const data = await api.get(`/novels/chapters/${chapterId}`);
            setContent(data.content || '');
            setCurrentChapter(data);
            contentRef.current = data.content || '';
            
            // 恢复滚动位置
            if (restoreScrollPosition && scrollViewRef.current) {
                setTimeout(() => {
                    scrollViewRef.current?.scrollTo({ y: restoreScrollPosition, animated: false });
                }, 100);
            }
            
            // 如果正在播放，继续播放新章节
            if (isPlaying) {
                Tts.stop();
                speak(data.content || '');
            }
        } catch (error) {
            console.error('加载章节内容失败:', error);
            Alert.alert('错误', '加载章节内容失败');
        }
    };

    // 保存阅读进度
    const saveProgress = async () => {
        if (!currentChapter || !userId) return;
        
        const progress: ReadingProgress = {
            userId,
            novelId,
            chapterId: currentChapter.id,
            chapterNo: currentChapterIndex + 1,
            scrollPosition: scrollPosition,
            ttsPosition: ttsProgressRef.current,
        };
        
        // 保存到本地
        await saveReadingProgressLocal(userId, novelId, progress);
        
        // 同步到服务器
        try {
            await api.post('/reading-progress/save', progress);
        } catch (e) {
            console.log('同步进度到服务器失败，已保存到本地');
        }
    };

    // 开始朗读
    const speak = (text: string, startPosition = 0) => {
        if (!text) return;
        
        // 从指定位置开始朗读
        const textToSpeak = startPosition > 0 ? text.substring(startPosition) : text;
        Tts.speak(textToSpeak);
        setIsPlaying(true);
    };

    // 停止朗读
    const stopTts = () => {
        Tts.stop();
        setIsPlaying(false);
    };

    // 从当前位置继续朗读
    const resumeTts = () => {
        if (content) {
            speak(content, ttsPosition);
        }
    };

    // 上一章
    const handlePrevChapter = () => {
        if (currentChapterIndex > 0) {
            const prevIndex = currentChapterIndex - 1;
            setCurrentChapterIndex(prevIndex);
            setTtsPosition(0);
            ttsProgressRef.current = 0;
            loadChapterContent(chapters[prevIndex].id);
        } else {
            Alert.alert('提示', '已经是第一章了');
        }
    };

    // 下一章
    const handleNextChapter = (autoPlay = false) => {
        if (currentChapterIndex < chapters.length - 1) {
            const nextIndex = currentChapterIndex + 1;
            setCurrentChapterIndex(nextIndex);
            setTtsPosition(0);
            ttsProgressRef.current = 0;
            loadChapterContent(chapters[nextIndex].id);
            
            if (autoPlay) {
                setTimeout(() => {
                    speak(contentRef.current);
                }, 500);
            }
        } else {
            Alert.alert('提示', '已经是最后一章了');
        }
    };

    // 选择章节
    const handleSelectChapter = (index: number) => {
        setCurrentChapterIndex(index);
        setTtsPosition(0);
        ttsProgressRef.current = 0;
        setShowChapterList(false);
        loadChapterContent(chapters[index].id);
    };

    // 滚动事件处理
    const handleScroll = useCallback((event: NativeSyntheticEvent<NativeScrollEvent>) => {
        const offsetY = event.nativeEvent.contentOffset.y;
        setScrollPosition(offsetY);
    }, []);

    // 渲染章节列表项
    const renderChapterItem = ({ item, index }: { item: Chapter; index: number }) => (
        <TouchableOpacity
            style={[
                styles.chapterItem,
                index === currentChapterIndex && styles.chapterItemActive,
            ]}
            onPress={() => handleSelectChapter(index)}
        >
            <Text
                style={[
                    styles.chapterItemText,
                    index === currentChapterIndex && styles.chapterItemTextActive,
                ]}
            >
                {item.title}
            </Text>
        </TouchableOpacity>
    );

    if (isLoading) {
        return (
            <View style={styles.loadingContainer}>
                <Text style={styles.loadingText}>加载中...</Text>
            </View>
        );
    }

    return (
        <View style={styles.container}>
            {/* 顶部信息栏 */}
            <View style={styles.header}>
                <Text style={styles.headerTitle} numberOfLines={1}>{title}</Text>
                <Text style={styles.chapterInfo}>
                    {currentChapter?.title || `第 ${currentChapterIndex + 1} 章`}
                </Text>
            </View>
            
            {/* 控制按钮栏 */}
            <View style={styles.controls}>
                <TouchableOpacity style={styles.controlBtn} onPress={handlePrevChapter}>
                    <Text style={styles.controlBtnText}>上一章</Text>
                </TouchableOpacity>
                
                <TouchableOpacity
                    style={[styles.controlBtn, styles.ttsBtn]}
                    onPress={isPlaying ? stopTts : resumeTts}
                >
                    <Text style={styles.controlBtnText}>
                        {isPlaying ? '⏹ 停止' : '▶ 朗读'}
                    </Text>
                </TouchableOpacity>
                
                <TouchableOpacity
                    style={styles.controlBtn}
                    onPress={() => setShowChapterList(true)}
                >
                    <Text style={styles.controlBtnText}>目录</Text>
                </TouchableOpacity>
                
                <TouchableOpacity style={styles.controlBtn} onPress={() => handleNextChapter()}>
                    <Text style={styles.controlBtnText}>下一章</Text>
                </TouchableOpacity>
            </View>
            
            {/* 内容区域 */}
            <ScrollView
                ref={scrollViewRef}
                style={styles.contentContainer}
                onScroll={handleScroll}
                scrollEventThrottle={100}
            >
                <Text style={styles.content}>{content}</Text>
                
                {/* 底部章节切换 */}
                <View style={styles.bottomNav}>
                    <TouchableOpacity
                        style={[styles.navBtn, currentChapterIndex === 0 && styles.navBtnDisabled]}
                        onPress={handlePrevChapter}
                        disabled={currentChapterIndex === 0}
                    >
                        <Text style={styles.navBtnText}>← 上一章</Text>
                    </TouchableOpacity>
                    
                    <TouchableOpacity
                        style={[styles.navBtn, currentChapterIndex === chapters.length - 1 && styles.navBtnDisabled]}
                        onPress={() => handleNextChapter()}
                        disabled={currentChapterIndex === chapters.length - 1}
                    >
                        <Text style={styles.navBtnText}>下一章 →</Text>
                    </TouchableOpacity>
                </View>
            </ScrollView>
            
            {/* TTS 进度提示 */}
            {isPlaying && (
                <View style={styles.ttsIndicator}>
                    <Text style={styles.ttsIndicatorText}>🔊 正在朗读中...</Text>
                </View>
            )}
            
            {/* 章节列表弹窗 */}
            <Modal
                visible={showChapterList}
                animationType="slide"
                transparent={true}
                onRequestClose={() => setShowChapterList(false)}
            >
                <View style={styles.modalOverlay}>
                    <View style={styles.chapterListContainer}>
                        <View style={styles.chapterListHeader}>
                            <Text style={styles.chapterListTitle}>章节目录</Text>
                            <TouchableOpacity onPress={() => setShowChapterList(false)}>
                                <Text style={styles.closeBtn}>✕</Text>
                            </TouchableOpacity>
                        </View>
                        <FlatList
                            data={chapters}
                            keyExtractor={(item) => item.id.toString()}
                            renderItem={renderChapterItem}
                            initialScrollIndex={currentChapterIndex > 0 ? currentChapterIndex : undefined}
                            getItemLayout={(data, index) => ({
                                length: 50,
                                offset: 50 * index,
                                index,
                            })}
                        />
                    </View>
                </View>
            </Modal>
        </View>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#f5f5dc', // 护眼色
    },
    loadingContainer: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
    },
    loadingText: {
        fontSize: 16,
        color: '#666',
    },
    header: {
        padding: 15,
        backgroundColor: '#8b4513',
    },
    headerTitle: {
        fontSize: 18,
        fontWeight: 'bold',
        color: '#fff',
        textAlign: 'center',
    },
    chapterInfo: {
        fontSize: 14,
        color: '#ddd',
        textAlign: 'center',
        marginTop: 5,
    },
    controls: {
        flexDirection: 'row',
        justifyContent: 'space-around',
        padding: 10,
        backgroundColor: '#d2691e',
    },
    controlBtn: {
        paddingVertical: 8,
        paddingHorizontal: 15,
        backgroundColor: '#8b4513',
        borderRadius: 5,
    },
    ttsBtn: {
        backgroundColor: '#228b22',
    },
    controlBtnText: {
        color: '#fff',
        fontSize: 14,
    },
    contentContainer: {
        flex: 1,
        padding: 15,
    },
    content: {
        fontSize: 18,
        lineHeight: 30,
        color: '#333',
        textAlign: 'justify',
    },
    bottomNav: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        marginTop: 30,
        marginBottom: 50,
    },
    navBtn: {
        paddingVertical: 12,
        paddingHorizontal: 25,
        backgroundColor: '#8b4513',
        borderRadius: 5,
    },
    navBtnDisabled: {
        backgroundColor: '#ccc',
    },
    navBtnText: {
        color: '#fff',
        fontSize: 16,
    },
    ttsIndicator: {
        position: 'absolute',
        bottom: 20,
        left: 20,
        right: 20,
        backgroundColor: 'rgba(0, 128, 0, 0.9)',
        padding: 10,
        borderRadius: 5,
        alignItems: 'center',
    },
    ttsIndicatorText: {
        color: '#fff',
        fontSize: 14,
    },
    modalOverlay: {
        flex: 1,
        backgroundColor: 'rgba(0, 0, 0, 0.5)',
        justifyContent: 'flex-end',
    },
    chapterListContainer: {
        backgroundColor: '#fff',
        borderTopLeftRadius: 15,
        borderTopRightRadius: 15,
        maxHeight: SCREEN_HEIGHT * 0.7,
    },
    chapterListHeader: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
        padding: 15,
        borderBottomWidth: 1,
        borderBottomColor: '#eee',
    },
    chapterListTitle: {
        fontSize: 18,
        fontWeight: 'bold',
    },
    closeBtn: {
        fontSize: 20,
        color: '#666',
        padding: 5,
    },
    chapterItem: {
        padding: 15,
        borderBottomWidth: 1,
        borderBottomColor: '#f0f0f0',
    },
    chapterItemActive: {
        backgroundColor: '#e6f7ff',
    },
    chapterItemText: {
        fontSize: 16,
        color: '#333',
    },
    chapterItemTextActive: {
        color: '#1890ff',
        fontWeight: 'bold',
    },
});

export default NovelReaderScreen;
