import React, { useEffect, useState } from 'react';
import { View, Text, ScrollView, Button, StyleSheet, Alert } from 'react-native';
import Tts from 'react-native-tts';
import api from '../services/api';

const NovelReaderScreen = ({ route }: any) => {
    const { novelId, title } = route.params;
    const [chapters, setChapters] = useState([]);
    const [currentChapterIndex, setCurrentChapterIndex] = useState(0);
    const [content, setContent] = useState('');
    const [isPlaying, setIsPlaying] = useState(false);

    useEffect(() => {
        fetchChapters();
        initTts();
        return () => {
            Tts.stop();
        };
    }, []);

    const initTts = () => {
        // Tts.setDefaultLanguage('zh-CN'); // Uncomment if Chinese voice needed and supported
    };

    const fetchChapters = async () => {
        try {
            const res = await api.get(`/novels/${novelId}/chapters`);
            if (res.data.code === 200) {
                setChapters(res.data.data);
                if (res.data.data.length > 0) {
                    loadChapterContent(res.data.data[0].id);
                }
            }
        } catch (error) {
            console.error(error);
        }
    };

    const loadChapterContent = async (chapterId: number) => {
        try {
            const res = await api.get(`/novels/chapters/${chapterId}`);
            if (res.data.code === 200) {
                setContent(res.data.data.content);
                if (isPlaying) {
                    Tts.stop();
                    speak(res.data.data.content);
                }
            }
        } catch (error) {
            console.error(error);
        }
    };

    const speak = (text: string) => {
        Tts.speak(text);
        setIsPlaying(true);
    };

    const stop = () => {
        Tts.stop();
        setIsPlaying(false);
    };

    const handleNext = () => {
        if (currentChapterIndex < chapters.length - 1) {
            const nextIndex = currentChapterIndex + 1;
            setCurrentChapterIndex(nextIndex);
            loadChapterContent(chapters[nextIndex].id);
        } else {
            Alert.alert('Info', 'Last chapter');
        }
    };

    return (
        <View style={styles.container}>
            <Text style={styles.header}>{title}</Text>
            <View style={styles.controls}>
                <Button title={isPlaying ? "Stop Voice" : "Play Voice"} onPress={isPlaying ? stop : () => speak(content)} />
                <Button title="Next Chapter" onPress={handleNext} />
            </View>
            <ScrollView style={styles.contentContainer}>
                <Text style={styles.content}>{content}</Text>
            </ScrollView>
        </View>
    );
};

const styles = StyleSheet.create({
    container: { flex: 1, padding: 10 },
    header: { fontSize: 20, fontWeight: 'bold', marginBottom: 10, textAlign: 'center' },
    controls: { flexDirection: 'row', justifyContent: 'space-around', marginBottom: 10 },
    contentContainer: { flex: 1 },
    content: { fontSize: 16, lineHeight: 24 },
});

export default NovelReaderScreen;
