package mozi.iot4j;
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
/**
* 响应码
*/
public class CoAPResponseCode extends CoAPCode
        {

public static CoAPResponseCode Created = new CoAPResponseCode("Created", "Created", (byte)2, (byte)1);
public static CoAPResponseCode Deleted = new CoAPResponseCode("Deleted", "Deleted", (byte)2, (byte)2);
public static CoAPResponseCode Valid = new CoAPResponseCode("Valid", "Valid", (byte)2, (byte)3);
public static CoAPResponseCode Changed = new CoAPResponseCode("Changed", "Changed", (byte)2, (byte)4);
public static CoAPResponseCode Content = new CoAPResponseCode("Content", "Content", (byte)2, (byte)5);

public static CoAPResponseCode Continue = new CoAPResponseCode("Content", "Content", (byte)2, (byte)31);

public static CoAPResponseCode BadRequest = new CoAPResponseCode("BadRequest", "Bad Request", (byte)4,(byte) 0);
public static CoAPResponseCode Unauthorized = new CoAPResponseCode("Unauthorized", "Unauthorized", (byte)4,(byte) 1);
public static CoAPResponseCode BadOption = new CoAPResponseCode("BadOption", "Bad Option", (byte)4,(byte) 2);
public static CoAPResponseCode Forbidden = new CoAPResponseCode("Forbidden", "Forbidden",(byte) 4,(byte) 3);
public static CoAPResponseCode NotFound = new CoAPResponseCode("NotFound", "Not Found",(byte) 4,(byte) 4);
public static CoAPResponseCode MethodNotAllowed = new CoAPResponseCode("MethodNotAllowed", "Method Not Allowed",(byte) 4,(byte) 5);
public static CoAPResponseCode NotAcceptable = new CoAPResponseCode("NotAcceptable", "Not Acceptable",(byte) 4,(byte) 6);

public static CoAPResponseCode RequestEntityIncomplete = new CoAPResponseCode(" RequestEntityIncomplete", " Request Entity Incomplete",(byte) 4,(byte) 8);

public static CoAPResponseCode PreconditionFailed = new CoAPResponseCode("PreconditionFailed", "Precondition Failed",(byte) 4,(byte) 12);
public static CoAPResponseCode RequestEntityTooLarge = new CoAPResponseCode("RequestEntityTooLarge", "Request Entity Too Large",(byte) 4,(byte) 13);
public static CoAPResponseCode UnsupportedContentFormat = new CoAPResponseCode("UnsupportedContentFormat", "Unsupported Content-Format",(byte) 4,(byte) 15);
public static CoAPResponseCode InternalServerError = new CoAPResponseCode("InternalServerError", "Internal Server Error",(byte) 5,(byte) 0);
public static CoAPResponseCode NotImplemented = new CoAPResponseCode("NotImplemented", "Not Implemented",(byte) 5,(byte) 1);
public static CoAPResponseCode BadGateway = new CoAPResponseCode("BadGateway", "Bad Gateway",(byte) 5,(byte) 2);
public static CoAPResponseCode ServiceUnavailable = new CoAPResponseCode("ServiceUnavailable", "Service Unavailable",(byte) 5,(byte) 3);
public static CoAPResponseCode GatewayTimeout = new CoAPResponseCode("GatewayTimeout", "Gateway Timeout",(byte) 5,(byte) 4);
public static CoAPResponseCode ProxyingNotSupported = new CoAPResponseCode("ProxyingNotSupported", "Proxying Not Supported",(byte) 5,(byte) 5);

        protected CoAPResponseCode(String name, String description, byte category, byte detail)
        {
            super(name, description, category, detail);
        }

        }
