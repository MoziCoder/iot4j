package mozi.iot4j;
/**
 * 请求码
 * 代码 8bits=3bits+5bits
 * 高3位为分类
 * 低5位为明细
 *
 *  0.00      Indicates an Empty message (see Section 4.1).
 *  0.01-0.31 Indicates a request.Values in this range are assigned by the "CoAP Method Codes" sub-registry(see Section 12.1.1).
 *    0.01  GET    | [RFC7252]
 *    0.02  POST   | [RFC7252]
 *    0.03  PUT    | [RFC7252]
 *    0.04  DELETE | [RFC7252]
 *
 *
*/
// 代码 8bits=3bits+5bits
// 高3位为分类
// 低5位为明细
//
//  0.00      Indicates an Empty message (see Section 4.1).
//  0.01-0.31 Indicates a request.Values in this range are assigned by the "CoAP Method Codes" sub-registry(see Section 12.1.1).
//     0.01  GET    | [RFC7252]
//     0.02  POST   | [RFC7252]
//     0.03  PUT    | [RFC7252]
//     0.04  DELETE | [RFC7252]
//
//  1.00-1.31 Reserved
//  2.00-5.31 Indicates a response.Values in this range are assigned bythe "CoAP Response Codes" sub-registry(see Section 12.1.2).
//
//     2.01 | Created                      | [RFC7252] |
//     2.02 | Deleted                      | [RFC7252] |
//     2.03 | Valid                        | [RFC7252] |
//     2.04 | Changed                      | [RFC7252] |
//     2.05 | Content                      | [RFC7252] |
//
//     2.31 | Continue                     | [RFC7959] |
//
//     4.00 | Bad Request                  | [RFC7252] |
//     4.01 | Unauthorized                 | [RFC7252] |
//     4.02 | Bad Option                   | [RFC7252] |
//     4.03 | Forbidden                    | [RFC7252] |
//     4.04 | Not Found                    | [RFC7252] |
//     4.05 | Method Not Allowed           | [RFC7252] |
//     4.06 | Not Acceptable               | [RFC7252] |
//
//     4.08 | Request Entity Incomplete    | [RFC7959] |
//
//     4.12 | Precondition Failed          | [RFC7252] |
//     4.13 | Request Entity Too Large     | [RFC7252] |
//     4.15 | Unsupported Content-Format   | [RFC7252] |
//     5.00 | Internal Server Error        | [RFC7252] |
//     5.01 | Not Implemented              | [RFC7252] |
//     5.02 | Bad Gateway                  | [RFC7252] |
//     5.03 | Service Unavailable          | [RFC7252] |
//     5.04 | Gateway Timeout              | [RFC7252] |
//     5.05 | Proxying Not Supported       | [RFC7252] |
//
//  6.00-7.31 Reserved

public class CoAPRequestMethod extends CoAPCode
        {

public static CoAPRequestMethod Get = new CoAPRequestMethod("GET", "", (byte)0, (byte)1);
public static CoAPRequestMethod Post = new CoAPRequestMethod("POST", "", (byte)0, (byte)2);
public static CoAPRequestMethod Put = new CoAPRequestMethod("PUT", "", (byte)0, (byte)3);
public static CoAPRequestMethod Delete = new CoAPRequestMethod("DELETE", "", (byte)0, (byte)4);

        protected CoAPRequestMethod(String name, String description, byte category, byte detail)
        {

            super(name, description, category, detail);
        }
}
