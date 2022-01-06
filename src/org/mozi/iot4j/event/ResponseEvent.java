package org.mozi.iot4j.event;

import org.mozi.iot4j.CoAPPackage;

/**
 * CoAP包抵达回调，如果中途出现
 */
public interface ResponseEvent{
    void onResponse(CoAPPackage cp);
}
