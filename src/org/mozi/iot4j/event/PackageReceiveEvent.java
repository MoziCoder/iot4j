package org.mozi.iot4j.event;

import org.mozi.iot4j.CoAPPackage;

import java.net.DatagramPacket;

/**
 * UDP包响应回调事件
 */
public interface PackageReceiveEvent {
    void onPackageReceived(DatagramPacket dp);
}

