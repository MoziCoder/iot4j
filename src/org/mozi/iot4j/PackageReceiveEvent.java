package org.mozi.iot4j;

import java.net.DatagramPacket;

public interface PackageReceiveEvent {
    public void onPackageReceived(DatagramPacket dp);
}
