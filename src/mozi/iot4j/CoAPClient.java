package mozi.iot4j;
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

}
