package mozi.iot4j;

import java.io.IOException;
import java.net.*;

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

    public void start(){

        try {
            _sc= new DatagramSocket(_port);
            _sc.setReuseAddress(true);
            _sc.setSoTimeout(32);
            SocketAddress sa=new SocketAddress() {
                @Override
                public int hashCode() {
                    return super.hashCode();
                }
            };

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void send(String host,int port,byte[] data){
        DatagramPacket dp=new DatagramPacket(data,data.length,new InetSocketAddress(host,port));
        try {
            _sc.send(dp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
