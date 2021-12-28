package mozi.iot4j;
//Main Reference:RFC7252
//Patial Reference:
//RFC7959 分块传输
//RFC8613 对象安全
//RFC8974 扩展凭据和无状态客户端 

//内容采用UTF-8编码
//头部截断使用0xFF填充

import mozi.iot4j.optionvalues.OptionValue;
import java.util.ArrayList;
import java.util.List;

/**
* 消息类型
* <list type="table">
*     <listheader>取值范围</listheader>
*     <item><term>0</term><see cref="Confirmable"/></item>
*     <item><term>1</term><see cref="NonConfirmable"/></item>
*     <item><term>2</term><see cref="Acknowledgement"/></item>
*     <item><term>3</term><see cref="Reset"/></item>
* </list>
*/
public enum  CoAPMessageType {
            Confirmable("Confirmable", 0),
            NonConfirmable ("NonConfirmable", 1),
            Acknowledgement("Acknowledgement", 2),
            Reset("Reset", 3)
         private String _name = "";
         private byte _typeValue;



         private CoAPMessageType(String name, byte typeValue) {
            _name = name;
            _typeValue = typeValue;
        }

        public byte getValue() {
            return _typeValue;
        }


//protected override string Tag
//        {
//        get
//        {
//        return _typeValue.ToString();
//        }
//        }

            public String getName() {
                return _name;
            }

}

public class CoAPPackage
{
    /**
    * 选项 类似HTTP头属性
    */
    public ArrayList<CoAPOption> Options = new ArrayList<CoAPOption>();
    private byte _version = 1,_tokenLength;
    private CoAPMessageType _msgType;
    private CoAPCode _code;
    private char _msgId;
    private byte[] _token,_payload;

    /**
    * 解析数据包
    *
    * @param data
    * @param isRequest
    */
    public static CoAPPackage Parse(byte[] data, boolean isRequest)
    {
        CoAPPackage pack = new CoAPPackage();
        byte head = data[0];
        pack.Version = (byte)(head >> 6);
        pack.MessageType = AbsClassEnum.Get<CoAPMessageType>(((byte)(head << 2) >> 4).ToString());
        pack.TokenLength = (byte)((byte)(head << 4) >> 4);

        pack.Code = isRequest ? AbsClassEnum.Get<CoAPRequestMethod>(data[1].ToString()) : (CoAPCode)AbsClassEnum.Get<CoAPResponseCode>(data[1].ToString());

        byte[] arrMsgId = new byte[2], arrToken = new byte[pack.TokenLength];
        System.arraycopy(data, 2, arrMsgId, 0, 2);
        System.arraycopy(data, 2 + 2, arrToken, 0, arrToken.Length);
        pack.Token = arrToken;
        pack.MesssageId = BitConverter.ToUInt16(arrMsgId.Revert(), 0);
        //3+2+arrToken.Length+1开始是Option部分
        int bodySplitterPos = 2 + 2 + arrToken.length;
        uint deltaSum = 0;
        while (bodySplitterPos < data.length && data[bodySplitterPos] != CoAPProtocol.HeaderEnd)
        {

            CoAPOption option = new CoAPOption();
            option.OptionHead = data[bodySplitterPos];
            //byte len=(byte)(option.OptionHead)
            int lenDeltaExt = 0, lenLengthExt = 0;
            if (option.Delta <= 12)
            {

            }
            else if (option.Delta == 13)
            {
                lenDeltaExt = 1;
            }
            else if (option.Delta == 14)
            {
                lenDeltaExt = 2;
            }
            if (lenDeltaExt > 0)
            {
                byte[] arrDeltaExt = new byte[2];
                System.arraycopy(data, bodySplitterPos + 1, arrDeltaExt, arrDeltaExt.length - lenDeltaExt, lenDeltaExt);
                option.DeltaExtend = BitConverter.ToUInt16(arrDeltaExt.Revert(), 0);
            }
            //赋默认值
            option.Option = AbsClassEnum.Get<CoAPOptionDefine>((option.DeltaValue + deltaSum).ToString());
            if (object.ReferenceEquals(null, option.Option))
            {
                option.Option = CoAPOptionDefine.Unknown;
            }
            if (option.Length <= 12)
            {

            }
            else if (option.Length == 13)
            {
                lenLengthExt = 1;
            }
            else if (option.Length == 14)
            {
                lenLengthExt = 2;
            }
            if (lenLengthExt > 0)
            {
                byte[] arrLengthExt = new byte[2];
                System.arraycopy(data, bodySplitterPos + 1 + lenDeltaExt, arrLengthExt, arrLengthExt.Length - lenLengthExt, lenLengthExt);
                option.LengthExtend = BitConverter.ToUInt16(arrLengthExt.Revert(), 0);
            }

            option.Value = new byte[option.LengthValue];
            System.arraycopy(data, bodySplitterPos + 1 + lenDeltaExt + lenLengthExt, option.Value.Pack, 0, option.Value.Length);
            pack.Options.Add(option);
            deltaSum += option.Delta;
            //头长度+delta扩展长度+len
            bodySplitterPos += 1 + lenDeltaExt + lenLengthExt + option.Value.length;

        }
        //有效荷载
        if (data.Length > bodySplitterPos && data[bodySplitterPos] == CoAPProtocol.HeaderEnd)
        {
            pack.Payload = new byte[data.Length - bodySplitterPos - 1];

            System.arraycopy(data, bodySplitterPos + 1, pack.Payload, 0, pack.Payload.Length);
        }
        return pack;

    }

