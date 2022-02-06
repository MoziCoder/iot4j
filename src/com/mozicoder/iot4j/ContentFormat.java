package com.mozicoder.iot4j;

/**
 * 内容格式
 * @author Jason
 * @date 2021/12/29
 *  CoAP Content-Formats Registry
 *  <p>
 *      0-255 | Expert Review
 *      256-9999 | IETF Review or IESG Approval
 *      10000-64999 | First Come First Served
 *      65000-65535 | Experimental use(no operational use)
 *  </p>
 *  <p>
 *      text/plain;              | -        |  0 | [RFC2046] [RFC3676]    |
 *      charset=utf-8            |          |    | [RFC5147]              |
 *      application/link-format  | -        | 40 | [RFC6690]              |
 *      application/xml          | -        | 41 | [RFC3023]              |
 *      application/octet-stream | -        | 42 | [RFC2045] [RFC2046]    |
 *      application/exi          | -        | 47 | [REC-exi-20140211]     |
 *      application/json         | -        | 50 | [RFC7159]              |
 *      applicaiton/cbor         | -        | 60 | [RFC7159]              |
 *  </p>
 */
public class ContentFormat extends AbsClassEnum {

    private char _num = 0;
    private String _contentType = "";

    public String getContentType() {
        return _contentType;
    }

    public char getOptionValue() {
        return _num;
    }

    public char getNum() {
        return _num;
    }

    @Override
    public String getTag() {
        return String.valueOf(_num);
    }

    public static ContentFormat TextPlain = new ContentFormat("text/plain", (char) 0);
    public static ContentFormat LinkFormat = new ContentFormat("application/link-format", (char) 40);
    public static ContentFormat XML = new ContentFormat("application/xml", (char) 41);
    public static ContentFormat Stream = new ContentFormat("application/octet-stream", (char) 42);
    public static ContentFormat EXI = new ContentFormat("application/exi", (char) 47);
    public static ContentFormat JSON = new ContentFormat("application/json", (char) 50);
    public static ContentFormat CBOR = new ContentFormat("applicaiton/cbor", (char) 60);

    protected ContentFormat(String contentType, char num) {
        _contentType = contentType;
        _num = num;
    }
}
