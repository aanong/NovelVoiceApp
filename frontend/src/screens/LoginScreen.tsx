import React, { useState, useEffect } from 'react';
import {
    View,
    TextInput,
    TouchableOpacity,
    StyleSheet,
    Alert,
    Text,
    ActivityIndicator,
    KeyboardAvoidingView,
    Platform,
} from 'react-native';
import api, { saveUserToStorage, getUserFromStorage } from '../services/api';
import { User } from '../types';

interface Props {
    navigation: any;
}

const LoginScreen: React.FC<Props> = ({ navigation }) => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [loading, setLoading] = useState(false);
    const [checkingLogin, setCheckingLogin] = useState(true);
    
    // 表单验证状态
    const [usernameError, setUsernameError] = useState('');
    const [passwordError, setPasswordError] = useState('');

    // 检查是否已登录
    useEffect(() => {
        checkExistingLogin();
    }, []);

    const checkExistingLogin = async () => {
        try {
            const user = await getUserFromStorage();
            if (user && user.token) {
                // 验证 Token 是否有效
                try {
                    const validUser = await api.get('/auth/user');
                    if (validUser) {
                        navigation.replace('NovelList', { user: validUser });
                        return;
                    }
                } catch (e) {
                    // Token 无效，继续显示登录页面
                }
            }
        } catch (e) {
            console.error('检查登录状态失败:', e);
        } finally {
            setCheckingLogin(false);
        }
    };

    // 验证用户名
    const validateUsername = (value: string): boolean => {
        if (!value.trim()) {
            setUsernameError('用户名不能为空');
            return false;
        }
        if (value.length < 3) {
            setUsernameError('用户名至少3个字符');
            return false;
        }
        if (value.length > 20) {
            setUsernameError('用户名不能超过20个字符');
            return false;
        }
        setUsernameError('');
        return true;
    };

    // 验证密码
    const validatePassword = (value: string): boolean => {
        if (!value) {
            setPasswordError('密码不能为空');
            return false;
        }
        if (value.length < 6) {
            setPasswordError('密码至少6位');
            return false;
        }
        setPasswordError('');
        return true;
    };

    const handleLogin = async () => {
        // 表单验证
        const isUsernameValid = validateUsername(username);
        const isPasswordValid = validatePassword(password);
        
        if (!isUsernameValid || !isPasswordValid) {
            return;
        }

        setLoading(true);
        try {
            const user: User = await api.post('/auth/login', { username, password });
            
            // 保存用户信息到本地存储
            await saveUserToStorage(user);
            
            Alert.alert('登录成功', `欢迎回来，${user.nickname || user.username}`);
            navigation.replace('NovelList', { user });
        } catch (error: any) {
            Alert.alert('登录失败', error.message || '用户名或密码错误');
        } finally {
            setLoading(false);
        }
    };

    if (checkingLogin) {
        return (
            <View style={styles.loadingContainer}>
                <ActivityIndicator size="large" color="#8b4513" />
                <Text style={styles.loadingText}>正在检查登录状态...</Text>
            </View>
        );
    }

    return (
        <KeyboardAvoidingView
            style={styles.container}
            behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
        >
            <View style={styles.formContainer}>
                {/* 标题 */}
                <Text style={styles.title}>📚 Novel Voice</Text>
                <Text style={styles.subtitle}>小说朗读 · 畅享阅读</Text>
                
                {/* 用户名输入框 */}
                <View style={styles.inputWrapper}>
                    <TextInput
                        style={[styles.input, usernameError ? styles.inputError : null]}
                        placeholder="请输入用户名"
                        placeholderTextColor="#999"
                        value={username}
                        onChangeText={(text) => {
                            setUsername(text);
                            if (usernameError) validateUsername(text);
                        }}
                        onBlur={() => validateUsername(username)}
                        autoCapitalize="none"
                        autoCorrect={false}
                    />
                    {usernameError ? (
                        <Text style={styles.errorText}>{usernameError}</Text>
                    ) : null}
                </View>
                
                {/* 密码输入框 */}
                <View style={styles.inputWrapper}>
                    <TextInput
                        style={[styles.input, passwordError ? styles.inputError : null]}
                        placeholder="请输入密码"
                        placeholderTextColor="#999"
                        secureTextEntry
                        value={password}
                        onChangeText={(text) => {
                            setPassword(text);
                            if (passwordError) validatePassword(text);
                        }}
                        onBlur={() => validatePassword(password)}
                    />
                    {passwordError ? (
                        <Text style={styles.errorText}>{passwordError}</Text>
                    ) : null}
                </View>
                
                {/* 登录按钮 */}
                <TouchableOpacity
                    style={[styles.loginBtn, loading && styles.loginBtnDisabled]}
                    onPress={handleLogin}
                    disabled={loading}
                >
                    {loading ? (
                        <ActivityIndicator color="#fff" />
                    ) : (
                        <Text style={styles.loginBtnText}>登 录</Text>
                    )}
                </TouchableOpacity>
                
                {/* 注册链接 */}
                <View style={styles.registerContainer}>
                    <Text style={styles.registerText}>还没有账号？</Text>
                    <TouchableOpacity onPress={() => navigation.navigate('Register')}>
                        <Text style={styles.registerLink}>立即注册</Text>
                    </TouchableOpacity>
                </View>
            </View>
        </KeyboardAvoidingView>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#f5f5dc',
    },
    loadingContainer: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: '#f5f5dc',
    },
    loadingText: {
        marginTop: 10,
        color: '#8b4513',
        fontSize: 16,
    },
    formContainer: {
        flex: 1,
        justifyContent: 'center',
        paddingHorizontal: 30,
    },
    title: {
        fontSize: 32,
        fontWeight: 'bold',
        color: '#8b4513',
        textAlign: 'center',
        marginBottom: 10,
    },
    subtitle: {
        fontSize: 16,
        color: '#a0522d',
        textAlign: 'center',
        marginBottom: 40,
    },
    inputWrapper: {
        marginBottom: 20,
    },
    input: {
        backgroundColor: '#fff',
        borderWidth: 1,
        borderColor: '#ddd',
        borderRadius: 10,
        padding: 15,
        fontSize: 16,
        color: '#333',
    },
    inputError: {
        borderColor: '#ff4d4f',
    },
    errorText: {
        color: '#ff4d4f',
        fontSize: 12,
        marginTop: 5,
        marginLeft: 5,
    },
    loginBtn: {
        backgroundColor: '#8b4513',
        borderRadius: 10,
        padding: 15,
        alignItems: 'center',
        marginTop: 10,
    },
    loginBtnDisabled: {
        backgroundColor: '#ccc',
    },
    loginBtnText: {
        color: '#fff',
        fontSize: 18,
        fontWeight: 'bold',
    },
    registerContainer: {
        flexDirection: 'row',
        justifyContent: 'center',
        marginTop: 25,
    },
    registerText: {
        color: '#666',
        fontSize: 14,
    },
    registerLink: {
        color: '#8b4513',
        fontSize: 14,
        fontWeight: 'bold',
        marginLeft: 5,
    },
});

export default LoginScreen;
