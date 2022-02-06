package com.mozicoder.iot4j.event;

import com.mozicoder.iot4j.CoAPPackage;

/**
 * CoAP包抵达回调，如果中途出现
 */
public interface ResponseEvent{
    /**
     * 响应包接收事件
     * @param host 服务端地址
     * @param port 服务端端口
     * @param cp 包信息
     */
    void onResponse(String host, int port, CoAPPackage cp);
}
