package mozi.iot4j;

/*
 * CoAP协议
 * @author Jason
 * @date 2021/12/29
 */
public class CoAPProtocol {
    /**
     * 默认服务端口UDP
     */
    public static final int Port = 5683;
    /**
     * 默认DTLS服务端口
     */
    public static final int SecurePort = 5684;
    /**
     * 默认服务端口TCP
     */
    public static final int TcpPort = 5685;
    /**
     * 头部和内容分割数据
     */
    public static final byte HeaderEnd = (byte) 0xFF;
};

