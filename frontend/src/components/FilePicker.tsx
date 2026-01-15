import React, { useState } from 'react';
import {
    View,
    Text,
    TouchableOpacity,
    StyleSheet,
    Alert,
    ActivityIndicator,
} from 'react-native';
import DocumentPicker, { DocumentPickerResponse } from 'react-native-document-picker';
import fileService from '../services/fileService';
import { FileUploadResult } from '../types';

/**
 * 文件选择器属性
 */
interface FilePickerProps {
    // 文件上传成功回调
    onFileUploaded?: (result: FileUploadResult) => void;
    // 上传子目录
    subDir?: string;
    // 允许的文件类型
    allowedTypes?: string[];
    // 按钮文本
    buttonText?: string;
    // 最大文件大小（字节）
    maxSize?: number;
    // 是否禁用
    disabled?: boolean;
}

/**
 * 文件选择上传组件
 * 支持选择各类文件并上传到对象存储
 */
const FilePicker: React.FC<FilePickerProps> = ({
    onFileUploaded,
    subDir = 'files',
    allowedTypes,
    buttonText = '选择文件',
    maxSize = 10 * 1024 * 1024, // 默认10MB
    disabled = false,
}) => {
    // 上传中状态
    const [uploading, setUploading] = useState(false);
    // 上传进度
    const [progress, setProgress] = useState(0);
    // 选中的文件名
    const [selectedFileName, setSelectedFileName] = useState<string | undefined>(undefined);

    /**
     * 选择文件
     */
    const pickFile = async () => {
        if (uploading || disabled) return;

        try {
            // 配置文件类型
            const types = allowedTypes || [DocumentPicker.types.allFiles];

            const result = await DocumentPicker.pick({
                type: types,
                copyTo: 'cachesDirectory',
            });

            if (result && result.length > 0) {
                const file = result[0];
                await handleFileSelected(file);
            }
        } catch (error: any) {
            if (!DocumentPicker.isCancel(error)) {
                Alert.alert('错误', '选择文件失败');
                console.error('选择文件失败:', error);
            }
        }
    };

    /**
     * 处理选中的文件
     */
    const handleFileSelected = async (file: DocumentPickerResponse) => {
        // 检查文件大小
        if (file.size && file.size > maxSize) {
            Alert.alert(
                '文件过大',
                `文件大小不能超过 ${fileService.formatFileSize(maxSize)}`
            );
            return;
        }

        // 获取文件URI
        const uri = file.fileCopyUri || file.uri;
        if (!uri) {
            Alert.alert('错误', '无法获取文件');
            return;
        }

        setSelectedFileName(file.name || '未知文件');
        await uploadFile(uri, file.name || `file_${Date.now()}`, file.type || 'application/octet-stream');
    };

    /**
     * 上传文件
     */
    const uploadFile = async (uri: string, fileName: string, mimeType: string) => {
        setUploading(true);
        setProgress(0);

        try {
            // 使用带进度的上传
            const result = await fileService.uploadWithProgress(
                uri,
                fileName,
                mimeType,
                `/files/upload/${subDir}`,
                (prog) => setProgress(prog)
            );

            // 上传成功
            onFileUploaded?.(result);
            setSelectedFileName(undefined);
        } catch (error: any) {
            Alert.alert('上传失败', error.message || '文件上传失败，请重试');
        } finally {
            setUploading(false);
            setProgress(0);
        }
    };

    return (
        <View style={styles.container}>
            <TouchableOpacity
                style={[
                    styles.button,
                    uploading && styles.buttonUploading,
                    disabled && styles.buttonDisabled,
                ]}
                onPress={pickFile}
                disabled={uploading || disabled}
            >
                {uploading ? (
                    <View style={styles.uploadingContent}>
                        <ActivityIndicator size="small" color="#fff" />
                        <Text style={styles.buttonText}>上传中 {progress}%</Text>
                    </View>
                ) : (
                    <Text style={styles.buttonText}>{buttonText}</Text>
                )}
            </TouchableOpacity>

            {selectedFileName && !uploading && (
                <Text style={styles.fileName} numberOfLines={1}>
                    已选择: {selectedFileName}
                </Text>
            )}
        </View>
    );
};

const styles = StyleSheet.create({
    container: {
        alignItems: 'flex-start',
    },
    button: {
        backgroundColor: '#007AFF',
        paddingHorizontal: 20,
        paddingVertical: 12,
        borderRadius: 8,
        minWidth: 120,
        alignItems: 'center',
    },
    buttonUploading: {
        backgroundColor: '#999',
    },
    buttonDisabled: {
        backgroundColor: '#ccc',
    },
    buttonText: {
        color: '#fff',
        fontSize: 14,
        fontWeight: '600',
    },
    uploadingContent: {
        flexDirection: 'row',
        alignItems: 'center',
    },
    fileName: {
        marginTop: 8,
        fontSize: 12,
        color: '#666',
        maxWidth: 200,
    },
});

export default FilePicker;
