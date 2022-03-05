package com.mozicoder.iot4j;

import com.mozicoder.iot4j.event.PackageReceiveEvent;

import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * CoAP对等端
 * @author Jason
 * @date 2021/12/19
 * <p>
 *      UDP使用对等模式工作，客户机和服务器地位对等，且CoAP协议定义的客户机和服务器也是对等关系，角色可以随时互换。
 *      服务端一般承载较大的并发压力和更复杂的业务逻辑，同时需要更强的算力。客户机则多用于信息采集，数据上报，资料下载等轻量型计算。
 *      基于上述原因，还是对从协议实现上对客户机和服务器进行角色区分。
 *      CoAP基于UDP,可工作的C/S模式，多播，单播，任播（IPV6）
 * </p>
 *  <p>
 * C/S模式
 *      URI格式:
 *           coap://{host}:{port}/{path}[?{query}]
 *      默认端口号为5683
 *           coaps://{host}:{port}/{path}[?{query}]
 *      默认端口号为5684
 *  </p>
 *  <p>
 * 多播模式:
 *      IPV4:224.0.1.187
 *      IPV6:FF0X::FD
 *  </p>
 *  <p>
 *  消息重传
 *  When SendTimeOut between {ACK_TIMEOUT} and (ACK_TIMEOUT * ACK_RANDOM_FACTOR)  then
 *      TryCount=0
 *  When TryCount <{MAX_RETRANSMIT} then
 *      TryCount++
 *      SendTime*=2
 *  When TryCount >{MAX_RETRANSMIT} then
 *      Send(Rest)
 *  </p>
 */
public class CoAPPeer implements PackageReceiveEvent {

    private int _blockSize = 128;

    protected  UDPSocket _socket;

    protected int BindPort = CoAPProtocol.Port;

    protected ArrayList<CoAPCode> SupportedRequest = new ArrayList<CoAPCode>() ;

    private PackageReceiveEvent _packReceiveEvent;
    /*
    * 最小分块大小
    * /
    public static final int MinBlockSize = 16;
    /*
    * 最大分块大小
    */
    public static final int MaxBlockSize = 2048;
    /**
     * 当前端默认采用块大小,默认值为128bytes,单位Byte
     * 在通讯两方没有进行协商的情况下，默认采用此值作为分块大小。取值区间为{@MinBlockSize}~{@MaxBlockSize"}</remarks>
     */
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
    public int getBlockSize(){
        return _blockSize;
    }
    public void setBlockSize(int size){
        _blockSize=size;
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
    /**
     *  启动本端服务
     *  @param port 本地端口
     */
    public void start(int port)
    {
        BindPort = port;
        _socket.start(BindPort);
        _socket.setOnPackageReceiveListener(this);
        _startTime = Calendar.getInstance(TimeZone.getDefault()).getTime();
    }
    /**
    * 端口下线
    */
    public void shutdown()
    {
        _socket.shutdown();
        _startTime = null;
        _socket.setOnPackageReceiveListener(null);
    }

    //TODO 此处会接管所有的数据包处理，故而将方法改为私有
    /**
     * 设置包接收侦听器
     * @param receiveEvent
     */
    private void setOnPackageReceiveListener(PackageReceiveEvent receiveEvent){
        if(receiveEvent!=null){
            _packReceiveEvent=receiveEvent;
            _socket.setOnPackageReceiveListener(_packReceiveEvent);
        }
    }
    /**
     * 发送请求消息,此方法为高级方法。
     * 如果对协议不够了解，请不要调用。
     * 此方法不会调用DNS解析域名，Domain地址请先转换为IP地址，然后将Domain填充到“Uri-Host”选项中
     *  @param host 服务器地址IPV4/IPV6
     *  @param port 服务器端口
     *  @param pack 数据报文
     * @returns MessageId
     */
    public  char sendMessage(String host, int port, CoAPPackage pack) {
        _socket.sendTo(pack.pack(), host, port);
        return pack.getMesssageId();
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

    /**
     * 是否受支持的请求方法
     * @param pack
     * @see CoAPRequestMethod
     */
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
