package com.richard.rpc.core;

import com.richard.rpc.common.CommonDecoder;
import com.richard.rpc.common.CommonEncoder;
import com.richard.rpc.common.bo.RpcRequest;
import com.richard.rpc.common.bo.RpcResponse;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public class RpcServer {

    public static void main(String[] args) throws InterruptedException {
        new RpcServer().start();
    }

    private void start() throws InterruptedException {
        final RpcHandler handler = new RpcHandler();
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            int port = 9999;
            bootstrap.group(group)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new CommonDecoder(RpcResponse.class))
                                    .addLast(new CommonEncoder(RpcRequest.class))
                                    .addLast(handler);
                        }
                    });
            ChannelFuture future = bootstrap.bind().sync();
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }
}
