package com.luguo.nettydemo.code;

import com.alibaba.fastjson.JSONObject;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

import java.nio.charset.Charset;

public class JsonEncoder extends OneToOneEncoder {
    private Class targetClazz ;
    public JsonEncoder(Class clazz){
        super();
        this.targetClazz = clazz;
    }
    protected Object encode(ChannelHandlerContext channelHandlerContext, Channel channel, Object o) throws Exception {
        if(o.getClass() ==targetClazz){
            String json = JSONObject.toJSONString(o);
            byte[] jsonBytes = json.getBytes(Charset.forName("UTF-8"));
            ChannelBuffer channelBuffer = ChannelBuffers
                    .buffer(jsonBytes.length);
            channelBuffer.writeBytes(jsonBytes);
            return channelBuffer;
        }

        return o;
    }
}
