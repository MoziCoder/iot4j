package org.mozi.iot4j;

import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * CoAP通讯端
 * @author Jason
 * @date 2021/12/19
 */
public class CoAPPeer implements PackageReceiveEvent{

    protected  UDPSocket _socket;

    protected int BindPort = CoAPProtocol.Port;

    protected ArrayList<CoAPCode> SupportedRequest = new ArrayList<CoAPCode>() ;

    /**
     * 启动时间
     */
    public Date _startTime;

    public int getBindPort() {
        return BindPort;
    }

    public void setBindPort(int port){
        BindPort=port;
    }

    public Date getStartTime(){
        return _startTime;
    }

    protected void setStartTime(Date dt){
        _startTime=dt;
    }

    public CoAPPeer()
    {

        SupportedRequest.add(CoAPRequestMethod.Get);
        SupportedRequest.add(CoAPRequestMethod.Post);
        SupportedRequest.add(CoAPRequestMethod.Put);
        SupportedRequest.add(CoAPRequestMethod.Delete );

        _socket = new UDPSocket();
    }

    /**
     * 以指定端口启动{@link #BindPort}，如果不配置端口则使用默认端口
     * @see #start(int port)
     */
    public void start()
    {
        start(BindPort);
    }
    /// <summary>
    /// 启动本端服务
    /// </summary>
    /// <param name="port"></param>
    public void start(int port)
    {
        BindPort = port;
        _socket.start(BindPort);
        _socket.setOnPackageReceiveListener(this);
        _startTime = Calendar.getInstance(TimeZone.getDefault()).getTime();
    }
    /// <summary>
    /// 端口下线
    /// </summary>
    public void shutdown()
    {
        _socket.shutdown();
        _startTime = null;
        _socket.setOnPackageReceiveListener(null);
    }

//    /// <summary>
//    /// 数据接收完成回调
//    /// </summary>
//    /// <param name="sender"></param>
//    /// <param name="args"></param>
//    protected virtual void Socket_AfterReceiveEnd(object sender, DataTransferArgs args)
//    {
//        //CoAPPackage pack2=null;
//
//        ////try
//        ////{
//        //    CoAPPackage pack = CoAPPackage.Parse(args.Data,true);
//
//        //    pack2 = new CoAPPackage()
//        //    {
//        //        Version = 1,
//        //        MessageType = CoAPMessageType.Acknowledgement,
//        //        Token = pack.Token,
//        //        MesssageId = pack.MesssageId,
//        //    };
//
//        //    //判断是否受支持的方法
//        //    if (IsSupportedRequest(pack))
//        //    {
//        //        if (pack.MessageType==CoAPMessageType.Confirmable||pack.MessageType == CoAPMessageType.Acknowledgement)
//        //        {
//        //            pack2.Code = CoAPResponseCode.Content;
//        //        }
//        //    }
//        //    else
//        //    {
//        //        pack2.Code = CoAPResponseCode.MethodNotAllowed;
//        //    }
//
//        //    //检查分块
//
//        //    //检查内容类型
//
//        ////}
//        ////catch (Exception ex)
//        ////{
//        ////    Console.WriteLine(ex.Message);
//        ////}
//        ////finally
//        ////{
//        //    if (pack2 != null)
//        //    {
//        //        _socket.SendTo(pack2.Pack(), args.IP, args.Port);
//        //    }
//        ////}
//
//    }

    /// <summary>
    /// 是否受支持的请求方法<see cref="CoAPRequestMethod"/>
    /// </summary>
    /// <param name="pack"></param>
    /// <returns></returns>
    protected boolean isSupportedRequest(CoAPPackage pack)
    {
        return SupportedRequest.contains(pack.getCode());
    }

    /**
     * 接收数据回调
     * @param dp
     */
    @Override
    public void onPackageReceived(DatagramPacket dp) {
        CoAPPackage cp=CoAPPackage.parse(dp.getData(),CoAPPackageType.Request);
        System.out.println(cp.getCode().getDescription());
    }
}