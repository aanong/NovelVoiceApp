package com.app.novelvoice.netty.proto;

import com.google.protobuf.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Protobuf 消息类 - 聊天消息
 * 支持群聊、私聊、表情、图片、文件等多种消息类型
 */
public final class ChatMessage extends GeneratedMessageV3 implements ChatMessageOrBuilder {
    
    private ChatMessage() {
        content_ = "";
        timestamp_ = "";
        fileUrl_ = "";
        fileName_ = "";
        senderNickname_ = "";
        senderAvatar_ = "";
    }

    private ChatMessage(GeneratedMessageV3.Builder<?> builder) {
        super(builder);
    }

    @Override
    protected Object newInstance(UnusedPrivateParameter unused) {
        return new ChatMessage();
    }

    @Override
    public final UnknownFieldSet getUnknownFields() {
        return this.unknownFields;
    }

    // 字段1：发送者ID
    public static final int SENDER_ID_FIELD_NUMBER = 1;
    private long senderId_;

    @Override
    public long getSenderId() {
        return senderId_;
    }

    // 字段2：接收者ID（0表示群聊）
    public static final int RECEIVER_ID_FIELD_NUMBER = 2;
    private long receiverId_;

    public long getReceiverId() {
        return receiverId_;
    }

    // 字段3：消息内容
    public static final int CONTENT_FIELD_NUMBER = 3;
    private Object content_;

    @Override
    public String getContent() {
        Object ref = content_;
        if (ref instanceof String)
            return (String) ref;
        ByteString bs = (ByteString) ref;
        String s = bs.toStringUtf8();
        content_ = s;
        return s;
    }

