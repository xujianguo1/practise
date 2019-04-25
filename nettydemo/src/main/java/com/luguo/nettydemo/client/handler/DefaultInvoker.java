package com.luguo.nettydemo.client.handler;

import com.luguo.nettydemo.model.AckMsg;
import com.luguo.nettydemo.model.RequestMsg;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class DefaultInvoker implements  Invoker{
    private long defaultTimeout = 1000;
    private static final Invoker invoker = new DefaultInvoker();
    private AtomicLong sequencer = new AtomicLong(0);//序列生成器
    private RequestMap reqMap = new RequestMap();


    private DefaultInvoker(){
        Thread timeoutChecker =  new Thread(new TimeoutChecker());
        timeoutChecker.setName("Timeout-Checker");
        timeoutChecker.start(); //启动超时检查
    }

    public static Invoker getInstance(){
        return invoker;
    }

    public void invokeAck(AckMsg msg){
        Long reqId = msg.getRequestId();
        SimpleCallback callback = this.reqMap.getCallback(reqId);
        this.reqMap.remove(reqId);
        if(callback != null){
            callback.setAckMsg(msg);
            callback.run(); //唤醒等待的线程
        }

    }
    public void invokeCallback(RequestMsg request,SimpleCallback callback){
        //NettyClient
        SimpleNettyClient client = SimpleNettyClient.getClient("local");
        request.setRequestId(sequencer.addAndGet(1));
        request.setSendTime(System.currentTimeMillis());
        if(callback != null){
            reqMap.putData(request.getRequestId(),callback);
        }
        client.write(request,callback);

    }

    private SimpleFuture invokeFuture(RequestMsg request){
        CallbackFuture callbackFuture = new CallbackFuture();
        callbackFuture.setRequestMsg(request);
        invokeCallback(request,callbackFuture);
        return callbackFuture;
    }
    public AckMsg invokeSync(RequestMsg request) throws Exception {
       SimpleFuture future = invokeFuture(request);
       return future.get(defaultTimeout);
    }

    public SimpleFuture invokeAsyc(RequestMsg requestMsg) {
        SimpleFuture future=invokeFuture(requestMsg);
        return future;
    }

    /**
     * 超时检测器
     */
    private class TimeoutChecker implements Runnable{
        public void run(){
            while(true){
                try{
                    long now = System.currentTimeMillis();
                    for(Long reqId:reqMap.requestMap.keySet()){
                        SimpleCallback callback = reqMap.getCallback(reqId);
                        if(callback.getRequestMsg().getSendTime() +defaultTimeout<now){//已经超时了
                            reqMap.remove(reqId); //删除超时的数据
                            log.warn("remove Timeout key="+reqId);
                        }
                    }
                    Thread.sleep(1000);
                }catch (Exception e){
                    log.error(e.getMessage(),e);
                }
            }
        }
    }
    private class RequestMap{
        /**
         * requestMap  key=请求Id， value = 为消息与处理
         */
        private Map<Long,SimpleCallback> requestMap =new ConcurrentHashMap<Long,SimpleCallback>();

        public SimpleCallback getCallback(Long requestId){
            return requestMap.get(requestId);
        }
        public void putData(Long requestId,SimpleCallback callback){
            requestMap.put(requestId,callback);
        }
        public void remove(Long requestId){
            requestMap.remove(requestId);
        }
    }

}
