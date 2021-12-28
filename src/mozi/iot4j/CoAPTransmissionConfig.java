package mozi.iot4j;
/**
 * CoAP传输时间参数
 */
public class CoAPTransmissionConfig {
    /**
     * 确认超时时间 seconds 取值推荐>1
     */
    public double ACK_TIMEOUT = 2;
    /**
     * 确认超时因子 取值推荐>1
     */
    public double ACK_RANDOM_FACTOR = 1.5;
    /**
     * 最大重传次数
     */
    public int MAX_RETRANSMIT = 4;

    public int NSTART = 1;
    /**
     * 空闲时间 seconds
     */
    public int DEFAULT_LEISURE = 5;
    /**
     * 查看频率byte/second
     */
    public int PROBING_RATE = 1;
    /**
     * 最大缓存时间 seconds
     */
    public int MAX_AGE = 60;
    //MAX_TRANSMIT_SPAN = ACK_TIMEOUT * ((2 ** MAX_RETRANSMIT) - 1) * ACK_RANDOM_FACTOR
    /**
     * 最大重传送间隔
     */
    public int MAX_TRANSMIT_SPAN = 45;
    //MAX_TRANSMIT_WAIT = ACK_TIMEOUT * ((2 ** (MAX_RETRANSMIT + 1)) - 1) *ACK_RANDOM_FACTOR
    /**
     * 最大消息确认等待时间，请求方等待ACK|RESET消息的超时时间 seconds
     */
    public int MAX_TRANSMIT_WAIT = 93;
    /**
     * 最大包接收延迟时间
     */
    public int MAX_LATENCY = 100;
    /**
     * 包确认时间 默认等于ACK_TIMEOUT seconds
     */
    public int PROCESSING_DELAY = 2;
    //2 * MAX_LATENCY) + PROCESSING_DELAY
    /**
     * 最大传输回环时间
     */
    public int MAX_RTT = 202;

    //MAX_TRANSMIT_SPAN + (2 * MAX_LATENCY) + PROCESSING_DELAY
    /**
     * 数据交换周期
     */
    public int EXCHANGE_LIFETIME = 247;
    //MAX_TRANSMIT_SPAN + MAX_LATENCY
    /**
     * Non-confirmable包MessageId重用周期
     */
    public int NON_LIFETIME = 145;
}