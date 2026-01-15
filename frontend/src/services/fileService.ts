import { Platform } from 'react-native';
import api from './api';
import { FileUploadResult, FileUploadOptions, StorageInfo } from '../types';

// 文件上传基础URL
const BASE_URL = Platform.OS === 'android' ? 'http://10.0.2.2:8080/api' : 'http://localhost:8080/api';

/**
 * 文件上传服务
 * 支持上传到 MinIO 或阿里云 OSS
 */
class FileService {

    /**
     * 上传图片
     * @param uri 图片URI（本地路径）
     * @param fileName 文件名
     * @param options 上传选项
     * @returns 上传结果
     */
    async uploadImage(
        uri: string, 
        fileName: string, 
        options?: FileUploadOptions
    ): Promise<FileUploadResult> {
        return this.uploadFile(uri, fileName, 'image/*', '/files/upload/image', options);
    }

    /**
     * 上传头像
     * @param uri 图片URI
     * @param fileName 文件名
     * @param options 上传选项
     * @returns 上传结果
     */
    async uploadAvatar(
        uri: string, 
        fileName: string, 
        options?: FileUploadOptions
    ): Promise<FileUploadResult> {
        return this.uploadFile(uri, fileName, 'image/*', '/files/upload/avatars', options);
    }

    /**
     * 上传聊天图片
     * @param uri 图片URI
     * @param fileName 文件名
     * @param options 上传选项
     * @returns 上传结果
     */
    async uploadChatImage(
        uri: string, 
        fileName: string, 
        options?: FileUploadOptions
    ): Promise<FileUploadResult> {
        return this.uploadFile(uri, fileName, 'image/*', '/files/upload/chat-images', options);
    }

    /**
     * 上传聊天文件
     * @param uri 文件URI
     * @param fileName 文件名
     * @param mimeType MIME类型
     * @param options 上传选项
     * @returns 上传结果
     */
    async uploadChatFile(
        uri: string, 
        fileName: string, 
        mimeType: string,
        options?: FileUploadOptions
    ): Promise<FileUploadResult> {
        return this.uploadFile(uri, fileName, mimeType, '/files/upload/chat-files', options);
    }

    /**
     * 上传小说封面
     * @param uri 图片URI
     * @param fileName 文件名
     * @param options 上传选项
     * @returns 上传结果
     */
    async uploadNovelCover(
        uri: string, 
        fileName: string, 
        options?: FileUploadOptions
    ): Promise<FileUploadResult> {
        return this.uploadFile(uri, fileName, 'image/*', '/files/upload/novel-covers', options);
    }

    /**
     * 通用文件上传
     * @param uri 文件URI
     * @param fileName 文件名
     * @param mimeType MIME类型
     * @param endpoint 上传端点
     * @param options 上传选项
     * @returns 上传结果
     */
    async uploadFile(
        uri: string, 
        fileName: string, 
        mimeType: string,
        endpoint: string,
        options?: FileUploadOptions
    ): Promise<FileUploadResult> {
        // 创建 FormData
        const formData = new FormData();
        
        // 添加文件
        formData.append('file', {
            uri: Platform.OS === 'android' ? uri : uri.replace('file://', ''),
            type: mimeType,
            name: fileName,
        } as any);

        // 添加存储类型参数（如果指定）
        if (options?.storageType) {
            formData.append('storageType', options.storageType);
        }

        try {
            // 使用 fetch 进行上传（支持进度回调）
            const response = await fetch(`${BASE_URL}${endpoint}`, {
                method: 'POST',
                body: formData,
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            });

            if (!response.ok) {
                throw new Error(`上传失败: ${response.status}`);
            }

            const result = await response.json();
            
            // 处理统一返回格式
            if (result.code === 200) {
                return result.data as FileUploadResult;
            } else {
                throw new Error(result.msg || '上传失败');
            }
        } catch (error: any) {
            console.error('文件上传失败:', error);
            throw error;
        }
    }

