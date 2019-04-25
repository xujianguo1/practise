package com.luguo.nettydemo.client.handler;

import com.luguo.nettydemo.model.AckMsg;
import com.luguo.nettydemo.model.RequestMsg;
import org.jboss.netty.channel.ChannelFuture;

public interface SimpleCallback extends Runnable {
    public void setRequestMsg(RequestMsg msg);
    public RequestMsg getRequestMsg();

    public void setAckMsg(AckMsg ack);

    public SimpleFuture getFuture(ChannelFuture future);
}
