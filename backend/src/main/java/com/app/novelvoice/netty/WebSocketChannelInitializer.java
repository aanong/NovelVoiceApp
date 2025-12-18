package com.app.novelvoice.netty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

@Component
public class WebSocketChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Autowired
    private ChatHandler chatHandler;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast("http-codec", new HttpServerCodec());
        ch.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));
        ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
        ch.pipeline().addLast("websocket-handler", new WebSocketServerProtocolHandler("/ws", null, true, 65536));
        ch.pipeline().addLast("proto-codec", new WebSocketProtobufCodec());
        ch.pipeline().addLast("handler", chatHandler);
    }
}
