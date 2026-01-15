package com.app.novelvoice.netty.proto;

import com.google.protobuf.Descriptors;
import com.google.protobuf.GeneratedMessageV3;

/**
 * ChatMessage Protobuf 描述符
 */
public final class ChatMessageProto {
    public static final Descriptors.Descriptor ChatMessageDescriptor;
    public static final GeneratedMessageV3.FieldAccessorTable ChatMessageFieldAccessorTable;
    private static Descriptors.FileDescriptor descriptor;

    static {
        // 更新后的描述符数据，包含所有新字段
        String[] descriptorData = {
            "\n\021ChatMessage.proto\022\036com.app.novelvoice.n" +
            "etty.proto\"\312\001\n\013ChatMessage\022\021\n\tsender_id\030\001" +
            " \001(\003\022\023\n\013receiver_id\030\002 \001(\003\022\017\n\007content\030\003 \001(\t" +
            "\022\014\n\004type\030\004 \001(\005\022\021\n\ttimestamp\030\005 \001(\t\022\020\n\010file_url" +
            "\030\006 \001(\t\022\021\n\tfile_name\030\007 \001(\t\022\021\n\tfile_size\030\010 \001(\003" +
            "\022\027\n\017sender_nickname\030\t \001(\t\022\025\n\rsender_avatar\030\n" +
            " \001(\tB2\n\036com.app.novelvoice.netty.protoB\020Chat" +
            "MessageProtoP\001b\006proto3"
        };
        descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(
            descriptorData, new Descriptors.FileDescriptor[] {});
        ChatMessageDescriptor = descriptor.getMessageTypes().get(0);
        ChatMessageFieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
            ChatMessageDescriptor,
            new String[] {
                "SenderId", "ReceiverId", "Content", "Type", "Timestamp",
                "FileUrl", "FileName", "FileSize", "SenderNickname", "SenderAvatar"
            });
    }

    public static Descriptors.FileDescriptor getDescriptor() {
        return descriptor;
    }
    
    private ChatMessageProto() {
        // 私有构造函数，防止实例化
    }
}
