package com.app.novelvoice.netty.proto;

import com.google.protobuf.Descriptors;
import com.google.protobuf.GeneratedMessageV3;

public final class ChatMessageProto {
        public static final Descriptors.Descriptor ChatMessageDescriptor;
        public static final GeneratedMessageV3.FieldAccessorTable ChatMessageFieldAccessorTable;
        private static Descriptors.FileDescriptor descriptor;

        static {
                String[] descriptorData = {
                                "\n\021ChatMessage.proto\022\036com.app.novelvoice.n" +
                                                "etty.proto\"U\n\013ChatMessage\022\021\n\tsender_id\030\001" +
                                                " \001(\003\022\017\n\007content\030\002 \001(\t\022\014\n\004type\030\003 \001(\005\022\021\n\tt"
                                                +
                                                "imestamp\030\004 \001(\tB2\n\036com.app.novelvoice.net" +
                                                "ty.protoB\020ChatMessageProtoP\001b\006proto3"
                };
                descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(
                                descriptorData, new Descriptors.FileDescriptor[] {});
                ChatMessageDescriptor = descriptor.getMessageTypes().get(0);
                ChatMessageFieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(
                                ChatMessageDescriptor, new String[] { "SenderId", "Content", "Type", "Timestamp", });
        }

        public static Descriptors.FileDescriptor getDescriptor() {
                return descriptor;
        }
}
