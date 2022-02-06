package com.mozicoder.iot4j.utils;

/**
 * @author Jason
 * @date 2022/1/4
 */
public class StringUtil {
    /**
     * 字符串为空判断
     * @param data
     * @return
     */
    public static boolean isNullOrEmpty(String data){
        return data==null|| data.equals("")||data.length()==0&& data.trim().equals("");
    }
}
