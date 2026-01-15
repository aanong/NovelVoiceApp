import React, { useState } from 'react';
import {
    View,
    Text,
    TouchableOpacity,
    Image,
    StyleSheet,
    Alert,
    ActivityIndicator,
    Modal,
} from 'react-native';
import { launchImageLibrary, launchCamera, ImagePickerResponse, Asset } from 'react-native-image-picker';
import fileService from '../services/fileService';
import { FileUploadResult } from '../types';

/**
 * 图片选择器属性
 */
interface ImagePickerProps {
    // 当前图片URL
    imageUrl?: string;
    // 图片上传成功回调
    onImageUploaded?: (result: FileUploadResult) => void;
    // 上传子目录
    subDir?: string;
    // 占位文本
    placeholder?: string;
    // 是否显示删除按钮
    showDelete?: boolean;
    // 删除回调
    onDelete?: () => void;
    // 宽度
    width?: number;
    // 高度
    height?: number;
    // 是否圆形（用于头像）
    circular?: boolean;
}

/**
 * 图片选择上传组件
 * 支持从相册选择或拍照，自动上传到对象存储
 */
const ImagePicker: React.FC<ImagePickerProps> = ({
    imageUrl,
    onImageUploaded,
    subDir = 'images',
    placeholder = '点击选择图片',
    showDelete = false,
    onDelete,
    width = 120,
    height = 120,
    circular = false,
}) => {
    // 上传中状态
    const [uploading, setUploading] = useState(false);
    // 上传进度
    const [progress, setProgress] = useState(0);
    // 显示选择弹窗
    const [showModal, setShowModal] = useState(false);
    // 预览图片（本地选择后的临时预览）
    const [previewUri, setPreviewUri] = useState<string | undefined>(undefined);

    /**
     * 打开选择弹窗
     */
    const handlePress = () => {
        if (uploading) return;
        setShowModal(true);
    };

    /**
     * 从相册选择图片
     */
    const pickFromGallery = () => {
        setShowModal(false);
        launchImageLibrary(
            {
                mediaType: 'photo',
                quality: 0.8,
                maxWidth: 1920,
                maxHeight: 1920,
            },
            handleImageResponse
        );
    };

    /**
     * 拍照
     */
    const takePhoto = () => {
        setShowModal(false);
        launchCamera(
            {
                mediaType: 'photo',
                quality: 0.8,
                maxWidth: 1920,
                maxHeight: 1920,
                saveToPhotos: false,
            },
            handleImageResponse
        );
    };

    /**
     * 处理图片选择结果
     */
    const handleImageResponse = async (response: ImagePickerResponse) => {
        if (response.didCancel) {
            return;
        }

        if (response.errorCode) {
            Alert.alert('错误', response.errorMessage || '选择图片失败');
            return;
        }

        const assets = response.assets;
        if (!assets || assets.length === 0) {
            return;
        }

        const asset: Asset = assets[0];
        if (!asset.uri) {
            Alert.alert('错误', '无法获取图片');
            return;
        }

        // 设置预览
        setPreviewUri(asset.uri);

        // 开始上传
        await uploadImage(asset);
    };

    /**
     * 上传图片
     */
    const uploadImage = async (asset: Asset) => {
        if (!asset.uri) return;

        setUploading(true);
        setProgress(0);

        try {
            const fileName = asset.fileName || `image_${Date.now()}.jpg`;
            
            // 使用带进度的上传
            const result = await fileService.uploadWithProgress(
                asset.uri,
                fileName,
                asset.type || 'image/jpeg',
                `/files/upload/${subDir}`,
                (prog) => setProgress(prog)
            );

            // 上传成功
            setPreviewUri(undefined);
            onImageUploaded?.(result);
        } catch (error: any) {
            Alert.alert('上传失败', error.message || '图片上传失败，请重试');
            setPreviewUri(undefined);
        } finally {
            setUploading(false);
            setProgress(0);
        }
    };

    /**
     * 删除图片
     */
    const handleDelete = () => {
        Alert.alert(
            '确认删除',
            '确定要删除这张图片吗？',
            [
                { text: '取消', style: 'cancel' },
                { 
                    text: '删除', 
                    style: 'destructive',
                    onPress: () => onDelete?.()
                },
            ]
        );
    };

    // 显示的图片URL
    const displayUrl = previewUri || imageUrl;

    return (
        <View style={styles.container}>
            <TouchableOpacity
                style={[
                    styles.imageContainer,
                    { width, height },
                    circular && { borderRadius: width / 2 },
                ]}
                onPress={handlePress}
                disabled={uploading}
            >
                {displayUrl ? (
                    <Image
                        source={{ uri: displayUrl }}
                        style={[
                            styles.image,
                            { width, height },
                            circular && { borderRadius: width / 2 },
                        ]}
                        resizeMode="cover"
                    />
                ) : (
                    <View style={styles.placeholder}>
                        <Text style={styles.placeholderIcon}>+</Text>
                        <Text style={styles.placeholderText}>{placeholder}</Text>
                    </View>
                )}

                {/* 上传进度遮罩 */}
                {uploading && (
                    <View style={[styles.overlay, circular && { borderRadius: width / 2 }]}>
                        <ActivityIndicator size="large" color="#fff" />
                        <Text style={styles.progressText}>{progress}%</Text>
                    </View>
                )}
            </TouchableOpacity>

            {/* 删除按钮 */}
            {showDelete && displayUrl && !uploading && (
                <TouchableOpacity style={styles.deleteButton} onPress={handleDelete}>
                    <Text style={styles.deleteText}>×</Text>
                </TouchableOpacity>
            )}

            {/* 选择方式弹窗 */}
            <Modal
                visible={showModal}
                transparent
                animationType="fade"
                onRequestClose={() => setShowModal(false)}
            >
                <TouchableOpacity
                    style={styles.modalOverlay}
                    activeOpacity={1}
                    onPress={() => setShowModal(false)}
                >
                    <View style={styles.modalContent}>
                        <Text style={styles.modalTitle}>选择图片</Text>
                        
                        <TouchableOpacity style={styles.modalOption} onPress={pickFromGallery}>
                            <Text style={styles.modalOptionText}>从相册选择</Text>
                        </TouchableOpacity>
                        
                        <TouchableOpacity style={styles.modalOption} onPress={takePhoto}>
                            <Text style={styles.modalOptionText}>拍照</Text>
                        </TouchableOpacity>
                        
                        <TouchableOpacity
                            style={[styles.modalOption, styles.modalCancel]}
                            onPress={() => setShowModal(false)}
                        >
                            <Text style={styles.modalCancelText}>取消</Text>
                        </TouchableOpacity>
                    </View>
                </TouchableOpacity>
            </Modal>
        </View>
    );
};

