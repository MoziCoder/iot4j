package com.mozicoder.iot4j;

import com.mozicoder.iot4j.cache.MessageCacheManager;
import com.mozicoder.iot4j.event.ResponseEvent;
import com.mozicoder.iot4j.utils.StringUtil;
import com.mozicoder.iot4j.utils.UriInfo;
import java.net.DatagramPacket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

//TODO 即时响应ACK，延迟响应CON,消息可即时响应也可处理完成后响应，延迟消息需要后端缓存支撑
//TODO 拥塞算法
//TODO 安全认证
//TODO 消息缓存
//TODO 分块传输 RFC 7959
//TODO 对象安全
//TODO 映射CoAP到Http
//TODO 观察者模式 观察者模式功能比较弱，是否考虑不实现？
//TODO Extend Token Length RFC8974

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
    private byte[] _token;

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
        _token=_cacheManager.generateToken(8);
    }

    /**
     * 设置Token
     * @param token
     * @return
     */
    public CoAPClient setToken(byte[] token){
        _token=token;
        return this;
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
     *  @returns MessageId
     *      @see CoAPPeer#sendMessage(String, int, CoAPPackage)
     */
    @Override
    public char sendMessage(String host, int port, CoAPPackage pack) {
        if (pack.getMesssageId() == 0)
        {
            pack.setMesssageId(_cacheManager.generateMessageId());
        }
        return super.sendMessage(host,port,pack);
    }

    /**
     * 发送请求消息
     * @param url
     * @param msgType
     * @param msgId
     * @param token
     * @param method
     * @param options
     * @param payload
     * @return
     * @throws Exception
     */
    public char sendMessage(String url,CoAPMessageType msgType,char msgId,byte[] token,CoAPRequestMethod method, ArrayList<CoAPOption> options,byte[] payload) throws Exception {
        CoAPPackage cp = new CoAPPackage();
        cp.setCode(method);
        //DONE Token要实现一个生成器
        cp.setToken(token);
        //DONE MessageId的生成配合拥塞控制算法，此处指定为固定值
        cp.setMesssageId(msgId);
        cp.setMessageType(msgType == null ? CoAPMessageType.Confirmable : msgType);
        UriInfo uri = UriInfo.Parse(url);

        if (!StringUtil.isNullOrEmpty(uri.Url)) {
            cp.setUri(uri);
            cp.setPayload(payload);
            //发起通讯
            if (!StringUtil.isNullOrEmpty(uri.Host)) {
                if (options != null)
                {
                    for (CoAPOption opt:options)
                    {
                        cp.setOption(opt.getOption(), opt.getValue());
                    }
                }
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
     *
     * @param url
     * @param msgType
     * @param method
     * @param options
     * @param payload
     * @return
     * @throws Exception
     */
    public  char sendMessage(String url,CoAPMessageType msgType,CoAPRequestMethod method, ArrayList<CoAPOption> options,byte[] payload) throws Exception {
        return sendMessage(url,msgType,_cacheManager.generateMessageId(),_cacheManager.generateToken(8),method,options,payload);
    }

    /**
     * Get方法 填入指定格式的URI，如果是域名，程序会调用DNS进行解析
     *
     * @param url 地址中的要素会被分解注入到Options中
     *      URI格式:{host}-IPV4地址,IPV6地址,Domain域名;{path}-路径,请使用REST样式路径;{query}为查询参数字符串
     *      <ul>
     *            <li>格式1：coap://{host}[:{port}]/{path}</li>
     *            <li>格式2：coap://{host}[:{port}]/{path}[?{query}]</li>
     *            <li>格式3：coap://{host}[:{port}]/{path1}[/{path2}]...[/{pathN}][?{query}]</li>
     *      </ul>
     * @param msgType 消息类型，默认为{@CoAPMessageType.Confirmable}
     * @param options 选项值，可设置除{@CoAPOptionDefine.UriHost}{@CoAPOptionDefine.UriPort}{@CoAPOptionDefine.UriPath}{@CoAPOptionDefine.UriQuery}之外的选项值
     * @returns MessageId
     */
    public char get(String url, CoAPMessageType msgType, ArrayList<CoAPOption> options) throws Exception {

        CoAPPackage cp = new CoAPPackage();
        cp.setCode(CoAPRequestMethod.Get);
        //DONE Token要实现一个生成器
        cp.setToken(_token);
        //DONE MessageId的生成配合拥塞控制算法，此处指定为固定值
        cp.setMesssageId(_cacheManager.generateMessageId());
        cp.setMessageType(msgType == null ? CoAPMessageType.Confirmable : msgType);
        UriInfo uri = UriInfo.Parse(url);

        if (!StringUtil.isNullOrEmpty(uri.Url)) {
            cp.setUri(uri);
            //发起通讯
            if (!StringUtil.isNullOrEmpty(uri.Host)) {
                if (options != null)
                {
                    for (CoAPOption opt:options)
                    {
                        cp.setOption(opt.getOption(), opt.getValue());
                    }
                }
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
     * Get方法
     * @param url 地址中的要素会被分解注入到Options中
     * @param msgType 消息类型，默认为{@CoAPMessageType.Confirmable}
     * @return MessageId
     * @throws Exception
     * @see #get(String, CoAPMessageType, ArrayList)
     */
    public char get(String url, CoAPMessageType msgType) throws Exception {
        return get(url,msgType,null);
    }
    /**
     * Get方法，默认消息类型为{@CoAPMessageType.Confirmable}
     * @param url 地址中的要素会被分解注入到Options中
     * @returns MessageId
     * @see #get(String, CoAPMessageType, ArrayList)
     */
    public char get(String url) throws Exception {
        return get(url, CoAPMessageType.Confirmable,null);
    }

    /**
     * Post方法，默认消息类型为{@CoAPMessageType.Confirmable}
     * @param url 地址中的要素会被分解注入到Options中
     * @param msgType 消息类型，默认为{@CoAPMessageType.Confirmable}
     * @param contentType
     * @param options
     * @param payload
     * @return MessageId
     * @throws Exception
     */
    public char post(String url, CoAPMessageType msgType, ContentFormat contentType, ArrayList<CoAPOption> options, byte[] payload) throws Exception {
        CoAPPackage cp = new CoAPPackage();
        cp.setCode(CoAPRequestMethod.Post);
        //DONE Token要实现一个生成器
        cp.setToken(_token);
        //DONE MessageId的生成配合拥塞控制算法，此处指定为固定值
        cp.setMesssageId(_cacheManager.generateMessageId());
        cp.setMessageType(msgType == null ? CoAPMessageType.Confirmable : msgType);
        UriInfo uri = UriInfo.Parse(url);

        if (!StringUtil.isNullOrEmpty(uri.Url)) {
            cp.setUri(uri);
            cp.setContentType(contentType);

            cp.setPayload(payload);

            //发起通讯
            if (!StringUtil.isNullOrEmpty(uri.Host)) {
                if (options != null)
                {
                    for (CoAPOption opt:options)
                    {
                        cp.setOption(opt.getOption(), opt.getValue());
                    }
                }
                sendMessage(uri.Host, uri.Port == 0 ? CoAPProtocol.Port : uri.Port, cp);
            } else {
                throw new Exception(String.format("DNS无法解析指定的域名:%s", uri.Domain));
            }
        } else {
            throw new Exception(String.format("本地无法解析指定的链接地址:%s", url));
        }
        return cp.getMesssageId();
    }
    public char post(String url, CoAPMessageType msgType, ContentFormat contentType, byte[] payload) throws Exception {
        return post(url, msgType, contentType,null, payload);
    }

    /**
     * Post方法
     * @param url 地址中的要素会被分解注入到Options中
     * @param msgType 消息类型，默认为{@CoAPMessageType.Confirmable}
     * @param contentType
     * @param options
     * @param payload
     * @return MessageId
     * @throws Exception
     */
    public char post(String url, CoAPMessageType msgType, ContentFormat contentType, ArrayList<CoAPOption> options, String payload) throws Exception {
        return post(url, msgType, contentType, options, payload.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Post方法
     * @param url 地址中的要素会被分解注入到Options中
     * @param msgType 消息类型，默认为{@CoAPMessageType.Confirmable}
     * @param contentType
     * @param payload
     * @return MessageId
     * @throws Exception
     */
    public char post(String url, CoAPMessageType msgType, ContentFormat contentType,  String payload) throws Exception {
        return post(url, msgType, contentType, null, payload);
    }
    /**
     * PUT方法
     * @param url 地址中的要素会被分解注入到Options中
     * @param msgType 消息类型，默认为{@CoAPMessageType.Confirmable}
     * @param contentType
     * @param options
     * @param payload
     * @return MessageId
     * @throws Exception
     */
    public char put(String url, CoAPMessageType msgType, ContentFormat contentType, ArrayList<CoAPOption> options, byte[] payload) throws Exception {
        CoAPPackage cp = new CoAPPackage();
        cp.setCode(CoAPRequestMethod.Put);
        //DONE Token要实现一个生成器
        cp.setToken(_token);
        //DONE MessageId的生成配合拥塞控制算法，此处指定为固定值
        cp.setMesssageId(_cacheManager.generateMessageId());
        cp.setMessageType(msgType == null ? CoAPMessageType.Confirmable : msgType);
        UriInfo uri = UriInfo.Parse(url);

        if (!StringUtil.isNullOrEmpty(uri.Url)) {
            cp.setUri(uri);

            cp.setContentType(contentType);

            cp.setPayload(payload);

            //发起通讯
            if (!StringUtil.isNullOrEmpty(uri.Host)) {
                if (options != null)
                {
                    for (CoAPOption opt:options)
                    {
                        cp.setOption(opt.getOption(), opt.getValue());
                    }
                }
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
     * PUT方法
     * @param url 地址中的要素会被分解注入到Options中
     * @param msgType 消息类型，默认为{@CoAPMessageType.Confirmable}
     * @param contentType
     * @param options
     * @return MessageId
     */
    public char put(String url, CoAPMessageType msgType, ContentFormat contentType, ArrayList<CoAPOption> options) throws Exception {
        return put(url, msgType, contentType, options,null);
    }
    /**
     * DELETE方法
     * @param url 地址中的要素会被分解注入到Options中
     * @param msgType 消息类型，默认为{@CoAPMessageType.Confirmable}
     * @param options
     * @return MessageId
     * @throws Exception
     */
    public char delete(String url, CoAPMessageType msgType,ArrayList<CoAPOption> options) throws Exception {

        CoAPPackage cp = new CoAPPackage();
        cp.setCode(CoAPRequestMethod.Delete);
        //DONE Token要实现一个生成器
        cp.setToken(_token);
        //DONE MessageId的生成配合拥塞控制算法，此处指定为固定值
        cp.setMesssageId(_cacheManager.generateMessageId());
        cp.setMessageType(msgType == null ? CoAPMessageType.Confirmable : msgType);

        UriInfo uri = UriInfo.Parse(url);

        if (!StringUtil.isNullOrEmpty(uri.Url)) {
            cp.setUri(uri);
            //发起通讯
            if (!StringUtil.isNullOrEmpty(uri.Host)) {
                if (options != null)
                {
                    for (CoAPOption opt:options)
                    {
                        cp.setOption(opt.getOption(), opt.getValue());
                    }
                }
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
     * DELETE方法
     * @param url 地址中的要素会被分解注入到Options中
     * @param msgType 消息类型，默认为{@CoAPMessageType.Confirmable}
     * @return MessageId
     */
    public char delete(String url, CoAPMessageType msgType) throws Exception {
        return delete(url, msgType,null);
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
