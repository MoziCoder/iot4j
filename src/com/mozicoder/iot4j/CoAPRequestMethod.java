package com.mozicoder.iot4j;

/**
 * 请求方法
 * @author Jason
 * @date 2021/12/29
 */
public class CoAPRequestMethod extends CoAPCode {

    public static CoAPRequestMethod Get = new CoAPRequestMethod("GET", "", (byte) 0, (byte) 1);
    public static CoAPRequestMethod Post = new CoAPRequestMethod("POST", "", (byte) 0, (byte) 2);
    public static CoAPRequestMethod Put = new CoAPRequestMethod("PUT", "", (byte) 0, (byte) 3);
    public static CoAPRequestMethod Delete = new CoAPRequestMethod("DELETE", "", (byte) 0, (byte) 4);

    protected CoAPRequestMethod(String name, String description, byte category, byte detail) {

        super(name, description, category, detail);
    }
}
