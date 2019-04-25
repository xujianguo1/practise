package com.luguo.nettydemo.client.handler;

import com.luguo.nettydemo.model.AckMsg;
import lombok.extern.slf4j.Slf4j;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
public class ClientReceiveHandler extends SimpleChannelHandler {
    private static  Executor executor = Executors.newCachedThreadPool();
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        final AckMsg ackMsg = (AckMsg) e.getMessage();
        try {
            this.executor.execute(new Runnable() {
                @Override
                public void run() {
                    DefaultInvoker.getInstance().invokeAck(ackMsg);
                }
            });
        } catch (Exception ex) {
            String msg = "ack callback execute fail \r\n";
            log.error(msg + ex.getMessage(), ex);
        }
    }
}
