import React, { useEffect, useState, useRef } from 'react';
import { View, Text, TextInput, Button, FlatList, StyleSheet } from 'react-native';
import { WS_URL } from '../services/api';
import api from '../services/api';
import * as protobuf from 'protobufjs';
import { Buffer } from 'buffer';

// Protobuf setup
const protoRoot = protobuf.Root.fromJSON({
    nested: {
        ChatMessage: {
            fields: {
                senderId: { type: "int64", id: 1 },
                content: { type: "string", id: 2 },
                type: { type: "int32", id: 3 },
                timestamp: { type: "string", id: 4 }
            }
        }
    }
});
const ChatMessage = protoRoot.lookupType("ChatMessage");

const ChatScreen = ({ route }: any) => {
    const { user } = route.params;
    const [messages, setMessages] = useState<any[]>([]);
    const [inputText, setInputText] = useState('');
    const ws = useRef<WebSocket | null>(null);

    useEffect(() => {
        fetchHistory();

        // Connect WebSocket
        ws.current = new WebSocket(WS_URL);
        ws.current.binaryType = 'arraybuffer'; // Crucial for Protobuf

        ws.current.onopen = () => {
            console.log('Connected to Chat Server');
        };

        ws.current.onmessage = (e) => {
            try {
                const uint8Array = new Uint8Array(e.data);
                const decoded = ChatMessage.decode(uint8Array);
                const msg = ChatMessage.toObject(decoded, {
                    longs: String, // Treat int64 as string to avoid precision issues
                    enums: String,
                    bytes: String,
                });
                setMessages((prev) => [...prev, msg]);
            } catch (err) {
                console.error('Decode error', err);
            }
        };

        ws.current.onclose = () => {
            console.log('WebSocket Disconnected');
        };

        return () => {
            ws.current?.close();
        };
    }, []);

    const fetchHistory = async () => {
        try {
            const data = await api.get('/chat/history');
            setMessages(data);
        } catch (e) {
            console.error('Fetch history failed:', e);
        }
    };

    const sendMessage = () => {
        if (inputText.trim() && ws.current) {
            const payload = {
                senderId: user.id,
                content: inputText,
                type: 0,
                timestamp: new Date().toISOString()
            };

            try {
                const message = ChatMessage.create(payload);
                const buffer = ChatMessage.encode(message).finish();
                ws.current.send(buffer);
                setInputText('');
            } catch (e) {
                console.error('Send error', e);
            }
        }
    };

    return (
        <View style={styles.container}>
            <FlatList
                data={messages}
                keyExtractor={(item, index) => index.toString()}
                renderItem={({ item }) => (
                    <View style={[styles.msgContainer, item.senderId.toString() === user.id.toString() ? styles.me : styles.them]}>
                        <Text style={styles.sender}>{item.senderId.toString() === user.id.toString() ? 'Me' : `User ${item.senderId}`}</Text>
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
    sender: { fontSize: 12, color: '#666', marginBottom: 2 },
    msgText: { fontSize: 16 },
    inputContainer: { flexDirection: 'row', alignItems: 'center', marginTop: 10 },
    input: { flex: 1, borderWidth: 1, borderColor: '#ccc', borderRadius: 5, padding: 10, marginRight: 10 },
});

export default ChatScreen;
