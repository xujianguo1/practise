package com.luguo.nettydemo.client.handler;

import com.luguo.nettydemo.model.AckMsg;
import com.luguo.nettydemo.model.RequestMsg;
import lombok.extern.slf4j.Slf4j;
import org.jboss.netty.channel.ChannelFuture;

@Slf4j
public class CallbackFuture implements SimpleCallback,SimpleFuture {
    private RequestMsg reqMsg;
    private AckMsg ackMsg;
    private ChannelFuture future;
    private boolean isDone = false;
    public synchronized void run() { //回调方法被执行，表名已经完成了
        isDone = true;
        this.notifyAll();
    }
    public void setRequestMsg(RequestMsg msg){
        this.reqMsg = msg;
    }
    public RequestMsg getRequestMsg(){
        return reqMsg;
    }

    public void setAckMsg(AckMsg ack) {
        this.ackMsg = ack;
    }

    public SimpleFuture getFuture(ChannelFuture future) {
        this.future = future;
        return this;
    }

    public synchronized  AckMsg get(long timeout) throws InterruptedException{

         long sendTime = this.reqMsg.getSendTime();
         while(!isDone){
             long leftTime = timeout -(System.currentTimeMillis()-sendTime);
             if(leftTime <0){//抛出一个超时
                 throw new RuntimeException("Request timeout ! seqId:"+reqMsg.getRequestId());
             }else{
                 log.info(this.reqMsg.getRequestId()+"需要睡眠时间："+leftTime);
                 this.wait(leftTime);
             }
         }
        return ackMsg;
    }

    public boolean isDone() {
        return false;
    }


}
