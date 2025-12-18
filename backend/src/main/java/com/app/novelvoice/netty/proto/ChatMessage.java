package com.app.novelvoice.netty.proto;

import com.google.protobuf.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class ChatMessage extends GeneratedMessageV3 implements ChatMessageOrBuilder {
    private ChatMessage() {
        content_ = "";
        timestamp_ = "";
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

    public static final int SENDER_ID_FIELD_NUMBER = 1;
    private long senderId_;

    @Override
    public long getSenderId() {
        return senderId_;
    }

    public static final int CONTENT_FIELD_NUMBER = 2;
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

    public static final int TYPE_FIELD_NUMBER = 3;
    private int type_;

    @Override
    public int getType() {
        return type_;
    }

    public static final int TIMESTAMP_FIELD_NUMBER = 4;
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

    @Override
    public final boolean isInitialized() {
        return true;
    }

    @Override
    public void writeTo(CodedOutputStream output) throws IOException {
        if (senderId_ != 0L)
            output.writeInt64(1, senderId_);
        if (!getContentBytes().isEmpty())
            GeneratedMessageV3.writeString(output, 2, content_);
        if (type_ != 0)
            output.writeInt32(3, type_);
        if (!getTimestampBytes().isEmpty())
            GeneratedMessageV3.writeString(output, 4, timestamp_);
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
        if (!getContentBytes().isEmpty())
            size += GeneratedMessageV3.computeStringSize(2, content_);
        if (type_ != 0)
            size += CodedOutputStream.computeInt32Size(3, type_);
        if (!getTimestampBytes().isEmpty())
            size += GeneratedMessageV3.computeStringSize(4, timestamp_);
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
        private Object content_ = "";
        private int type_;
        private Object timestamp_ = "";

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
            content_ = "";
            type_ = 0;
            timestamp_ = "";
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
            result.content_ = content_;
            result.type_ = type_;
            result.timestamp_ = timestamp_;
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

        public long getSenderId() {
            return senderId_;
        }

        public Builder setSenderId(long value) {
            senderId_ = value;
            onChanged();
            return this;
        }

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

        public int getType() {
            return type_;
        }

        public Builder setType(int value) {
            type_ = value;
            onChanged();
            return this;
        }

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
                    case 8:
                        senderId_ = input.readInt64();
                        break;
                    case 18:
                        content_ = input.readStringRequireUtf8();
                        break;
                    case 24:
                        type_ = input.readInt32();
                        break;
                    case 34:
                        timestamp_ = input.readStringRequireUtf8();
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
