import React, { useState } from 'react';
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
    ScrollView,
} from 'react-native';
import api from '../services/api';

interface Props {
    navigation: any;
}

const RegisterScreen: React.FC<Props> = ({ navigation }) => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [nickname, setNickname] = useState('');
    const [loading, setLoading] = useState(false);
    
    // 表单验证状态
    const [errors, setErrors] = useState<{ [key: string]: string }>({});

    // 验证表单
    const validateForm = (): boolean => {
        const newErrors: { [key: string]: string } = {};
        
        // 验证用户名
        if (!username.trim()) {
            newErrors.username = '用户名不能为空';
        } else if (username.length < 3) {
            newErrors.username = '用户名至少3个字符';
        } else if (username.length > 20) {
            newErrors.username = '用户名不能超过20个字符';
        } else if (!/^[a-zA-Z0-9_]+$/.test(username)) {
            newErrors.username = '用户名只能包含字母、数字和下划线';
        }
        
        // 验证密码
        if (!password) {
            newErrors.password = '密码不能为空';
        } else if (password.length < 6) {
            newErrors.password = '密码至少6位';
        } else if (password.length > 30) {
            newErrors.password = '密码不能超过30位';
        }
        
        // 验证确认密码
        if (!confirmPassword) {
            newErrors.confirmPassword = '请确认密码';
        } else if (password !== confirmPassword) {
            newErrors.confirmPassword = '两次输入的密码不一致';
        }
        
        // 验证昵称（可选）
        if (nickname && nickname.length > 20) {
            newErrors.nickname = '昵称不能超过20个字符';
        }
        
        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleRegister = async () => {
        if (!validateForm()) {
            return;
        }

        setLoading(true);
        try {
            await api.post('/auth/register', {
                username: username.trim(),
                password,
                nickname: nickname.trim() || username.trim(),
            });
            
            Alert.alert(
                '注册成功',
                '账号创建成功，请登录',
                [
                    {
                        text: '去登录',
                        onPress: () => navigation.goBack(),
                    },
                ]
            );
        } catch (error: any) {
            Alert.alert('注册失败', error.message || '请稍后重试');
        } finally {
            setLoading(false);
        }
    };

    // 更新字段值并清除对应错误
    const updateField = (field: string, value: string, setter: (v: string) => void) => {
        setter(value);
        if (errors[field]) {
            setErrors((prev) => {
                const newErrors = { ...prev };
                delete newErrors[field];
                return newErrors;
            });
        }
    };

    return (
        <KeyboardAvoidingView
            style={styles.container}
            behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
        >
            <ScrollView
                contentContainerStyle={styles.scrollContainer}
                keyboardShouldPersistTaps="handled"
            >
                <View style={styles.formContainer}>
                    {/* 标题 */}
                    <Text style={styles.title}>创建账号</Text>
                    <Text style={styles.subtitle}>加入 Novel Voice，开启阅读之旅</Text>
                    
                    {/* 用户名 */}
                    <View style={styles.inputWrapper}>
                        <Text style={styles.label}>用户名 *</Text>
                        <TextInput
                            style={[styles.input, errors.username ? styles.inputError : null]}
                            placeholder="3-20位字母、数字或下划线"
                            placeholderTextColor="#999"
                            value={username}
                            onChangeText={(v) => updateField('username', v, setUsername)}
                            autoCapitalize="none"
                            autoCorrect={false}
                        />
                        {errors.username ? (
                            <Text style={styles.errorText}>{errors.username}</Text>
                        ) : null}
                    </View>
                    
                    {/* 密码 */}
                    <View style={styles.inputWrapper}>
                        <Text style={styles.label}>密码 *</Text>
                        <TextInput
                            style={[styles.input, errors.password ? styles.inputError : null]}
                            placeholder="至少6位密码"
                            placeholderTextColor="#999"
                            secureTextEntry
                            value={password}
                            onChangeText={(v) => updateField('password', v, setPassword)}
                        />
                        {errors.password ? (
                            <Text style={styles.errorText}>{errors.password}</Text>
                        ) : null}
                    </View>
                    
                    {/* 确认密码 */}
                    <View style={styles.inputWrapper}>
                        <Text style={styles.label}>确认密码 *</Text>
                        <TextInput
                            style={[styles.input, errors.confirmPassword ? styles.inputError : null]}
                            placeholder="再次输入密码"
                            placeholderTextColor="#999"
                            secureTextEntry
                            value={confirmPassword}
                            onChangeText={(v) => updateField('confirmPassword', v, setConfirmPassword)}
                        />
                        {errors.confirmPassword ? (
                            <Text style={styles.errorText}>{errors.confirmPassword}</Text>
                        ) : null}
                    </View>
                    
                    {/* 昵称 */}
                    <View style={styles.inputWrapper}>
                        <Text style={styles.label}>昵称（选填）</Text>
                        <TextInput
                            style={[styles.input, errors.nickname ? styles.inputError : null]}
                            placeholder="给自己取个昵称吧"
                            placeholderTextColor="#999"
                            value={nickname}
                            onChangeText={(v) => updateField('nickname', v, setNickname)}
                        />
                        {errors.nickname ? (
                            <Text style={styles.errorText}>{errors.nickname}</Text>
                        ) : null}
                    </View>
                    
                    {/* 注册按钮 */}
                    <TouchableOpacity
                        style={[styles.registerBtn, loading && styles.registerBtnDisabled]}
                        onPress={handleRegister}
                        disabled={loading}
                    >
                        {loading ? (
                            <ActivityIndicator color="#fff" />
                        ) : (
                            <Text style={styles.registerBtnText}>注 册</Text>
                        )}
                    </TouchableOpacity>
                    
                    {/* 返回登录 */}
                    <TouchableOpacity
                        style={styles.backBtn}
                        onPress={() => navigation.goBack()}
                    >
                        <Text style={styles.backBtnText}>← 返回登录</Text>
                    </TouchableOpacity>
                </View>
            </ScrollView>
        </KeyboardAvoidingView>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#f5f5dc',
    },
    scrollContainer: {
        flexGrow: 1,
        justifyContent: 'center',
    },
    formContainer: {
        paddingHorizontal: 30,
        paddingVertical: 40,
    },
    title: {
        fontSize: 28,
        fontWeight: 'bold',
        color: '#8b4513',
        textAlign: 'center',
        marginBottom: 10,
    },
    subtitle: {
        fontSize: 14,
        color: '#a0522d',
        textAlign: 'center',
        marginBottom: 30,
    },
    inputWrapper: {
        marginBottom: 18,
    },
    label: {
        fontSize: 14,
        color: '#666',
        marginBottom: 8,
        marginLeft: 5,
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
    registerBtn: {
        backgroundColor: '#8b4513',
        borderRadius: 10,
        padding: 15,
        alignItems: 'center',
        marginTop: 15,
    },
    registerBtnDisabled: {
        backgroundColor: '#ccc',
    },
    registerBtnText: {
        color: '#fff',
        fontSize: 18,
        fontWeight: 'bold',
    },
    backBtn: {
        marginTop: 20,
        alignItems: 'center',
    },
    backBtnText: {
        color: '#8b4513',
        fontSize: 16,
    },
});

export default RegisterScreen;
