package com.luguo.nettydemo.client.handler;

import com.luguo.nettydemo.model.RequestMsg;
import com.luguo.nettydemo.model.AckMsg;

public interface Invoker  {
    public AckMsg invokeSync(RequestMsg request) throws Exception;
    public SimpleFuture invokeAsyc(RequestMsg requestMsg);
    public void invokeAck(AckMsg ackMsg);
}
