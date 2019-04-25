package com.luguo.nettydemo.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 应答 or 返回 消息
 */
@NoArgsConstructor
@Data
public class AckMsg  implements Serializable {
    private Long requestId; //消息请求Id
    private Integer msgType;//消息类型
    private Object respContent; //返回内容
}
