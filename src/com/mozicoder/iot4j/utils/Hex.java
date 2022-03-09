package com.mozicoder.iot4j.utils;

public class Hex {
    public static byte[] From(String hexStr){
        int hexlen = hexStr.length()/2;
        byte [] result;
        result = new byte[hexlen];
        for (int i = 0; i < hexlen; i++){
            result[i]=(byte)Integer.parseInt(hexStr.substring(i*2,i*2+2),16);
        }
        return result;
    }

    public static String To(byte[] data){
        int len = data.length;
        String restult = new String();
        for(int i =0;i<len;i++)
        {
            restult += String.format( "%02x",data[i] );
        }
        return restult;
    }
}
