import axios from 'axios';
import { Platform } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';

// Android 模拟器使用 10.0.2.2 访问宿主机 localhost
const BASE_URL = Platform.OS === 'android' ? 'http://10.0.2.2:8080/api' : 'http://localhost:8080/api';
export const WS_URL = Platform.OS === 'android' ? 'ws://10.0.2.2:8081/ws' : 'ws://localhost:8081/ws';

const api = axios.create({
    baseURL: BASE_URL,
    timeout: 10000,
});

// 请求拦截器：添加 Token
api.interceptors.request.use(
    async (config) => {
        try {
            const token = await AsyncStorage.getItem('userToken');
            if (token) {
                config.headers.Authorization = `Bearer ${token}`;
            }
        } catch (e) {
            console.error('获取 Token 失败:', e);
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// 响应拦截器：处理统一返回格式
api.interceptors.response.use(
    (response) => {
        const res = response.data;
        // code 为 200 表示成功，返回 data 部分
        if (res.code === 200) {
            return res.data;
        }
        // 其他情况返回错误信息
        return Promise.reject(new Error(res.msg || '请求失败'));
    },
    (error) => {
        return Promise.reject(error);
    }
);

// 存储用户信息到本地
export const saveUserToStorage = async (user: any) => {
    try {
        await AsyncStorage.setItem('userInfo', JSON.stringify(user));
        if (user.token) {
            await AsyncStorage.setItem('userToken', user.token);
        }
    } catch (e) {
        console.error('保存用户信息失败:', e);
    }
};

// 从本地获取用户信息
export const getUserFromStorage = async () => {
    try {
        const userJson = await AsyncStorage.getItem('userInfo');
        if (userJson) {
            return JSON.parse(userJson);
        }
    } catch (e) {
        console.error('获取用户信息失败:', e);
    }
    return null;
};

// 清除用户信息
export const clearUserStorage = async () => {
    try {
        await AsyncStorage.removeItem('userInfo');
        await AsyncStorage.removeItem('userToken');
    } catch (e) {
        console.error('清除用户信息失败:', e);
    }
};

// 保存阅读进度到本地
export const saveReadingProgressLocal = async (userId: number, novelId: number, progress: any) => {
    try {
        const key = `reading_progress_${userId}_${novelId}`;
        await AsyncStorage.setItem(key, JSON.stringify(progress));
    } catch (e) {
        console.error('保存阅读进度失败:', e);
    }
};

// 获取本地阅读进度
export const getReadingProgressLocal = async (userId: number, novelId: number) => {
    try {
        const key = `reading_progress_${userId}_${novelId}`;
        const progressJson = await AsyncStorage.getItem(key);
        if (progressJson) {
            return JSON.parse(progressJson);
        }
    } catch (e) {
        console.error('获取阅读进度失败:', e);
    }
    return null;
};

export default api;
