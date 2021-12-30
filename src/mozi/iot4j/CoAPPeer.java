package mozi.iot4j;

/**
 * CoAP通讯端
 * @author Jason
 * @date 2021/12/19
 */
public class CoAPPeer {

    protected  UDPSocket _socket;

    protected int BindPort = CoAPProtocol.Port;

    protected List<CoAPCode> SupportedRequest = new List<CoAPCode> { CoAPRequestMethod.Get, CoAPRequestMethod.Post, CoAPRequestMethod.Put, CoAPRequestMethod.Delete };

    /// <summary>
    /// 服务端口
    /// </summary>
    public int Port { get { return BindPort; } protected set { BindPort = value; } }
    /// <summary>
    /// 启动时间
    /// </summary>
    public DateTime StartTime { get; private set; }

    public CoAPPeer()
    {
        _socket = new UDPSocket();
        _socket.AfterReceiveEnd += Socket_AfterReceiveEnd;
    }
    /// <summary>
    /// 以指定端口启动<see cref="F:Port"/>，如果不配置端口则使用默认端口
    /// </summary>
    public void Start()
    {
        Start(BindPort);
    }
    /// <summary>
    /// 启动本端服务
    /// </summary>
    /// <param name="port"></param>
    public void Start(int port)
    {
        BindPort = port;
        _socket.Start(BindPort);
        StartTime = DateTime.Now;
    }
    /// <summary>
    /// 端口下线
    /// </summary>
    public void Shutdown()
    {
        _socket.Shutdown();
        StartTime = DateTime.MinValue;
    }
    /// <summary>
    /// 数据接收完成回调
    /// </summary>
    /// <param name="sender"></param>
    /// <param name="args"></param>
    protected virtual void Socket_AfterReceiveEnd(object sender, DataTransferArgs args)
    {
        //CoAPPackage pack2=null;

        ////try
        ////{
        //    CoAPPackage pack = CoAPPackage.Parse(args.Data,true);

        //    pack2 = new CoAPPackage()
        //    {
        //        Version = 1,
        //        MessageType = CoAPMessageType.Acknowledgement,
        //        Token = pack.Token,
        //        MesssageId = pack.MesssageId,
        //    };

        //    //判断是否受支持的方法
        //    if (IsSupportedRequest(pack))
        //    {
        //        if (pack.MessageType==CoAPMessageType.Confirmable||pack.MessageType == CoAPMessageType.Acknowledgement)
        //        {
        //            pack2.Code = CoAPResponseCode.Content;
        //        }
        //    }
        //    else
        //    {
        //        pack2.Code = CoAPResponseCode.MethodNotAllowed;
        //    }

        //    //检查分块

        //    //检查内容类型

        ////}
        ////catch (Exception ex)
        ////{
        ////    Console.WriteLine(ex.Message);
        ////}
        ////finally
        ////{
        //    if (pack2 != null)
        //    {
        //        _socket.SendTo(pack2.Pack(), args.IP, args.Port);
        //    }
        ////}

    }
    /// <summary>
    /// 是否受支持的请求方法<see cref="CoAPRequestMethod"/>
    /// </summary>
    /// <param name="pack"></param>
    /// <returns></returns>
    protected bool IsSupportedRequest(CoAPPackage pack)
    {
        return SupportedRequest.Contains(pack.Code);
    }
}
