package com.app.novelvoice.netty;

import com.app.novelvoice.entity.Message;
import com.app.novelvoice.netty.proto.ChatMessage;
import com.app.novelvoice.service.ChatService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@ChannelHandler.Sharable
public class ChatHandler extends SimpleChannelInboundHandler<ChatMessage> {

    private static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Autowired
    private ChatService chatService;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        channels.add(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        channels.remove(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatMessage protoMsg) throws Exception {
        // Convert Proto to Entity for DB persistence
        Message msg = new Message();
        msg.setSenderId(protoMsg.getSenderId());
        msg.setContent(protoMsg.getContent());
        msg.setType(protoMsg.getType());
        msg.setCreateTime(new Date());

        // Persist to DB
        chatService.saveMessage(msg);

        // Broadcast to all connected clients
        for (Channel channel : channels) {
            channel.writeAndFlush(protoMsg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
