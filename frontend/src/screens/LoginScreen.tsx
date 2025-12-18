import React, { useState } from 'react';
import { View, TextInput, Button, StyleSheet, Alert, Text } from 'react-native';
import api from '../services/api';

const LoginScreen = ({ navigation }: any) => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');

    const handleLogin = async () => {
        try {
            const res = await api.post('/auth/login', { username, password });
            if (res.data.code === 200) {
                // Save user info globally if needed (Context/Redux)
                const user = res.data.data;
                Alert.alert('Success', `Welcome ${user.nickname}`);
                navigation.replace('NovelList', { user });
            } else {
                Alert.alert('Error', res.data.msg);
            }
        } catch (error: any) {
            Alert.alert('Error', error.response?.data?.msg || 'Login failed');
        }
    };

    return (
        <View style={styles.container}>
            <Text style={styles.title}>Novel Voice Login</Text>
            <TextInput
                style={styles.input}
                placeholder="Username"
                value={username}
                onChangeText={setUsername}
            />
            <TextInput
                style={styles.input}
                placeholder="Password"
                secureTextEntry
                value={password}
                onChangeText={setPassword}
            />
            <Button title="Login" onPress={handleLogin} />
            <View style={{ marginTop: 20 }}>
                <Button title="Register" onPress={() => navigation.navigate('Register')} color="gray" />
            </View>
        </View>
    );
};

const styles = StyleSheet.create({
    container: { flex: 1, justifyContent: 'center', padding: 20 },
    title: { fontSize: 24, marginBottom: 20, textAlign: 'center' },
    input: { borderWidth: 1, borderColor: '#ccc', marginBottom: 15, padding: 10, borderRadius: 5 },
});

export default LoginScreen;
