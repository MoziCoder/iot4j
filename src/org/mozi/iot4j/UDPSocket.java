package org.mozi.iot4j;

import org.mozi.iot4j.event.PackageReceiveEvent;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.*;

/**
 * Udp通讯组件
 * @author Jason
 * @date 2021/12/29
 */
public class UDPSocket implements Thread.UncaughtExceptionHandler {

    private int _port;

    private DatagramSocket _sc;
    private boolean _observe=false;
    private PackageReceiveEvent _receiveEvent;

    private Thread thread;

    public UDPSocket(){

    }

    /**
     * 设置本地绑定端口
     * @param port
     */
    public void setPort(int port){
        _port=port;
    }

    /**
     * 启动 实例化 DatagramSocket
     * @param port
     */
    public void start(int port){

        _port=port;
        try {
            if(_sc==null) {
                _sc = new DatagramSocket(_port);
                _sc.setReuseAddress(true);
                _sc.setSoTimeout(32);
                _observe=true;
                observeMessageReceive();
                SocketAddress sa=new SocketAddress() {
                    @Override
                    public int hashCode() {
                        return super.hashCode();
                    }
                };
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * 包接收回调事件
     * @param receiveEvent
     */
    public void setOnPackageReceiveListener(PackageReceiveEvent receiveEvent){
        _receiveEvent=receiveEvent;
    }

    /**
     * 线程侦听数据接收
     */
    public void observeMessageReceive(){
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (_sc!=null&&(!_sc.isClosed())&&_observe){
                    byte[] buffer=new byte[1024];
                    DatagramPacket dp=new DatagramPacket(buffer,buffer.length);
                    try {
                        try {
                            _sc.receive(dp);
                            if (_receiveEvent != null) {
                                _receiveEvent.onPackageReceived(dp);
                            }

                        }catch (InterruptedIOException ex){
                            System.out.println("Peer doesn't receive any datagram after the timeout(3s) has expired");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.setUncaughtExceptionHandler(this);
        thread.start();
    }

    /**
     * 发送数据到指定的终结点
     * @param data
     * @param host
     * @param port
     */
    public void sendTo(byte[] data,String host,int port){
        DatagramPacket dp=new DatagramPacket(data,data.length,new InetSocketAddress(host,port));
        try {
            _sc.send(dp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭套接字
     */
    public void shutdown() {
        _observe=false;
        if(thread!=null&&thread.isAlive()){
            try {
                thread.interrupt();
            }catch (Exception ex){

            }
        }
        if(!_sc.isClosed()){
            _sc.close();
        }
    }

    /**
     * 线程未捕获异常回调
     * @param t
     * @param e
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        System.out.println("Observe thread occurs an uncaught exception."+e.getMessage());
    }
}
