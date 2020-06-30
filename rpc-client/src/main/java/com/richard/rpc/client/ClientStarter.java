package com.richard.rpc.client;

import com.richard.rpc.common.CommonDecoder;
import com.richard.rpc.common.CommonEncoder;
import com.richard.rpc.common.bo.RpcRequest;
import com.richard.rpc.common.bo.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

public class ClientStarter {

    private final static int PORT = 9999;

    private final static String HOST = "127.0.0.1";

    public static void main(String[] args) throws Exception {
        new ClientStarter().start();
    }

    private void start() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        try {
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(HOST, PORT))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new CommonDecoder(RpcResponse.class))
                                    .addLast(new CommonEncoder(RpcRequest.class))
                                    .addLast(new ClientHandler());
                        }
                    });
            ChannelFuture future = bootstrap.connect().sync();
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }
}
