package com.luguo.nettydemo.client.handler;

import com.luguo.nettydemo.model.AckMsg;

public interface SimpleFuture {
    public AckMsg get(long timeout)throws InterruptedException ;
    public boolean isDone();
}
