package org.mozi.iot4j;

import org.mozi.iot4j.utils.StringUtil;
import org.mozi.iot4j.utils.Uint32;
import org.mozi.iot4j.utils.UriInfo;

/**
 * CoAP对等端
 * @author Jason
 * @date 2021-12-19
 *
 * UDP使用对等模式工作，客户机和服务器地位对等，且CoAP协议定义的客户机和服务器也是对等关系，角色可以随时互换。
 * 服务端一般承载较大的并发压力和更复杂的业务逻辑，同时需要更强的算力。客户机则多用于信息采集，数据上报，资料下载等轻量型计算。
 * 基于上述原因，还是对从协议实现上对客户机和服务器进行角色区分。
 * CoAP基于UDP,可工作的C/S模式，多播，单播，任播（IPV6）
 *
 * <p>
 * C/S模式
 * URI格式:
 *      coap://{host}:{port}/{path}[?{query}]
 * 默认端口号为5683
 *      coaps://{host}:{port}/{path}[?{query}]
 * 默认端口号为5684
 * <p>
 * 多播模式:
 *      IPV4:224.0.1.187
 *      IPV6:FF0X::FD
 * <p>
 * 消息重传
 *      When SendTimeOut between {ACK_TIMEOUT} and (ACK_TIMEOUT * ACK_RANDOM_FACTOR)  then
 *           TryCount=0
 *      When TryCount <{MAX_RETRANSMIT} then
 *           TryCount++
 *           SendTime*=2
 *      When TryCount >{MAX_RETRANSMIT} then
 *           Send(Rest)
 * <p>
 */

//TODO 即时响应ACK，延迟响应CON,消息可即时响应也可处理完成后响应，延迟消息需要后端缓存支撑
//TODO 拥塞算法
//TODO 安全认证
//TODO 消息缓存
//TODO 分块传输 RFC 7959
//TODO 对象安全

public class CoAPClient extends CoAPPeer{

    private boolean _randomPort = true;

    private CoAPTransmissionConfig _transConfig = new CoAPTransmissionConfig();

    //private MessageCacheManager _cacheManager;

    //private char _remotePort = CoAPProtocol.Port;
    //private string _remotehost = "";

    ///// <summary>
    ///// 远端服务器地址
    ///// </summary>
    //public string RemoteAddress { get { return _remotehost; } protected set { _remotehost = value; } }

    ///// <summary>
    ///// 远端服务器端口
    ///// </summary>
    //public char RemotePort { get { return _remotePort; } protected set { _remotePort = value; } }

    public CoAPClient()
    {
        //_cacheManager = new MessageCacheManager(this);
        //配置本地服务口地址
    }
    /// <summary>
    /// 设置本地端口，默认为<see cref=" CoAPProtocol.Port"/>,如果不设置则使用随机端口
    /// </summary>
    /// <param name="port"></param>
    /// <returns></returns>
    public CoAPClient setPort(int port)
    {
        BindPort = port;
        _randomPort = false;
        return this;
    }
//    /// <summary>
//    /// 数据接收完成回调
//    /// </summary>
//    /// <param name="sender"></param>
//    /// <param name="args"></param>
//    protected override void Socket_AfterReceiveEnd(object sender, DataTransferArgs args)
//    {
//        CoAPPackage pack2 = null;
//
//        //try
//        //{
//        CoAPPackage pack = CoAPPackage.Parse(args.Data, false);
//
//        //pack2 = new CoAPPackage()
//        //{
//        //    Version = 1,
//        //    MessageType = CoAPMessageType.Acknowledgement,
//        //    Token = pack.Token,
//        //    MesssageId = pack.MesssageId,
//        //};
//
//        ////判断是否受支持的方法
//        //if (IsSupportedRequest(pack))
//        //{
//        //    if (pack.MessageType == CoAPMessageType.Confirmable || pack.MessageType == CoAPMessageType.Acknowledgement)
//        //    {
//        //        pack2.Code = CoAPResponseCode.Content;
//        //    }
//        //}
//        //else
//        //{
//        //    pack2.Code = CoAPResponseCode.MethodNotAllowed;
//        //}
//
//        ////检查分块
//
//        ////检查内容类型
//
//        ////}
//        ////catch (Exception ex)
//        ////{
//        ////    Console.WriteLine(ex.Message);
//        ////}
//        ////finally
//        ////{
//        //if (pack2 != null)
//        //{
//        //    _socket.SendTo(pack2.Pack(), args.IP, args.Port);
//        //}
//        ////}
//    }
    /// <summary>
    /// 发送请求消息,此方法为高级方法。如果对协议不够了解，请不要调用。
    /// DOMAIN地址请先转换为IP地址，然后填充到Uri-Host选项中
    /// </summary>
    /// <param name="pack"></param>
    /// <returns>MessageId</returns>
    public  char sendMessage(String host, int port, CoAPPackage pack)
    {
//        if (pack.getMesssageId() == 0)
//        {
//            pack.MesssageId = _cacheManager.GenerateMessageId();
//        }
        _socket.sendTo(pack.Pack(), host, port);
        return pack.getMesssageId();
    }

