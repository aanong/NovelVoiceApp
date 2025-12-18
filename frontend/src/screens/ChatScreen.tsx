import React, { useEffect, useState, useRef } from 'react';
import { View, Text, TextInput, Button, FlatList, StyleSheet } from 'react-native';
import { WS_URL } from '../services/api';
import api from '../services/api';

const ChatScreen = ({ route }: any) => {
    const { user } = route.params;
    const [messages, setMessages] = useState<any[]>([]);
    const [inputText, setInputText] = useState('');
    const ws = useRef<WebSocket | null>(null);

    useEffect(() => {
        fetchHistory();
        // Connect WebSocket
        ws.current = new WebSocket(WS_URL);
        ws.current.onopen = () => {
            console.log('Connected to Chat Server');
        };
        ws.current.onmessage = (e) => {
            try {
                const msg = JSON.parse(e.data);
                setMessages((prev) => [...prev, msg]);
            } catch (err) {
                console.error('Parse error', err);
            }
        };
        ws.current.onclose = () => {
            console.log('Valid WebSocket Disconnected');
        };

        return () => {
            ws.current?.close();
        };
    }, []);

    const fetchHistory = async () => {
        try {
            // Assume API endpoint exists for history if needed, but for now relying on WS or initial fetch
            const res = await api.get('/chat/history');
            if (res.data.code === 200) {
                setMessages(res.data.data);
            }
        } catch (e) {
            console.error(e);
        }
    };

    const sendMessage = () => {
        if (inputText.trim() && ws.current) {
            const payload = {
                senderId: user.id,
                content: inputText
            };
            ws.current.send(JSON.stringify(payload));
            setInputText('');
        }
    };

    return (
        <View style={styles.container}>
            <FlatList
                data={messages}
                keyExtractor={(item, index) => index.toString()}
                renderItem={({ item }) => (
                    <View style={[styles.msgContainer, item.senderId === user.id ? styles.me : styles.them]}>
                        <Text style={styles.msgText}>{item.content}</Text>
                    </View>
                )}
            />
            <View style={styles.inputContainer}>
                <TextInput
                    style={styles.input}
                    value={inputText}
                    onChangeText={setInputText}
                    placeholder="Type a message..."
                />
                <Button title="Send" onPress={sendMessage} />
            </View>
        </View>
    );
};

const styles = StyleSheet.create({
    container: { flex: 1, padding: 10, paddingBottom: 20 },
    msgContainer: { padding: 10, borderRadius: 5, marginVertical: 5, maxWidth: '80%' },
    me: { alignSelf: 'flex-end', backgroundColor: '#DCF8C6' },
    them: { alignSelf: 'flex-start', backgroundColor: '#fff', borderColor: '#eee', borderWidth: 1 },
    msgText: { fontSize: 16 },
    inputContainer: { flexDirection: 'row', alignItems: 'center', marginTop: 10 },
    input: { flex: 1, borderWidth: 1, borderColor: '#ccc', borderRadius: 5, padding: 10, marginRight: 10 },
});

export default ChatScreen;