    @Override
    public ByteString getContentBytes() {
        Object ref = content_;
        if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String) ref);
            content_ = b;
            return b;
        } else
            return (ByteString) ref;
    }

    // 字段4：消息类型
    public static final int TYPE_FIELD_NUMBER = 4;
    private int type_;

    @Override
    public int getType() {
        return type_;
    }

    // 字段5：时间戳
    public static final int TIMESTAMP_FIELD_NUMBER = 5;
    private Object timestamp_;

    @Override
    public String getTimestamp() {
        Object ref = timestamp_;
        if (ref instanceof String)
            return (String) ref;
        ByteString bs = (ByteString) ref;
        String s = bs.toStringUtf8();
        timestamp_ = s;
        return s;
    }

    @Override
    public ByteString getTimestampBytes() {
        Object ref = timestamp_;
        if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String) ref);
            timestamp_ = b;
            return b;
        } else
            return (ByteString) ref;
    }

    // 字段6：文件URL
    public static final int FILE_URL_FIELD_NUMBER = 6;
    private Object fileUrl_;

    public String getFileUrl() {
        Object ref = fileUrl_;
        if (ref instanceof String)
            return (String) ref;
        ByteString bs = (ByteString) ref;
        String s = bs.toStringUtf8();
        fileUrl_ = s;
        return s;
    }

    public ByteString getFileUrlBytes() {
        Object ref = fileUrl_;
        if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String) ref);
            fileUrl_ = b;
            return b;
        } else
            return (ByteString) ref;
    }

    // 字段7：文件名
    public static final int FILE_NAME_FIELD_NUMBER = 7;
    private Object fileName_;

    public String getFileName() {
        Object ref = fileName_;
        if (ref instanceof String)
            return (String) ref;
        ByteString bs = (ByteString) ref;
        String s = bs.toStringUtf8();
        fileName_ = s;
        return s;
    }

    public ByteString getFileNameBytes() {
        Object ref = fileName_;
        if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String) ref);
            fileName_ = b;
            return b;
        } else
            return (ByteString) ref;
    }

    // 字段8：文件大小
    public static final int FILE_SIZE_FIELD_NUMBER = 8;
    private long fileSize_;

    public long getFileSize() {
        return fileSize_;
    }

    // 字段9：发送者昵称
    public static final int SENDER_NICKNAME_FIELD_NUMBER = 9;
    private Object senderNickname_;

    public String getSenderNickname() {
        Object ref = senderNickname_;
        if (ref instanceof String)
            return (String) ref;
        ByteString bs = (ByteString) ref;
        String s = bs.toStringUtf8();
        senderNickname_ = s;
        return s;
    }

    public ByteString getSenderNicknameBytes() {
        Object ref = senderNickname_;
        if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String) ref);
            senderNickname_ = b;
            return b;
        } else
            return (ByteString) ref;
    }

    // 字段10：发送者头像
    public static final int SENDER_AVATAR_FIELD_NUMBER = 10;
    private Object senderAvatar_;

    public String getSenderAvatar() {
        Object ref = senderAvatar_;
        if (ref instanceof String)
            return (String) ref;
        ByteString bs = (ByteString) ref;
        String s = bs.toStringUtf8();
        senderAvatar_ = s;
        return s;
    }

    public ByteString getSenderAvatarBytes() {
        Object ref = senderAvatar_;
        if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String) ref);
            senderAvatar_ = b;
            return b;
        } else
            return (ByteString) ref;
    }

    @Override
    public final boolean isInitialized() {
        return true;
    }

    @Override
    public void writeTo(CodedOutputStream output) throws IOException {
        if (senderId_ != 0L)
            output.writeInt64(1, senderId_);
        if (receiverId_ != 0L)
            output.writeInt64(2, receiverId_);
        if (!getContentBytes().isEmpty())
            GeneratedMessageV3.writeString(output, 3, content_);
        if (type_ != 0)
            output.writeInt32(4, type_);
        if (!getTimestampBytes().isEmpty())
            GeneratedMessageV3.writeString(output, 5, timestamp_);
        if (!getFileUrlBytes().isEmpty())
            GeneratedMessageV3.writeString(output, 6, fileUrl_);
        if (!getFileNameBytes().isEmpty())
            GeneratedMessageV3.writeString(output, 7, fileName_);
        if (fileSize_ != 0L)
            output.writeInt64(8, fileSize_);
        if (!getSenderNicknameBytes().isEmpty())
            GeneratedMessageV3.writeString(output, 9, senderNickname_);
        if (!getSenderAvatarBytes().isEmpty())
            GeneratedMessageV3.writeString(output, 10, senderAvatar_);
        unknownFields.writeTo(output);
    }

    @Override
    public int getSerializedSize() {
        int size = memoizedSize;
        if (size != -1)
            return size;
        size = 0;
        if (senderId_ != 0L)
            size += CodedOutputStream.computeInt64Size(1, senderId_);
        if (receiverId_ != 0L)
            size += CodedOutputStream.computeInt64Size(2, receiverId_);
        if (!getContentBytes().isEmpty())
            size += GeneratedMessageV3.computeStringSize(3, content_);
        if (type_ != 0)
            size += CodedOutputStream.computeInt32Size(4, type_);
        if (!getTimestampBytes().isEmpty())
            size += GeneratedMessageV3.computeStringSize(5, timestamp_);
        if (!getFileUrlBytes().isEmpty())
            size += GeneratedMessageV3.computeStringSize(6, fileUrl_);
        if (!getFileNameBytes().isEmpty())
            size += GeneratedMessageV3.computeStringSize(7, fileName_);
        if (fileSize_ != 0L)
            size += CodedOutputStream.computeInt64Size(8, fileSize_);
        if (!getSenderNicknameBytes().isEmpty())
            size += GeneratedMessageV3.computeStringSize(9, senderNickname_);
        if (!getSenderAvatarBytes().isEmpty())
            size += GeneratedMessageV3.computeStringSize(10, senderAvatar_);
        size += unknownFields.getSerializedSize();
        memoizedSize = size;
        return size;
    }

    public static ChatMessage parseFrom(byte[] data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static ChatMessage parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static ChatMessage parseFrom(ByteString data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static Builder newBuilder() {
        return DEFAULT_INSTANCE.toBuilder();
    }

    public static Builder newBuilder(ChatMessage prototype) {
        return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }

    @Override
    public Builder newBuilderForType() {
        return newBuilder();
    }

    @Override
    protected Builder newBuilderForType(BuilderParent parent) {
        return new Builder(parent);
    }

    @Override
    public Builder toBuilder() {
        return this == DEFAULT_INSTANCE ? new Builder() : new Builder().mergeFrom(this);
    }

    public static ChatMessage getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    @Override
    public ChatMessage getDefaultInstanceForType() {
        return DEFAULT_INSTANCE;
    }

    public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements ChatMessageOrBuilder {
        private long senderId_;
        private long receiverId_;
        private Object content_ = "";
        private int type_;
        private Object timestamp_ = "";
        private Object fileUrl_ = "";
        private Object fileName_ = "";
        private long fileSize_;
        private Object senderNickname_ = "";
        private Object senderAvatar_ = "";

        private Builder() {
            maybeForceBuilderInitialization();
        }

        private Builder(BuilderParent parent) {
            super(parent);
            maybeForceBuilderInitialization();
        }

        private void maybeForceBuilderInitialization() {
        }

        @Override
        public Builder clear() {
            super.clear();
            senderId_ = 0L;
            receiverId_ = 0L;
            content_ = "";
            type_ = 0;
            timestamp_ = "";
            fileUrl_ = "";
            fileName_ = "";
            fileSize_ = 0L;
            senderNickname_ = "";
            senderAvatar_ = "";
            return this;
        }

        @Override
        public Descriptors.Descriptor getDescriptorForType() {
            return ChatMessageProto.ChatMessageDescriptor;
        }

        @Override
        public ChatMessage getDefaultInstanceForType() {
            return ChatMessage.getDefaultInstance();
        }

        @Override
        public ChatMessage build() {
            ChatMessage result = buildPartial();
            if (!result.isInitialized())
                throw newUninitializedMessageException(result);
            return result;
        }

        @Override
        public ChatMessage buildPartial() {
            ChatMessage result = new ChatMessage(this);
            result.senderId_ = senderId_;
            result.receiverId_ = receiverId_;
            result.content_ = content_;
            result.type_ = type_;
            result.timestamp_ = timestamp_;
            result.fileUrl_ = fileUrl_;
            result.fileName_ = fileName_;
            result.fileSize_ = fileSize_;
            result.senderNickname_ = senderNickname_;
            result.senderAvatar_ = senderAvatar_;
            onBuilt();
            return result;
        }

        @Override
        public Builder mergeFrom(Message other) {
            if (other instanceof ChatMessage)
                return mergeFrom((ChatMessage) other);
            super.mergeFrom(other);
            return this;
        }

        public Builder mergeFrom(ChatMessage other) {
            if (other == ChatMessage.getDefaultInstance())
                return this;
            if (other.getSenderId() != 0L)
                setSenderId(other.getSenderId());
            if (other.getReceiverId() != 0L)
                setReceiverId(other.getReceiverId());
            if (!other.getContent().isEmpty()) {
                content_ = other.content_;
                onChanged();
            }
            if (other.getType() != 0)
                setType(other.getType());
            if (!other.getTimestamp().isEmpty()) {
                timestamp_ = other.timestamp_;
                onChanged();
            }
            if (!other.getFileUrl().isEmpty()) {
                fileUrl_ = other.fileUrl_;
                onChanged();
            }
            if (!other.getFileName().isEmpty()) {
                fileName_ = other.fileName_;
                onChanged();
            }
            if (other.getFileSize() != 0L)
                setFileSize(other.getFileSize());
            if (!other.getSenderNickname().isEmpty()) {
                senderNickname_ = other.senderNickname_;
                onChanged();
            }
            if (!other.getSenderAvatar().isEmpty()) {
                senderAvatar_ = other.senderAvatar_;
                onChanged();
            }
            this.mergeUnknownFields(other.unknownFields);
            onChanged();
            return this;
        }

        @Override
        public final boolean isInitialized() {
            return true;
        }

        @Override
        public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            ChatMessage parsedMessage = null;
            try {
                parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (InvalidProtocolBufferException e) {
                parsedMessage = (ChatMessage) e.getUnfinishedMessage();
                throw e.unwrapIOException();
            } finally {
                if (parsedMessage != null)
                    mergeFrom(parsedMessage);
            }
            return this;
        }

        // senderId
        public long getSenderId() {
            return senderId_;
        }

        public Builder setSenderId(long value) {
            senderId_ = value;
            onChanged();
            return this;
        }

        // receiverId
        public long getReceiverId() {
            return receiverId_;
        }

        public Builder setReceiverId(long value) {
            receiverId_ = value;
            onChanged();
            return this;
        }

        // content
        public String getContent() {
            Object ref = content_;
            if (!(ref instanceof String)) {
                ByteString bs = (ByteString) ref;
                String s = bs.toStringUtf8();
                content_ = s;
                return s;
            } else
                return (String) ref;
        }

        public Builder setContent(String value) {
            if (value == null)
                throw new NullPointerException();
            content_ = value;
            onChanged();
            return this;
        }

        public ByteString getContentBytes() {
            Object ref = content_;
            if (ref instanceof String) {
                ByteString b = ByteString.copyFromUtf8((String) ref);
                content_ = b;
                return b;
            } else
                return (ByteString) ref;
        }

        // type
        public int getType() {
            return type_;
        }

        public Builder setType(int value) {
            type_ = value;
            onChanged();
            return this;
        }

        // timestamp
        public String getTimestamp() {
            Object ref = timestamp_;
            if (!(ref instanceof String)) {
                ByteString bs = (ByteString) ref;
                String s = bs.toStringUtf8();
                timestamp_ = s;
                return s;
            } else
                return (String) ref;
        }

        public Builder setTimestamp(String value) {
            if (value == null)
                throw new NullPointerException();
            timestamp_ = value;
            onChanged();
            return this;
        }

        public ByteString getTimestampBytes() {
            Object ref = timestamp_;
            if (ref instanceof String) {
                ByteString b = ByteString.copyFromUtf8((String) ref);
                timestamp_ = b;
                return b;
            } else
                return (ByteString) ref;
        }

        // fileUrl
        public String getFileUrl() {
            Object ref = fileUrl_;
            if (!(ref instanceof String)) {
                ByteString bs = (ByteString) ref;
                String s = bs.toStringUtf8();
                fileUrl_ = s;
                return s;
            } else
                return (String) ref;
        }

        public Builder setFileUrl(String value) {
            if (value == null)
                throw new NullPointerException();
            fileUrl_ = value;
            onChanged();
            return this;
        }

        // fileName
        public String getFileName() {
            Object ref = fileName_;
            if (!(ref instanceof String)) {
                ByteString bs = (ByteString) ref;
                String s = bs.toStringUtf8();
                fileName_ = s;
                return s;
            } else
                return (String) ref;
        }

        public Builder setFileName(String value) {
            if (value == null)
                throw new NullPointerException();
            fileName_ = value;
            onChanged();
            return this;
        }

        // fileSize
        public long getFileSize() {
            return fileSize_;
        }

        public Builder setFileSize(long value) {
            fileSize_ = value;
            onChanged();
            return this;
        }

        // senderNickname
        public String getSenderNickname() {
            Object ref = senderNickname_;
            if (!(ref instanceof String)) {
                ByteString bs = (ByteString) ref;
                String s = bs.toStringUtf8();
                senderNickname_ = s;
                return s;
            } else
                return (String) ref;
        }

        public Builder setSenderNickname(String value) {
            if (value == null)
                throw new NullPointerException();
            senderNickname_ = value;
            onChanged();
            return this;
        }

        // senderAvatar
        public String getSenderAvatar() {
            Object ref = senderAvatar_;
            if (!(ref instanceof String)) {
                ByteString bs = (ByteString) ref;
                String s = bs.toStringUtf8();
                senderAvatar_ = s;
                return s;
            } else
                return (String) ref;
        }

        public Builder setSenderAvatar(String value) {
            if (value == null)
                throw new NullPointerException();
            senderAvatar_ = value;
            onChanged();
            return this;
        }

        @Override
        protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
            return ChatMessageProto.ChatMessageFieldAccessorTable;
        }
    }

    private static final ChatMessage DEFAULT_INSTANCE = new ChatMessage();
    private static final Parser<ChatMessage> PARSER = new AbstractParser<ChatMessage>() {
        @Override
        public ChatMessage parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry)
                throws InvalidProtocolBufferException {
            return new ChatMessage(input, extensionRegistry);
        }
    };

    @Override
    public Parser<ChatMessage> getParserForType() {
        return PARSER;
    }

    @Override
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return ChatMessageProto.ChatMessageFieldAccessorTable;
    }

    private ChatMessage(CodedInputStream input, ExtensionRegistryLite extensionRegistry)
            throws InvalidProtocolBufferException {
        this();
        UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();
        try {
            boolean done = false;
            while (!done) {
                int tag = input.readTag();
                switch (tag) {
                    case 0:
                        done = true;
                        break;
                    case 8: // senderId (field 1, wire type 0)
                        senderId_ = input.readInt64();
                        break;
                    case 16: // receiverId (field 2, wire type 0)
                        receiverId_ = input.readInt64();
                        break;
                    case 26: // content (field 3, wire type 2)
                        content_ = input.readStringRequireUtf8();
                        break;
                    case 32: // type (field 4, wire type 0)
                        type_ = input.readInt32();
                        break;
                    case 42: // timestamp (field 5, wire type 2)
                        timestamp_ = input.readStringRequireUtf8();
                        break;
                    case 50: // fileUrl (field 6, wire type 2)
                        fileUrl_ = input.readStringRequireUtf8();
                        break;
                    case 58: // fileName (field 7, wire type 2)
                        fileName_ = input.readStringRequireUtf8();
                        break;
                    case 64: // fileSize (field 8, wire type 0)
                        fileSize_ = input.readInt64();
                        break;
                    case 74: // senderNickname (field 9, wire type 2)
                        senderNickname_ = input.readStringRequireUtf8();
                        break;
                    case 82: // senderAvatar (field 10, wire type 2)
                        senderAvatar_ = input.readStringRequireUtf8();
                        break;
                    default:
                        if (!parseUnknownField(input, unknownFields, extensionRegistry, tag))
                            done = true;
                        break;
                }
            }
        } catch (IOException e) {
            throw new InvalidProtocolBufferException(e).setUnfinishedMessage(this);
        } finally {
            this.unknownFields = unknownFields.build();
            makeExtensionsImmutable();
        }
    }
}
