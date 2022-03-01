package com.mozicoder.iot4j;

import com.mozicoder.iot4j.cache.MessageCacheManager;
import com.mozicoder.iot4j.event.ResponseEvent;
import com.mozicoder.iot4j.utils.StringUtil;
import com.mozicoder.iot4j.utils.Uint32;
import com.mozicoder.iot4j.utils.UriInfo;
import java.net.DatagramPacket;

//TODO 即时响应ACK，延迟响应CON,消息可即时响应也可处理完成后响应，延迟消息需要后端缓存支撑
//TODO 拥塞算法
//TODO 安全认证
//TODO 消息缓存
//TODO 分块传输 RFC 7959
//TODO 对象安全

/**
 * CoAP客户端
 * @author Jason
 * @date 2021-12-19
 */
public class CoAPClient extends CoAPPeer {

    private boolean _randomPort = true;

    private CoAPTransmissionConfig _transConfig = new CoAPTransmissionConfig();

    private MessageCacheManager _cacheManager;

    private ResponseEvent _responseEvent;

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

    public CoAPClient() {
        _cacheManager = new MessageCacheManager(this);
        //配置本地服务口地址
    }

    /**
     * 设置本地端口，默认为{@CoAPProtocol.Port}
     *
     * @param port
     * @returns
     */
    public CoAPClient setPort(int port) {
        BindPort = port;
        _randomPort = false;
        return this;
    }
    /**
     * 设置Response回调事件
     * @param event
     */
    public void setResponseListener(ResponseEvent event){
        _responseEvent=event;
    }

    /**
     * 发送请求消息,此方法为高级方法。
     * 如果对协议不够了解，请不要调用。
     * 此方法不会调用DNS解析域名，DOMAIN地址请先转换为IP地址，然后填充到“Uri-Host”选项中
     *  @param host 服务器地址IPV4/IPV6
     *  @param port 服务器端口
     *  @param pack 数据报文
     * @returns MessageId
     * @see CoAPPeer#sendMessage(String, int, CoAPPackage)
     */
    @Override
    public char sendMessage(String host, int port, CoAPPackage pack) {
        if (pack.getMesssageId() == 0)
        {
            pack.setMesssageId(_cacheManager.GenerateMessageId());
        }
        return super.sendMessage(host,port,pack);
    }

    /**
     * 注入URL相关参数,domain,port,paths,queries
     *  <list type="bullet">
     *      <listheader>自动注入的Option</listheader>
     *      <item><term><see cref="CoAPOptionDefine.UriHost"/></term>如果URL中的主机地址为域名，则注入此Option</item>
     *      <item><term><see cref="CoAPOptionDefine.UriPort"/></term></item>
     *      <item><term><see cref="CoAPOptionDefine.UriPath"/></term>以'/'分割Option</item>
     *      <item><term><see cref="CoAPOptionDefine.UriQuery"/></term>以'&'分割Option</item>
     *  </list>
     * @param uri
     * @param cp
     */
    private void PackUrl(UriInfo uri, CoAPPackage cp) {

        //注入域名
        if (!StringUtil.isNullOrEmpty(uri.Domain)) {
            cp.setOption(CoAPOptionDefine.UriHost, uri.Domain);
        }
        //注入端口号
        if (uri.Port > 0 && !(uri.Port == CoAPProtocol.Port || uri.Port == CoAPProtocol.SecurePort)) {
            cp.setOption(CoAPOptionDefine.UriPort, new Uint32(uri.Port));
        }
        //注入路径
        for (int i = 0; i < uri.Paths.length; i++) {
            cp.setOption(CoAPOptionDefine.UriPath, uri.Paths[i]);
        }
        //注入查询参数
        for (int i = 0; i < uri.Queries.length; i++) {
            cp.setOption(CoAPOptionDefine.UriQuery, uri.Queries[i]);
        }

    }

