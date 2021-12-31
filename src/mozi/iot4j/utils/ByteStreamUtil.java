package mozi.iot4j.utils;

import mozi.iot4j.DNSClient;

import java.util.regex.Pattern;

/**
 * 数值类型转bytes[]数组
 * @author Jason
 * @date 2021/12/30
 * 高位在前 低位在后
 */
public class ByteStreamUtil {

    public static Uint32 uint32FromBytes(byte[] data){
        Uint32 re=new Uint32(0);
        re.setValue(re.getValue()|(data[0]<<24));
        re.setValue(re.getValue()|(data[1]<<16));
        re.setValue(re.getValue()|(data[2]<<8));
        re.setValue(re.getValue()|(data[3]));
        return re;
    }
    /**
     */
    public static byte[] uint32ToBytes(Uint32 data){

        byte[] targets = new byte[4];
        targets[3] = (byte) (data.getValue() & 0xff);// 最低位
        targets[2] = (byte) ((data.getValue() >> 8) & 0xff);// 次低位
        targets[1] = (byte) ((data.getValue() >> 16) & 0xff);// 次高位
        targets[0] = (byte) (data.getValue() >>> 24);// 最高位,无符号右移。
        return targets;
    }

    public static char charFromBytes(byte[] data){
        char re=0;
        re|=data[0]<<8;
        re|=data[1];
        return re;
    }

    public static byte[] charToBytes(char data){
        byte[] targets = new byte[4];
        targets[1] = (byte) (data & 0xff);// 最低位
        targets[0] = (byte) ((data >>> 8) & 0xff);// 次低位
        return targets;
    }
}





