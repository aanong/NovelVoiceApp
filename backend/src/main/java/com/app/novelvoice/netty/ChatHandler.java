package com.app.novelvoice.netty;

import com.app.novelvoice.entity.Message;
import com.app.novelvoice.service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
@ChannelHandler.Sharable
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Autowired
    private ChatService chatService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        channels.add(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        channels.remove(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame frame) throws Exception {
        String text = frame.text();
        // Assume sending JSON: {"senderId": 1, "content": "hello"}

        try {
            Map<String, Object> map = objectMapper.readValue(text, Map.class);
            Long senderId = Long.valueOf(map.get("senderId").toString());
            String content = (String) map.get("content");

            Message msg = new Message();
            msg.setSenderId(senderId);
            msg.setContent(content);
            msg.setType(0); // Text
            msg.setCreatedAt(new Date());

            // Persist to DB
            chatService.saveMessage(msg);

            // Broadcast
            TextWebSocketFrame outFrame = new TextWebSocketFrame(objectMapper.writeValueAsString(msg));
            for (Channel channel : channels) {
                channel.writeAndFlush(outFrame.retain()); // retain for multiple writes
            }
        } catch (Exception e) {
            e.printStackTrace();
            ctx.channel().writeAndFlush(new TextWebSocketFrame("Error parsing message: " + e.getMessage()));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