    /**
     * Get提交 填入指定格式的URI，如果是域名，程序会调用DNS进行解析
     *
     * @param url 地址中的要素会被分解注入到Options中
     *      <list type="table">
     *            <listheader>URI格式:{host}-IPV4地址,IPV6地址,Domain域名;{path}-路径,请使用REST样式路径;{query}为查询参数字符串</listheader>
     *            <item><term>格式1：</term>coap://{host}[:{port}]/{path}</item>
     *            <item><term>格式2：</term>coap://{host}[:{port}]/{path}[?{query}]</item>
     *            <item><term>格式3：</term>coap://{host}[:{port}]/{path1}[/{path2}]...[/{pathN}][?{query}]</item>
     *      </list>
     * @param msgType 消息类型，默认为{@CoAPMessageType.Confirmable}
     * @returns MessageId
     */
    public char get(String url, CoAPMessageType msgType) throws Exception {

        CoAPPackage cp = new CoAPPackage();
        cp.setCode(CoAPRequestMethod.Get);
        //TODO Token要实现一个生成器
        cp.setToken(new byte[]{0x01, 0x02, 0x03, 0x04});
        //DONE MessageId的生成配合拥塞控制算法，此处指定为固定值
        cp.setMesssageId(_cacheManager.GenerateMessageId());
        cp.setMessageType(msgType == null ? CoAPMessageType.Confirmable : msgType);
        UriInfo uri = UriInfo.Parse(url);

        if (!StringUtil.isNullOrEmpty(uri.Url)) {
            PackUrl(uri, cp);
            //发起通讯
            if (!StringUtil.isNullOrEmpty(uri.Host)) {
                sendMessage(uri.Host, uri.Port == 0 ? CoAPProtocol.Port : uri.Port, cp);
            } else {
                throw new Exception(String.format("DNS无法解析指定的域名:%s", uri.Domain));
            }
        } else {
            throw new Exception(String.format("本地无法解析指定的链接地址:%s", url));
        }
        return cp.getMesssageId();
    }

    /**
     * Get方法，默认消息类型为{@CoAPMessageType.Confirmable}
     * @param url
     * @returns MessageId
     * @see #get(String, CoAPMessageType)
     */
    public char get(String url) throws Exception {
        return get(url, CoAPMessageType.Confirmable);
    }

    /**
     * Post方法，默认消息类型为{@CoAPMessageType.Confirmable}
     * @param url
     * @param msgType
     * @param contentType
     * @param postBody
     * @return
     * @throws Exception
     */
    public char post(String url, CoAPMessageType msgType, ContentFormat contentType, byte[] postBody) throws Exception {
        CoAPPackage cp = new CoAPPackage();
        cp.setCode(CoAPRequestMethod.Post);
        //TODO Token要实现一个生成器
        cp.setToken(new byte[]{0x01, 0x02, 0x03, 0x04});
        //DONE MessageId的生成配合拥塞控制算法，此处指定为固定值
        cp.setMesssageId(_cacheManager.GenerateMessageId());
        cp.setMessageType(msgType == null ? CoAPMessageType.Confirmable : msgType);
        UriInfo uri = UriInfo.Parse(url);

        if (!StringUtil.isNullOrEmpty(uri.Url)) {
            PackUrl(uri, cp);

            cp.setContentType(contentType);

            cp.setPayload(postBody);

            //发起通讯
            if (!StringUtil.isNullOrEmpty(uri.Host)) {
                sendMessage(uri.Host, uri.Port == 0 ? CoAPProtocol.Port : uri.Port, cp);
            } else {
                throw new Exception(String.format("DNS无法解析指定的域名:%s", uri.Domain));
            }
        } else {
            throw new Exception(String.format("本地无法解析指定的链接地址:%s", url));
        }
        return cp.getMesssageId();
    }

    //TODO 是否会出现安全问题
    private void put(String url) {

    }

    //TODO 是否会出现安全问题
    private void delete(String url) {

    }

    /**
     * 接收数据回调
     * @param dp
     */
    @Override
    public void onPackageReceived(DatagramPacket dp) {
        CoAPPackage cp=CoAPPackage.parse(dp.getData(),CoAPPackageType.Response);
        System.out.println(cp.getCode().getDescription());
        if(_responseEvent!=null){
            _responseEvent.onResponse(dp.getAddress().getHostAddress(),dp.getPort(),cp);
        }
    }
}