    /**
     * 使用 XMLHttpRequest 上传（支持进度回调）
     * @param uri 文件URI
     * @param fileName 文件名
     * @param mimeType MIME类型
     * @param endpoint 上传端点
     * @param onProgress 进度回调
     * @returns 上传结果
     */
    uploadWithProgress(
        uri: string, 
        fileName: string, 
        mimeType: string,
        endpoint: string,
        onProgress?: (progress: number) => void
    ): Promise<FileUploadResult> {
        return new Promise((resolve, reject) => {
            const xhr = new XMLHttpRequest();
            
            // 创建 FormData
            const formData = new FormData();
            formData.append('file', {
                uri: Platform.OS === 'android' ? uri : uri.replace('file://', ''),
                type: mimeType,
                name: fileName,
            } as any);

            // 监听上传进度
            if (onProgress) {
                xhr.upload.onprogress = (event) => {
                    if (event.lengthComputable) {
                        const progress = Math.round((event.loaded / event.total) * 100);
                        onProgress(progress);
                    }
                };
            }

            // 监听完成事件
            xhr.onload = () => {
                if (xhr.status >= 200 && xhr.status < 300) {
                    try {
                        const result = JSON.parse(xhr.responseText);
                        if (result.code === 200) {
                            resolve(result.data as FileUploadResult);
                        } else {
                            reject(new Error(result.msg || '上传失败'));
                        }
                    } catch (e) {
                        reject(new Error('解析响应失败'));
                    }
                } else {
                    reject(new Error(`上传失败: ${xhr.status}`));
                }
            };

            // 监听错误事件
            xhr.onerror = () => {
                reject(new Error('网络错误'));
            };

            // 发送请求
            xhr.open('POST', `${BASE_URL}${endpoint}`);
            xhr.send(formData);
        });
    }

    /**
     * 删除文件
     * @param fileKey 文件Key
     * @returns 是否删除成功
     */
    async deleteFile(fileKey: string): Promise<boolean> {
        try {
            const result: any = await api.delete('/files/delete', {
                params: { fileKey }
            });
            return result.success === true;
        } catch (error) {
            console.error('删除文件失败:', error);
            return false;
        }
    }

    /**
     * 获取文件预签名URL
     * @param fileKey 文件Key
     * @param expireSeconds 过期时间（秒）
     * @returns 预签名URL
     */
    async getPresignedUrl(fileKey: string, expireSeconds: number = 3600): Promise<string> {
        try {
            const result: any = await api.get('/files/presigned-url', {
                params: { fileKey, expireSeconds }
            });
            return result.url;
        } catch (error) {
            console.error('获取预签名URL失败:', error);
            throw error;
        }
    }

    /**
     * 检查文件是否存在
     * @param fileKey 文件Key
     * @returns 是否存在
     */
    async checkExists(fileKey: string): Promise<boolean> {
        try {
            const result: any = await api.get('/files/exists', {
                params: { fileKey }
            });
            return result.exists === true;
        } catch (error) {
            console.error('检查文件是否存在失败:', error);
            return false;
        }
    }

    /**
     * 获取存储信息
     * @returns 存储信息
     */
    async getStorageInfo(): Promise<StorageInfo> {
        try {
            const result: any = await api.get('/files/storage-info');
            return result as StorageInfo;
        } catch (error) {
            console.error('获取存储信息失败:', error);
            throw error;
        }
    }

    /**
     * 格式化文件大小
     * @param bytes 字节数
     * @returns 格式化后的大小字符串
     */
    formatFileSize(bytes: number): string {
        if (bytes < 1024) {
            return bytes + ' B';
        } else if (bytes < 1024 * 1024) {
            return (bytes / 1024).toFixed(2) + ' KB';
        } else if (bytes < 1024 * 1024 * 1024) {
            return (bytes / (1024 * 1024)).toFixed(2) + ' MB';
        } else {
            return (bytes / (1024 * 1024 * 1024)).toFixed(2) + ' GB';
        }
    }

    /**
     * 获取文件扩展名
     * @param fileName 文件名
     * @returns 扩展名
     */
    getFileExtension(fileName: string): string {
        const lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0) {
            return fileName.substring(lastDot + 1).toLowerCase();
        }
        return '';
    }

    /**
     * 判断是否为图片文件
     * @param mimeType MIME类型
     * @returns 是否为图片
     */
    isImage(mimeType: string): boolean {
        return mimeType.startsWith('image/');
    }
}

// 导出单例实例
const fileService = new FileService();
export default fileService;
