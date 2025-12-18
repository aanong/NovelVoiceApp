import axios from 'axios';
import { Platform } from 'react-native';

// Android Emulator uses 10.0.2.2 for localhost
const BASE_URL = Platform.OS === 'android' ? 'http://10.0.2.2:8080/api' : 'http://localhost:8080/api';
export const WS_URL = Platform.OS === 'android' ? 'ws://10.0.2.2:8081/ws' : 'ws://localhost:8081/ws';

const api = axios.create({
    baseURL: BASE_URL,
    timeout: 10000,
});

export default api;