    /// <summary>
    /// 注入URL相关参数,domain,port,paths,queries
    /// </summary>
    /// <param name="uri"></param>
    /// <param name="cp"></param>
    private void PackUrl(UriInfo uri, CoAPPackage cp)
    {
        //注入域名
        if (StringUtil.isNullOrEmpty(uri.Domain))
        {
            cp.SetOption(CoAPOptionDefine.UriHost, uri.Domain);
        }
        //注入端口号
        if (uri.Port > 0 && (uri.Port != CoAPProtocol.Port || uri.Port != CoAPProtocol.SecurePort))
        {
            cp.SetOption(CoAPOptionDefine.UriPort, new Uint32(uri.Port));
        }
        //注入路径
        for (int i = 0; i < uri.Paths.length; i++)
        {
            cp.SetOption(CoAPOptionDefine.UriPath, uri.Paths[i]);
        }
        //注入查询参数
        for (int i = 0; i < uri.Queries.length; i++)
        {
            cp.SetOption(CoAPOptionDefine.UriQuery, uri.Queries[i]);
        }
    }

    ///<summary>
    /// Get提交 填入指定格式的URI，如果是域名，程序会调用DNS进行解析
    /// </summary>
    /// <param name="url">
    ///     <list type="table">
    ///         <listheader>URI格式:{host}-IPV4地址,IPV6地址,Domain域名;{path}-路径,请使用REST样式路径;{query}为查询参数字符串</listheader>
    ///         <item><term>格式1：</term>coap://{host}[:{port}]/{path}</item>
    ///         <item><term>格式2：</term>coap://{host}[:{port}]/{path}[?{query}]</item>
    ///         <item><term>格式3：</term>coap://{host}[:{port}]/{path1}[/{path2}]...[/{pathN}][?{query}]</item>
    /// </list>
    /// </param>
    /// <param name="msgType">消息类型，默认为<see cref="CoAPMessageType.Confirmable"/></param>
    /// <returns>MessageId</returns>
    public char get(String url, CoAPMessageType msgType) throws Exception {

        CoAPPackage cp = new CoAPPackage();
        cp.setCode(CoAPRequestMethod.Get);
        //TODO Token要实现一个生成器
        cp.setToken(new byte[]{0x01,0x02,0x03,0x04});
        //TODO MessageId的生成配合拥塞控制算法，此处指定为固定值
        cp.setMesssageId((char)123456);
        cp.setMessageType(msgType==null?CoAPMessageType.Confirmable:msgType);
        UriInfo uri = UriInfo.Parse(url);

        if (!StringUtil.isNullOrEmpty(uri.Url))
        {
            PackUrl(uri, cp);
            //发起通讯
            if (!StringUtil.isNullOrEmpty(uri.Host))
            {
                sendMessage(uri.Host, uri.Port == 0 ? CoAPProtocol.Port : uri.Port, cp);
            }
            else
            {
                throw new Exception(String.format("DNS无法解析指定的域名:%s",uri.Domain));
            }
        }
        else
        {
            throw new Exception(String.format("本地无法解析指定的链接地址:%s",url));
        }
        return cp.getMesssageId();
    }
    /// <summary>
    /// Get方法，默认消息类型为<see cref="CoAPMessageType.Confirmable"/>
    /// </summary>
    /// <param name="url"></param>
    /// <returns>MessageId</returns>
    public char get(String url) throws Exception {
        return get(url, CoAPMessageType.Confirmable);
    }

    public char post(String url, CoAPMessageType msgType, ContentFormat contentType, byte[] postBody) throws Exception {
        CoAPPackage cp = new CoAPPackage();
        cp.setCode(CoAPRequestMethod.Post);
        //TODO Token要实现一个生成器
        cp.setToken(new byte[]{0x01,0x02,0x03,0x04});
        //TODO MessageId的生成配合拥塞控制算法，此处指定为固定值
        cp.setMesssageId((char)123456);
        cp.setMessageType(msgType==null?CoAPMessageType.Confirmable:msgType);
        UriInfo uri = UriInfo.Parse(url);

        if (!StringUtil.isNullOrEmpty(uri.Url))
        {
            PackUrl(uri, cp);

            cp.SetContentType(contentType);

            cp.setPayload( postBody);

            //发起通讯
            if (!StringUtil.isNullOrEmpty(uri.Host))
            {
                sendMessage(uri.Host, uri.Port == 0 ? CoAPProtocol.Port : uri.Port, cp);
            }
            else
            {
                throw new Exception(String.format("DNS无法解析指定的域名:%s",uri.Domain));
            }
        }
        else
        {
            throw new Exception(String.format("本地无法解析指定的链接地址:%s",url));
        }
        return cp.getMesssageId();
    }

    //TODO 是否会出现安全问题
    private void put(String url)
    {

    }
    //TODO 是否会出现安全问题
    private void delete(String url)
    {

    }
}
