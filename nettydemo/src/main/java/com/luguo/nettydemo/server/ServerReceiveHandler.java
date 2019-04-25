package com.luguo.nettydemo.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.luguo.nettydemo.model.AckMsg;
import com.luguo.nettydemo.model.RequestMsg;
import lombok.extern.slf4j.Slf4j;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
public class ServerReceiveHandler extends SimpleChannelHandler {
    private static  Executor executor = Executors.newCachedThreadPool();
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        final RequestMsg req = (RequestMsg) e.getMessage();
        log.warn("receiveMsg:"+JSON.toJSONString(req));
        AckMsg ackMsg = new AckMsg();
        ackMsg.setRequestId(req.getRequestId());
        ackMsg.setRespContent("我已经收到消息，入库了，你的消息是：id="+req.getRequestId()+",content="+req.getContent());

        //3秒内的随机数，反应服务端处理时间，同时监控客户端的超时反应
        long sleepTime = new Random().nextInt(400);
        log.warn("待发送的消息："+JSON.toJSONString(ackMsg)+",准备睡眠时间："+sleepTime);
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        e.getChannel().write(ackMsg);
    }
}
