package com.app.novelvoice.netty;

import com.app.novelvoice.netty.proto.ChatMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.List;

public class WebSocketProtobufCodec extends MessageToMessageCodec<BinaryWebSocketFrame, ChatMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ChatMessage msg, List<Object> out) throws Exception {
        byte[] bytes = msg.toByteArray();
        ByteBuf buf = Unpooled.wrappedBuffer(bytes);
        out.add(new BinaryWebSocketFrame(buf));
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, BinaryWebSocketFrame frame, List<Object> out) throws Exception {
        ByteBuf content = frame.content();
        byte[] bytes = new byte[content.readableBytes()];
        content.readBytes(bytes);
        ChatMessage chatMessage = ChatMessage.parseFrom(bytes);
        out.add(chatMessage);
    }
}
