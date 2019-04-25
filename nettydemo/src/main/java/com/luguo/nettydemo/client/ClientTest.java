package com.luguo.nettydemo.client;

import com.luguo.nettydemo.Constants;
import com.luguo.nettydemo.client.handler.DefaultInvoker;
import com.luguo.nettydemo.client.handler.SimpleFuture;
import com.luguo.nettydemo.model.AckMsg;
import com.luguo.nettydemo.model.RequestMsg;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class ClientTest {
    public static void main(String[] args){
        final long invokeTime = 1000;
        final Executor syncExecutor = Executors.newFixedThreadPool(Constants.channelPoolSize);
        Executor ayncExecutor = Executors.newFixedThreadPool(20);
        Thread syncThread = new Thread(){
            public void run(){
                long startTime = System.currentTimeMillis();
                for(int i = 0;i<invokeTime;i++){
                    ((ExecutorService) syncExecutor).submit(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                AckMsg ackMsg = sendMsgSync("send sync Msg：");
                                if(ackMsg != null){
                                    log.warn("应答消息："+ackMsg);
                                }else{
                                    log.error("应答消息返回为null");
                                }
                            }catch (Exception e){
                                log.error(e.getMessage(),e);
                            }
                        }
                    });
                }
                long endTime = System.currentTimeMillis();
                log.warn("同步基本处理完成，用时"+((endTime-startTime)/1000));
            }
        };

        Thread ayncThread = new Thread(){
            public void run(){
                long startTime = System.currentTimeMillis();
                for(int i = 0;i<invokeTime;i++){
                    ((ExecutorService) syncExecutor).submit(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                SimpleFuture future = sendMsyAsyc("send aysc Msg：");
                                log.warn("asyc 发送完毕，我要做其他事了");
                                AckMsg ackMsg=future.get(1000);
                                if(ackMsg != null){
                                    log.warn("应答消息："+ackMsg);
                                }else{
                                    log.error("应答消息返回为null");
                                }

                            }catch (Exception e){
                                e.getMessage();
                            }
                        }
                    });
                }
                long endTime = System.currentTimeMillis();
                log.warn("同步基本处理完成，用时"+((endTime-startTime)/1000));
            }
        };
        syncThread.start();
        //ayncThread.start();
    }
    public static AckMsg sendMsgSync(String content) throws Exception {
        RequestMsg msg = new RequestMsg();
        msg.setContent(content);
        return DefaultInvoker.getInstance().invokeSync(msg);
    }
    public static SimpleFuture sendMsyAsyc(String content){
        RequestMsg msg = new RequestMsg();
        msg.setContent(content);
        return DefaultInvoker.getInstance().invokeAsyc(msg);
    }

}
