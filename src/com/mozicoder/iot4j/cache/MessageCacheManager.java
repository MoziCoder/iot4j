package com.mozicoder.iot4j.cache;

import com.mozicoder.iot4j.CoAPPeer;
import com.mozicoder.iot4j.utils.ByteStreamUtil;
import java.util.Random;

//TODO 进一步完成缓存对象管理
/**
 * 消息缓存管理器
 * @author Jason
 * @date 2021/1/4
 */
public class MessageCacheManager {
    private static final char[] _seads = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private char _indStart = 0;

    public MessageCacheManager(CoAPPeer peer) {

    }

    /// <summary>
    /// 生成未使用的MessageId
    /// </summary>
    /// <returns></returns>
    public char generateMessageId() {
        Random random = new Random();
        byte high = Integer.valueOf(new String(new char[]{_seads[random.nextInt(16)], _seads[random.nextInt(16)]}), 16).byteValue();
        byte low = Integer.valueOf(new String(new char[]{_seads[random.nextInt(16)], _seads[random.nextInt(16)]}), 16).byteValue();

        return ByteStreamUtil.charFromBytes(new byte[]{high, low});
    }

    /// <summary>
    /// 随机生成Token
    /// </summary>
    /// <returns></returns>
    public  byte[] generateToken(int tokenLen)
    {
        byte[] data = new byte[tokenLen];
        Random ran = new Random();
        ran.nextBytes(data);
        return data;
    }
}
