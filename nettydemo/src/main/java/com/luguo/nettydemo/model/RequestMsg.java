package com.luguo.nettydemo.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 请求消息
 */
@NoArgsConstructor
@Data
public class RequestMsg implements Serializable {
    private Long requestId; //消息请求Id
    private Integer msgType;//消息类型
    private Long sendTime; //发送时间
    private Object content; //消息内容
}
