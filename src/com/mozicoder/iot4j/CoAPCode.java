package com.mozicoder.iot4j;

/**
 * CoAP操作代码
 * @author Jason
 * @date 2021/12/29
 * 数据封装格式：
 * 代码 8bits=3bits+5bits
 *      高3位为分类
 *      低5位为明细
 *
 * 代码定义：
 *
 *   0.00      Indicates an Empty message (see Section 4.1).
 *
 *   Requests
 *
 *   0.01-0.31 Indicates a request.Values in this range are assigned by the "CoAP Method Codes" sub-registry(see Section 12.1.1).
 *      0.01  GET    | [RFC7252]
 *      0.02  POST   | [RFC7252]
 *      0.03  PUT    | [RFC7252]
 *      0.04  DELETE | [RFC7252]
 *
 *   1.00-1.31 Reserved
 *
 *   Responses
 *
 *   2.00-5.31 Indicates a response.Values in this range are assigned bythe "CoAP Response Codes" sub-registry(see Section 12.1.2).
 *
 *      2.01 | Created                      | [RFC7252] |
 *      2.02 | Deleted                      | [RFC7252] |
 *      2.03 | Valid                        | [RFC7252] |
 *      2.04 | Changed                      | [RFC7252] |
 *      2.05 | Content                      | [RFC7252] |
 *
 *      2.31 | Continue                     | [RFC7959] |
 *
 *      4.00 | Bad Request                  | [RFC7252] |
 *      4.01 | Unauthorized                 | [RFC7252] |
 *      4.02 | Bad Option                   | [RFC7252] |
 *      4.03 | Forbidden                    | [RFC7252] |
 *      4.04 | Not Found                    | [RFC7252] |
 *      4.05 | Method Not Allowed           | [RFC7252] |
 *      4.06 | Not Acceptable               | [RFC7252] |
 *
 *      4.08 | Request Entity Incomplete    | [RFC7959] |
 *
 *      4.12 | Precondition Failed          | [RFC7252] |
 *      4.13 | Request Entity Too Large     | [RFC7252] |
 *      4.15 | Unsupported Content-Format   | [RFC7252] |
 *      5.00 | Internal Server Error        | [RFC7252] |
 *      5.01 | Not Implemented              | [RFC7252] |
 *      5.02 | Bad Gateway                  | [RFC7252] |
 *      5.03 | Service Unavailable          | [RFC7252] |
 *      5.04 | Gateway Timeout              | [RFC7252] |
 *      5.05 | Proxying Not Supported       | [RFC7252] |
 *
 *   6.00-7.31 Reserved
 */
public class CoAPCode extends AbsClassEnum
 {
    private String _name = "", _description;

    private byte _category = 0, _detail = 0;

    public static CoAPCode Empty = new CoAPCode("Empty", "Empty Message", (byte)0, (byte)0);
    /**
    * 分类
    */
    public int getCategory(){
        return _category;
    }
    /**
    * 明细
    */
    public byte getDetail() {
        return _detail;
    }

    /**
     * 代码名称
    */
    public String getName(){
        return _name;
    }
    public String getDescription(){
        return _description;
    }
    /**
     *
    */
     public byte getPack() {
         return (byte)((_category << 5) | _detail);
     }

     public void setPack(byte pack) {
         _category = (byte)((pack >> 5)&0b00000111);
         _detail = (byte)(pack&0b00011111);
     }

      protected CoAPCode(String name, String description, byte category, byte detail)
      {
            _name = name;
            _description = description;
            _category = category;
            _detail = detail;
        }

     @Override
     protected String getTag() {
         return String.valueOf((byte)(_category << 5) + _detail);
     }
 }