    /**
    * 版本 2bits
    */
    public byte getVersion() {
        return _version;
    }

    public void setVersion(byte version) {
        _version=version;
    }

    /**
    * 消息类型 2bits
    */
    public CoAPMessageType getMessageType() {
        return _msgType;
    }

    public void setMessageType(CoAPMessageType messageType) {
        _msgType=messageType;
    }

    /**
    * Token长度 4bits
    * 0-8bytes取值范围
    * 9-15为保留使用，收到此消息时直接消息报错
    */

    public byte getTokenLength() {
        return (byte)(_token == null ? 0 : _token.length);
    }

    public void setTokenLength(byte tokenLength) {
        if (tokenLength == 0)
        {
            _token = null;
        }
        else
        {
            Token = new byte[tokenLength];
        }
    }

    /**
    * 8bits Lengths 9-15 reserved
    */
    public CoAPCode getCode() {
        return _code;
    }

    public void setCode(CoAPCode code) {
        _code=code;
    }

    /**
    * 用于消息确认防重，消息确认-重置 16bits
    */
    public char getMesssageId() {
        return _msgId;
    }

    public void setMesssageId(char messsageId) {
        _msgId =messsageId;
    }

    /**
    * 凭据
    *  0-8bytes 典型应用场景需>=4bytes。本地和远程终结点不变的情况下可以使用同一Token,一般建议每请求重新生成Token
    */
    public byte[] getToken() {
        return _token;
    }

    public void setToken(byte[] token) {
        _token=token;
    }

    /**
    * 包体
    */
    public byte[] getPayload() {
        return _payload;
    }


    // 链接地址
    //
    //public string Url
    //{
    //    get;
    //    set;
    //}

    public void setPayload(byte[] payload) {
        _payload=payload;
    }

    /**
    * 转为HTTP包,ASCII字符串数据包
    *
    */
    //public byte[] ToHttp()
    //{
    //List<string> data = new List<string>();
    //string head = string.Format("{0} ");
    //}

