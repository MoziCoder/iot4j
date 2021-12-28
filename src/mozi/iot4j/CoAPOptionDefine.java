package mozi.iot4j;
//CoAP Option Numbers Registry
//|       0-255 | IETF Review or IESG Approval
//|    256-2047 | Specification Required
//|  2048-64999 | Expert Review
//| 65000-65535 | Experimental use(no operational use)

//     0 | (Reserved)             | [RFC7252] |
//     1 | If-Match               | [RFC7252] |
//     3 | Uri-Host               | [RFC7252] |
//     4 | ETag                   | [RFC7252] |
//     5 | If-None-Match          | [RFC7252] |
//     6 | Extended-Token-Length  | [RFC8974] |
//     7 | Uri-Port               | [RFC7252] |
//     8 | Location-Path          | [RFC7252] |
//    11 | Uri-Path               | [RFC7252] |
//    12 | Content-Format         | [RFC7252] |
//    14 | Max-Age                | [RFC7252] |
//    15 | Uri-Query              | [RFC7252] |
//    17 | Accept                 | [RFC7252] |
//    20 | Location-Query         | [RFC7252] |
//
//     23 | Block2                 | [RFC7959] |
//     27 | Block1                 | [RFC7959] |
//     28 | Size2                  | [RFC7959] |
//
//     35 | Proxy-Uri              | [RFC7252] |
//     39 | Proxy-Scheme           | [RFC7252] |
//     60 | Size1                  | [RFC7252] |
//    128 | (Reserved)             | [RFC7252] |
//    132 | (Reserved)             | [RFC7252] |
//    136 | (Reserved)             | [RFC7252] |
//    140 | (Reserved)             | [RFC7252] |
//
// Option Delta代表Option的类型，该值代表了上表中Option类型的代码值与上一个Option代码值之间的差值
// （如果该Option为第一个Option，则直接表达该Option的Option Delta）

/// 由于Option Delta只有4位，最大只能表达15，为了解决这个问题，coap协议有着如下规定：

// 当Option Delta号码<=12时：Option Delta位为实际的Option Delta值
// 当Option Delta号码<269时：Option Delta位填入13；并且在后面的Option Delta(extended) 位会占用1字节，并且填入的数为实际Option Delta值减去13
// 当Option Delta号码<65804时：Option Delta位填入14；并且在后面的Option Delta(extended)位会占用2字节，并且填入的数为实际Option Delta值减去269

// 特别注意，填入的Option Delta值不可能为15（0x0f）当遇到15时，该包无效

public enum CoAPOptionDefine {

    IfMatch("If-Match", (char)1),
    UriHost("Uri-Host",(char)3),
    ETag("ETag",(char)4),
    IfNoneMatch("If-None-Match",(char)5),
    ExtendedTokenLength("Extended-Token-Length",(char)6),
    UriPort("Uri-Port",(char)7),
    LocationPath("Location-Path",(char)8),
    UriPath("Uri-Path",(char)11),
    ContentFormat("Content-Format",(char)12),
    MaxAge("Max-Age",(char)14),
    UriQuery("Uri-Query",(char)15),
    Accept("Accept",(char)17),
    LocationQuery("Location-Query",(char)20),

    Block2("Block2",(char)23),    //RFC 7959
    Block1("Block1",(char)27),    //RFC 7959

    Size2("Size2",(char)28), //RFC 7959

    ProxyUri("Proxy-Uri",(char)35),
    ProxyScheme("Proxy-Scheme",(char)39),

    Size1("Size1",(char)60),

    Unknown("Unknown",(char)0);

    private String _name="";
    //选项序号
    private char _optionNumber;

    private boolean _critical,_unsafe,_noCacheKey;

    CoAPOptionDefine(String name,char optionNumber){
        _name=name;

        _optionNumber=optionNumber;
    }
//
//    public String getName() {
//        return _name;
//    }
//    public  char getOptionNumber() {
//        return _optionNumber;
//    }

    public boolean getCritical() {
        return (((byte)_optionNumber) & 0x01) == 0x01;
    }

    public boolean UnSafe()
    {
        return (((byte)_optionNumber) & 0x02) == 0x02;
    }

    public boolean NoCacheKey()
    {
        return (((byte)_optionNumber) & 0x1e) == 0x1e;
    }

    public String toString()
    {
        return String.format("Option Name:%s,OptionNumber:%d,Figure:%s", _name, _optionNumber, String.join(",", _critical ? "Critical" : "", _unsafe ? "UnSafe" : "", _noCacheKey ? "NoCacheKey" : ""));
    }
};