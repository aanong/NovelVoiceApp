import React, { useState } from 'react';
import { View, TextInput, Button, StyleSheet, Alert, Text } from 'react-native';
import api from '../services/api';

const RegisterScreen = ({ navigation }: any) => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [nickname, setNickname] = useState('');

    const handleRegister = async () => {
        try {
            const res = await api.post('/auth/register', { username, password, nickname });
            if (res.data.code === 200) {
                Alert.alert('Success', 'Registered successfully');
                navigation.goBack();
            } else {
                Alert.alert('Error', res.data.msg);
            }
        } catch (error: any) {
            Alert.alert('Error', error.response?.data?.msg || 'Registration failed');
        }
    };

    return (
        <View style={styles.container}>
            <Text style={styles.title}>Register</Text>
            <TextInput style={styles.input} placeholder="Username" value={username} onChangeText={setUsername} />
            <TextInput style={styles.input} placeholder="Password" secureTextEntry value={password} onChangeText={setPassword} />
            <TextInput style={styles.input} placeholder="Nickname" value={nickname} onChangeText={setNickname} />
            <Button title="Submit" onPress={handleRegister} />
        </View>
    );
};

const styles = StyleSheet.create({
    container: { flex: 1, justifyContent: 'center', padding: 20 },
    title: { fontSize: 24, marginBottom: 20, textAlign: 'center' },
    input: { borderWidth: 1, borderColor: '#ccc', marginBottom: 15, padding: 10, borderRadius: 5 },
});

export default RegisterScreen;