    /**
    * 打包|转为字节流
    */
    public Byte[] Pack()
    {
        List<Byte> data = new ArrayList<Byte>();
        byte head = 0b00000000;
        head = (byte)(head | (_version << 6));
        head = (byte)(head | (_msgType.getValue() << 4));
        head = (byte)(head | _tokenLength);

        data.add(head);
        data.add((byte)(((byte)_code.getCategory() << 5) | ((byte)(_code.getDetail() << 3) >> 3)));
        data.addAll(BitConverter.GetBytes(_msgId).Revert());
        data.addAll(_token);
        Uint delta = 0;

        foreach (CoaAPOption op:Options)
        {
            op.DeltaValue = op.Option.OptionNumber - delta;
            data.addAll(op.Pack);
            delta += op.DeltaValue;
        }
        if (_payload != null)
        {
            data.add(CoAPProtocol.HeaderEnd);
            data.addAll(_payload);
        }
        return (Byte[])data.toArray();
    }

    /**
    * 设置空选项值
    *
    * @param define
    */
    public CoAPPackage SetOption(CoAPOptionDefine define)
    {
        return SetOption(define, new EmptyOptionValue());
    }

    /**
    * 设置选项值，此方法可以设置自定义的选项值类型
    *
    * @param define
    * @param optionValue
    */
    public CoAPPackage SetOption(CoAPOptionDefine define, OptionValue optionValue)
    {
        CoAPOption option = new CoAPOption()
        {
            Option = define,
            Value = optionValue
        };
        var optGreater = Options.FindIndex(x => x.DeltaValue > option.DeltaValue);
        if (optGreater < 0)
        {
            optGreater = Options.Count;
        }
        Options.Insert(optGreater, option);
        return this;
    }

    /**
    * 设置字节流选项值
    *
    * @param define
    * @param optionValue
    */
    public CoAPPackage SetOption(CoAPOptionDefine define, byte[] optionValue)
    {
        CoAPOption option = new CoAPOption()
        {
            Option = define,
            Value = new ArrayByteOptionValue() { Value = optionValue }
        };
        Options.add(option);
        return this;
    }

    /**
    * 设置uint(32)选项值
    *
    * @param define
    * @param optionValue
    */
    public CoAPPackage SetOption(CoAPOptionDefine define, uint optionValue)
    {
        UnsignedIntegerOptionValue v = new UnsignedIntegerOptionValue() { Value = optionValue };
        return SetOption(define, v);
    }

    /**
    * 设置字符串选项值
    *
    * @param define
    * @param optionValue
    */
    public CoAPPackage SetOption(CoAPOptionDefine define, string optionValue)
    {
        StringOptionValue v = new StringOptionValue() { Value = optionValue };
        return SetOption(define, v);
    }

    /**
    * 设置Block1|Block2选项值，此处会作去重处理。设置非Block1|Block2会被忽略掉
    *
    * @param define
    * @param optionValue
    */
    public CoAPPackage SetOption(CoAPOptionDefine define, BlockOptionValue optionValue)
    {
        if (define == CoAPOptionDefine.Block1 || define == CoAPOptionDefine.Block2)
        {
            var opt = Options.Find(x => x.Option == define);
            StringOptionValue v = new StringOptionValue() { Value = optionValue };
            if (opt == null)
            {
                opt = new CoAPOption() { Option = define, Value = v };
            }
            else
            {
                opt.Value = v;
            }

            return SetOption(define, v);
        }
        else
        {
            return this;
        }
    }

    /**
     设置Block1
    * @param optionValue
     */
    public CoAPPackage SetBlock1(BlockOptionValue optionValue)
    {
        return SetOption(CoAPOptionDefine.Block1, optionValue);
    }

    /**
    * 设置Block2
    *
    * @param optionValue
    */
    public CoAPPackage SetBlock2(BlockOptionValue optionValue)
    {
        return SetOption(CoAPOptionDefine.Block2, optionValue);
    }

    /**
    * 设置内容格式类型Content-Format,Http中的Content-Type
    *
    * @param ft
    */
    public CoAPPackage SetContentType(ContentFormat ft)
    {
        return SetOption(CoAPOptionDefine.ContentFormat, ft.Num);
    }
}

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
 * 请求码
