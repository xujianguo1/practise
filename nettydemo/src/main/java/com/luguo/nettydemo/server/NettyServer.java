package com.luguo.nettydemo.server;

import com.luguo.nettydemo.Constants;
import com.luguo.nettydemo.code.JsonDecoder;
import com.luguo.nettydemo.code.JsonEncoder;
import com.luguo.nettydemo.model.AckMsg;
import com.luguo.nettydemo.model.RequestMsg;
import lombok.extern.slf4j.Slf4j;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
@Slf4j
public class NettyServer {
    private ServerBootstrap bootstrap;
    public NettyServer(){
        bootstrap = new ServerBootstrap(
                new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline= Channels.pipeline();
                pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(
                        Integer.MAX_VALUE, 0, 4, 0, 4));
                pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
                //服务端的json编码解码与客户端相反
                pipeline.addLast("jsonDecoder", new JsonDecoder(RequestMsg.class));
                pipeline.addLast("jsonEncoder", new JsonEncoder(AckMsg.class));
                pipeline.addLast("handler", new ServerReceiveHandler());
                return pipeline;
            }
        });
    }

    public void start(){
        log.info("准备绑定端口："+Constants.port);
        bootstrap.bind(new InetSocketAddress(Constants.port));
        log.info("绑定端口成功");
    }
}
