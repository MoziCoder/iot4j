package com.mozicoder.iot4j.event;

import com.mozicoder.iot4j.CoAPPackage;

/**
 * CoAP包传输事件，发送和接收通用
 */
public interface MessageTransmitEvent {
    /**
     * @param host 服务端地址
     * @param port 服务端端口
     * @param cp 包信息
     */
    void onTransmit(String host, int port, CoAPPackage cp);
}
