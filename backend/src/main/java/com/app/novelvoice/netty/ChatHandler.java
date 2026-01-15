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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 聊天消息处理器
 * 处理 WebSocket 消息的接收和转发
 */
@Component
@ChannelHandler.Sharable
public class ChatHandler extends SimpleChannelInboundHandler<ChatMessage> {

    /**
     * 所有连接的 Channel 组（用于群聊广播）
     */
    private static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    
    /**
     * 用户ID -> Channel 映射（用于私聊定向发送）
     */
    private static final Map<Long, Channel> userChannelMap = new ConcurrentHashMap<>();

    @Autowired
    private ChatService chatService;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        channels.add(ctx.channel());
        System.out.println("新用户连接: " + ctx.channel().id().asShortText());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        channels.remove(ctx.channel());
        
        // 从映射中移除
        userChannelMap.entrySet().removeIf(entry -> entry.getValue().equals(ctx.channel()));
        
        System.out.println("用户断开连接: " + ctx.channel().id().asShortText());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatMessage protoMsg) throws Exception {
        long senderId = protoMsg.getSenderId();
        long receiverId = protoMsg.getReceiverId();
        
        // 绑定用户ID和Channel
        if (senderId > 0 && !userChannelMap.containsKey(senderId)) {
            userChannelMap.put(senderId, ctx.channel());
        }
        
        // 转换 Proto 消息为实体进行持久化
        Message msg = new Message();
        msg.setSenderId(senderId);
        msg.setReceiverId(receiverId > 0 ? receiverId : null);
        msg.setContent(protoMsg.getContent());
        msg.setType(protoMsg.getType());
        msg.setFileUrl(protoMsg.getFileUrl());
        msg.setFileName(protoMsg.getFileName());
        msg.setFileSize(protoMsg.getFileSize());
        msg.setIsRead(false);
        msg.setCreateTime(new Date());

        // 持久化消息到数据库
        chatService.saveMessage(msg);

        // 根据消息类型转发
        if (receiverId > 0) {
            // 私聊消息：发送给发送者和接收者
            sendToUser(senderId, protoMsg);
            sendToUser(receiverId, protoMsg);
        } else {
            // 群聊消息：广播给所有连接的客户端
            for (Channel channel : channels) {
                channel.writeAndFlush(protoMsg);
            }
        }
    }
    
    /**
     * 向指定用户发送消息
     */
    private void sendToUser(long userId, ChatMessage message) {
        Channel channel = userChannelMap.get(userId);
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(message);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.err.println("WebSocket 异常: " + cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }
    
    /**
     * 获取在线用户数量
     */
    public static int getOnlineCount() {
        return channels.size();
    }
    
    /**
     * 检查用户是否在线
     */
    public static boolean isUserOnline(long userId) {
        Channel channel = userChannelMap.get(userId);
        return channel != null && channel.isActive();
    }
}
