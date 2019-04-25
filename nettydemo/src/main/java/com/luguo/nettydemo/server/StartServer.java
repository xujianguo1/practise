package com.luguo.nettydemo.server;

public class StartServer {
    public static  void main(String[] args){
        NettyServer server = new NettyServer();
        server.start();
    }
}
