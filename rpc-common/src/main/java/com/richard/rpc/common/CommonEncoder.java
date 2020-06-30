package com.richard.rpc.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class CommonEncoder extends MessageToByteEncoder<Object> {

    private Class<?> targetClass;

    public CommonEncoder(Class<?> targetClass) {
        this.targetClass = targetClass;
    }

    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if (targetClass.isInstance(msg)) {
            byte[] data = SerializationUtil.serialize(msg);
            out.writeInt(data.length);
            out.writeBytes(data);
        }
    }
}
