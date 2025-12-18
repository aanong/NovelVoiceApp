package com.app.novelvoice.netty.proto;

import com.google.protobuf.MessageOrBuilder;

public interface ChatMessageOrBuilder extends MessageOrBuilder {
    long getSenderId();

    String getContent();

    com.google.protobuf.ByteString getContentBytes();

    int getType();

    String getTimestamp();

    com.google.protobuf.ByteString getTimestampBytes();
}
