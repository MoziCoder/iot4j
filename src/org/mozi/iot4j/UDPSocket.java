package org.mozi.iot4j;

import javax.security.auth.callback.Callback;
import java.io.IOException;
import java.net.*;
import java.util.Date;

/**
 * Udp通讯组件
 * @author Jason
 * @date 2021/12/29
 */
public class UDPSocket {

    private int _port;

    private DatagramSocket _sc;

    public UDPSocket(){

    }

    public void setPort(int port){
        _port=port;
    }

    public void start(int port){

        _port=port;
        try {
            if(_sc==null) {
                _sc = new DatagramSocket(_port);
                _sc.setReuseAddress(true);
                _sc.setSoTimeout(32);
//            SocketAddress sa=new SocketAddress() {
//                @Override
//                public int hashCode() {
//                    return super.hashCode();
//                }
//            };
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    public void observeMessageReceive(Callback cb){
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                while (_sc!=null){
                    byte[] buffer=new byte[1024];
                    DatagramPacket dp=new DatagramPacket(buffer,buffer.length);
                    try {
                        _sc.receive(dp);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    public void sendTo(byte[] data,String host,int port){
        DatagramPacket dp=new DatagramPacket(data,data.length,new InetSocketAddress(host,port));
        try {
            _sc.send(dp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        if(!_sc.isClosed()){
            _sc.close();
        }
    }
}
