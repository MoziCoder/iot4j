package com.mozicoder.iot4j.event;

import java.net.DatagramPacket;

/**
 * UDP包响应回调事件
 */
public interface PackageReceiveEvent {
    void onPackageReceived(DatagramPacket dp);
}

