package com.luguo.nettydemo.client.handler;

import com.luguo.nettydemo.Constants;
import com.luguo.nettydemo.code.JsonDecoder;
import com.luguo.nettydemo.code.JsonEncoder;
import com.luguo.nettydemo.model.AckMsg;
import com.luguo.nettydemo.model.RequestMsg;
import lombok.extern.slf4j.Slf4j;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SimpleNettyClient {
    private ClientBootstrap bootstrap;
    private ChannelPool channelPool;
    private static Map<String, SimpleNettyClient> clientMap= new ConcurrentHashMap<String, SimpleNettyClient>();
    public SimpleNettyClient(){
        bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory( Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool()));
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline= Channels.pipeline();
                pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(
                        Integer.MAX_VALUE, 0, 4, 0, 4));
                pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
                pipeline.addLast("jsonDecoder", new JsonDecoder(AckMsg.class));
                pipeline.addLast("jsonEncoder", new JsonEncoder(RequestMsg.class));
                pipeline.addLast("handler", new ClientReceiveHandler());
                return pipeline;
            }
        });
        channelPool = new ChannelPool(Constants.channelPoolSize);
    }

    private Channel connect(){
        ChannelFuture future = bootstrap.connect(new InetSocketAddress(Constants.host,
                Constants.port));
        // 等待连接创建成功
        if (future.awaitUninterruptibly(3000,
                TimeUnit.MILLISECONDS)) {
            if (future.isSuccess()) {
                log.info("Client is conneted to " + Constants.host + ":" + Constants.port);
            } else {
                log.warn("Client is not conneted to " + Constants.host + ":"
                        + Constants.port);
            }
        }
        return future.getChannel();
    }


    public static SimpleNettyClient getClient(String clientName){
        SimpleNettyClient client = clientMap.get(clientName);//这里可以扩展，进行负载均衡算法选择目标
        if(client==null){
            synchronized (clientMap){
                if( clientMap.get(clientName)==null){//二次检查
                    client = new SimpleNettyClient();
                    clientMap.put(clientName,client);
                    return client;
                }
                return clientMap.get(clientName);
            }

        }else{
            return client;
        }
    }
    public SimpleFuture write(RequestMsg requestMsg, SimpleCallback callback){
        Channel channel = this.channelPool.get();
        if(channel==null){
            channel = connect();
        }
        ChannelFuture future = channel.write(requestMsg);
        this.channelPool.released(channel);
//        if(requestMsg.getMsgType() ==1){
//            future.addListener(new ChannelFutureListener(){
//                public void operationComplete(ChannelFuture channelFuture) throws Exception {
//                    if(channelFuture.isSuccess()){
//                        return;
//                    }else{
//                        //可以添加 写异常的返回
//                    }
//                }
//            });
//        }
        if(callback != null){
            callback.setRequestMsg(requestMsg);
            return callback.getFuture(future);
        }
        return null;
    }


    private class ChannelPool {
        private ArrayBlockingQueue<Channel> channels;


        public ChannelPool(int poolSize) {
            this.channels = new ArrayBlockingQueue<Channel>(poolSize);
            for (int i = 0; i < poolSize; i++) {
                channels.add(connect());
            }
        }

        public Channel get(){
            try{
               return this.channels.take();
            }catch (Exception e){

            }
            return null;
        }

        /**
         * 同步获取netty channel
         */
        public void released(Channel ch) {
            channels.add(ch);
        }
    }

}
