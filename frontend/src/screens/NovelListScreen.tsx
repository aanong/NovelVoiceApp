import React, { useEffect, useState } from 'react';
import { View, Text, FlatList, TouchableOpacity, StyleSheet, Button } from 'react-native';
import api from '../services/api';

const NovelListScreen = ({ navigation, route }: any) => {
    const [novels, setNovels] = useState([]);
    const user = route.params?.user;

    useEffect(() => {
        fetchNovels();
        navigation.setOptions({
            headerRight: () => (
                <Button title="Chat" onPress={() => navigation.navigate('Chat', { user })} />
            ),
        });
    }, []);

    const fetchNovels = async () => {
        try {
            const data = await api.get('/novels/list');
            setNovels(data);
        } catch (error) {
            console.error('Fetch novels failed:', error);
        }
    };

    const renderItem = ({ item }: any) => (
        <TouchableOpacity
            style={styles.item}
            onPress={() => navigation.navigate('NovelReader', { novelId: item.id, title: item.title })}
        >
            <Text style={styles.title}>{item.title}</Text>
            <Text style={styles.author}>{item.author}</Text>
            <Text numberOfLines={2} style={styles.desc}>{item.description}</Text>
        </TouchableOpacity>
    );

    return (
        <View style={styles.container}>
            <FlatList
                data={novels}
                renderItem={renderItem}
                keyExtractor={(item: any) => item.id.toString()}
            />
        </View>
    );
};

const styles = StyleSheet.create({
    container: { flex: 1, padding: 10 },
    item: { padding: 15, borderBottomWidth: 1, borderBottomColor: '#eee', backgroundColor: '#fff', marginBottom: 5 },
    title: { fontSize: 18, fontWeight: 'bold' },
    author: { fontSize: 14, color: '#666', marginTop: 5 },
    desc: { fontSize: 14, color: '#999', marginTop: 5 },
});

export default NovelListScreen;