const styles = StyleSheet.create({
    container: {
        position: 'relative',
    },
    imageContainer: {
        backgroundColor: '#f5f5f5',
        borderRadius: 8,
        borderWidth: 1,
        borderColor: '#ddd',
        borderStyle: 'dashed',
        justifyContent: 'center',
        alignItems: 'center',
        overflow: 'hidden',
    },
    image: {
        borderRadius: 8,
    },
    placeholder: {
        alignItems: 'center',
        justifyContent: 'center',
    },
    placeholderIcon: {
        fontSize: 32,
        color: '#999',
        marginBottom: 4,
    },
    placeholderText: {
        fontSize: 12,
        color: '#999',
    },
    overlay: {
        ...StyleSheet.absoluteFillObject,
        backgroundColor: 'rgba(0, 0, 0, 0.5)',
        justifyContent: 'center',
        alignItems: 'center',
        borderRadius: 8,
    },
    progressText: {
        color: '#fff',
        fontSize: 14,
        marginTop: 8,
    },
    deleteButton: {
        position: 'absolute',
        top: -8,
        right: -8,
        width: 24,
        height: 24,
        borderRadius: 12,
        backgroundColor: '#ff4444',
        justifyContent: 'center',
        alignItems: 'center',
    },
    deleteText: {
        color: '#fff',
        fontSize: 18,
        fontWeight: 'bold',
        lineHeight: 20,
    },
    modalOverlay: {
        flex: 1,
        backgroundColor: 'rgba(0, 0, 0, 0.5)',
        justifyContent: 'flex-end',
    },
    modalContent: {
        backgroundColor: '#fff',
        borderTopLeftRadius: 16,
        borderTopRightRadius: 16,
        paddingBottom: 34,
    },
    modalTitle: {
        fontSize: 16,
        fontWeight: '600',
        color: '#333',
        textAlign: 'center',
        paddingVertical: 16,
        borderBottomWidth: 1,
        borderBottomColor: '#eee',
    },
    modalOption: {
        paddingVertical: 16,
        borderBottomWidth: 1,
        borderBottomColor: '#eee',
    },
    modalOptionText: {
        fontSize: 16,
        color: '#007AFF',
        textAlign: 'center',
    },
    modalCancel: {
        marginTop: 8,
        borderBottomWidth: 0,
    },
    modalCancelText: {
        fontSize: 16,
        color: '#666',
        textAlign: 'center',
    },
});

export default ImagePicker;
