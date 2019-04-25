package com.luguo.nettydemo.code;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

import java.nio.charset.Charset;
@Slf4j
public class JsonDecoder extends OneToOneDecoder {
    private Class targetClazz;
    public JsonDecoder(Class targetClass){
        this.targetClazz = targetClass;
    }
    protected Object decode(ChannelHandlerContext channelHandlerContext, Channel channel, Object o) throws Exception {
        if(!(o instanceof ChannelBuffer)){
            return o;
        }
        ChannelBuffer buffer = (ChannelBuffer)o;
        String jsonStr = buffer.toString(Charset.forName("utf-8"));
        try {
           return  JSONObject.parseObject(jsonStr, targetClazz);
        }catch(Exception e){
            log.error("无法解析的消息："+jsonStr);
            throw e;
        }
    }
}