*/
public class CoAPRequestMethod : CoAPCode
        {

public static CoAPRequestMethod Get = new CoAPRequestMethod("GET", "", 0, 1);
public static CoAPRequestMethod Post = new CoAPRequestMethod("POST", "", 0, 2);
public static CoAPRequestMethod Put = new CoAPRequestMethod("PUT", "", 0, 3);
public static CoAPRequestMethod Delete = new CoAPRequestMethod("DELETE", "", 0, 4);

        internal CoAPRequestMethod(string name, string description, byte category, byte detail) : base(name, description, category, detail)
        {

        }
        }
/**
* 响应码
*/
public class CoAPResponseCode : CoAPCode
        {

public static CoAPResponseCode Created = new CoAPResponseCode("Created", "Created", 2, 1);
public static CoAPResponseCode Deleted = new CoAPResponseCode("Deleted", "Deleted", 2, 2);
public static CoAPResponseCode Valid = new CoAPResponseCode("Valid", "Valid", 2, 3);
public static CoAPResponseCode Changed = new CoAPResponseCode("Changed", "Changed", 2, 4);
public static CoAPResponseCode Content = new CoAPResponseCode("Content", "Content", 2, 5);

public static CoAPResponseCode Continue = new CoAPResponseCode("Content", "Content", 2, 31);

public static CoAPResponseCode BadRequest = new CoAPResponseCode("BadRequest", "Bad Request", 4, 0);
public static CoAPResponseCode Unauthorized = new CoAPResponseCode("Unauthorized", "Unauthorized", 4, 1);
public static CoAPResponseCode BadOption = new CoAPResponseCode("BadOption", "Bad Option", 4, 2);
public static CoAPResponseCode Forbidden = new CoAPResponseCode("Forbidden", "Forbidden", 4, 3);
public static CoAPResponseCode NotFound = new CoAPResponseCode("NotFound", "Not Found", 4, 4);
public static CoAPResponseCode MethodNotAllowed = new CoAPResponseCode("MethodNotAllowed", "Method Not Allowed", 4, 5);
public static CoAPResponseCode NotAcceptable = new CoAPResponseCode("NotAcceptable", "Not Acceptable", 4, 6);

public static CoAPResponseCode RequestEntityIncomplete = new CoAPResponseCode(" RequestEntityIncomplete", " Request Entity Incomplete", 4, 8);

public static CoAPResponseCode PreconditionFailed = new CoAPResponseCode("PreconditionFailed", "Precondition Failed", 4, 12);
public static CoAPResponseCode RequestEntityTooLarge = new CoAPResponseCode("RequestEntityTooLarge", "Request Entity Too Large", 4, 13);
public static CoAPResponseCode UnsupportedContentFormat = new CoAPResponseCode("UnsupportedContentFormat", "Unsupported Content-Format", 4, 15);
public static CoAPResponseCode InternalServerError = new CoAPResponseCode("InternalServerError", "Internal Server Error", 5, 0);
public static CoAPResponseCode NotImplemented = new CoAPResponseCode("NotImplemented", "Not Implemented", 5, 1);
public static CoAPResponseCode BadGateway = new CoAPResponseCode("BadGateway", "Bad Gateway", 5, 2);
public static CoAPResponseCode ServiceUnavailable = new CoAPResponseCode("ServiceUnavailable", "Service Unavailable", 5, 3);
public static CoAPResponseCode GatewayTimeout = new CoAPResponseCode("GatewayTimeout", "Gateway Timeout", 5, 4);
public static CoAPResponseCode ProxyingNotSupported = new CoAPResponseCode("ProxyingNotSupported", "Proxying Not Supported", 5, 5);

        internal CoAPResponseCode(string name, string description, byte category, byte detail) : base(name, description, category, detail)
        {

        }

        }
