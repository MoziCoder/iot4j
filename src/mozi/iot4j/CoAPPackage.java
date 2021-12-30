package mozi.iot4j;

import mozi.iot4j.optionvalues.*;

import java.io.ByteArrayOutputStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Target(ElementType.PARAMETER)
@interface BlockDefineCategory{};

/**
 * 仿枚举 抽象类
 * @author Jason
 * @date 2021/12/29
*/
public abstract class AbsClassEnum
{
    protected abstract String getTag();
    /// <summary>
    /// 获取方法 不区分标识符大小写
    /// </summary>
    /// <param name="name"></param>
    /// <returns></returns>
    public static AbsClassEnum get(String tag, Class cls)
    {
        //T t = Activator.CreateInstance<T>();
        AbsClassEnum rv;
        try {
            Field[] pis = cls.getDeclaredFields();
            for(Field info:pis)
            {
                info.setAccessible(true);
                //TODO 此处能否得到预期结果

                Object obj=info.get(null);
                if(null!=obj&&(obj.getClass().isInstance( cls.getClass()))){
                      if(((AbsClassEnum)obj).getTag().equals(tag)){
                          rv=(AbsClassEnum)obj;

                      }
                }
            }
        }catch (Exception ex) {
            return null;
        }
    }

//    /// <summary>
//    /// 此处判断标识符是否相等,区分大小写
//    /// <para>
//    ///     如果要判断子对象是否等于<see cref="null"/>，请使用<see cref="object.Equals(object, object)"/>
//    /// </para>
//    /// </summary>
//    /// <param name="obj"></param>
//    /// <returns></returns>
//    public override bool Equals(object obj)
//{
//    return obj is AbsClassEnum && ((AbsClassEnum)obj).Tag.Equals(Tag);
//}
//    /// <summary>
//    /// 重载==
//    /// <para>
//    ///     如果要判断子对象是否等于<see cref="null"/>，请使用<see cref="object.Equals(object, object)"/>
//    /// </para>
//    /// </summary>
//    /// <param name="a"></param>
//    /// <param name="b"></param>
//    /// <returns></returns>
//    public static bool operator ==(AbsClassEnum a, AbsClassEnum b)
//    {
//        return (object)b != null && (object)a != null && a.Tag.Equals(b.Tag);
//    }
//
//    /// <summary>
//    /// 重载!=
//    /// <para>
//    ///     如果要判断子对象是否等于<see cref="null"/>，请使用<see cref="object.Equals(object, object)"/>
//    /// </para>
//    /// </summary>
//    /// <param name="a"></param>
//    /// <param name="b"></param>
//    /// <returns></returns>
//    public static bool operator !=(AbsClassEnum a, AbsClassEnum b)
//    {
//        return (object)a == null || (object)b == null || !a.Tag.Equals(b.Tag);
//    }
//
//    public override int GetHashCode()
//{
//    return Tag.GetHashCode();
//}
}

/**
 * @author Jason
 * @date 2021/12/29
 *
 *      Main Reference:RFC7252
 *      Patial Reference:
 *      RFC7959 分块传输
 *      RFC8613 对象安全
 *      RFC8974 扩展凭据和无状态客户端
 *
 *      内容采用UTF-8编码
 *      头部截断使用0xFF填充
 */
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
        pack.setVersion((byte)(head >> 6));
        pack.setMessageType((CoAPMessageType)AbsClassEnum.get(String.valueOf((byte)(head << 2) >> 4),CoAPMessageType.class));
        pack.setTokenLength((byte)((byte)(head << 4) >> 4));

        pack.setCode(isRequest ? (CoAPCode)AbsClassEnum.get(String.valueOf(data[1]),CoAPRequestMethod.class) : (CoAPCode)AbsClassEnum.get(String.valueOf(data[1]),CoAPResponseCode.class));

        byte[] arrMsgId = new byte[2], arrToken = new byte[pack.getTokenLength()];
        System.arraycopy(data, 2, arrMsgId, 0, 2);
        System.arraycopy(data, 2 + 2, arrToken, 0, arrToken.length);
        pack.setToken( arrToken);

        pack.setMesssageId(ByteStreamUtil.charFromBytes(arrMsgId));
        //3+2+arrToken.Length+1开始是Option部分
        int bodySplitterPos = 2 + 2 + arrToken.length;
        Uint32 deltaSum = new Uint32(0);
        while (bodySplitterPos < data.length && data[bodySplitterPos] != CoAPProtocol.HeaderEnd)
        {

            CoAPOption option = new CoAPOption();
            option.setOptionHead( data[bodySplitterPos]);
            //byte len=(byte)(option.OptionHead)
            int lenDeltaExt = 0, lenLengthExt = 0;
            if (option.getDelta() <= 12)
            {

            }
            else if (option.getDelta() == 13)
            {
                lenDeltaExt = 1;
            }
            else if (option.getDelta() == 14)
            {
                lenDeltaExt = 2;
            }
            if (lenDeltaExt > 0)
            {
                byte[] arrDeltaExt = new byte[2];
                System.arraycopy(data, bodySplitterPos + 1, arrDeltaExt, arrDeltaExt.length - lenDeltaExt, lenDeltaExt);
                option.setDeltaExtend(ByteStreamUtil.charFromBytes(arrDeltaExt));
            }
            //赋默认值
            option.setOption((CoAPOption)AbsClassEnum.get(option.getDeltaValue().plus(deltaSum).toString(),CoAPOptionDefine.class));
            //TODO 此处需要验证Java语言下的执行效果
            if (Object.ReferenceEquals(null, option.getOption()))
            {
                option.setOption(CoAPOptionDefine.Unknown);
            }
            if (option.getLength() <= 12)
            {

            }
            else if (option.getLength() == 13)
            {
                lenLengthExt = 1;
            }
            else if (option.getLength() == 14)
            {
                lenLengthExt = 2;
            }
            if (lenLengthExt > 0)
            {
                byte[] arrLengthExt = new byte[2];
                System.arraycopy(data, bodySplitterPos + 1 + lenDeltaExt, arrLengthExt, arrLengthExt.length - lenLengthExt, lenLengthExt);
                option.setLengthExtend(ByteStreamUtil.charFromBytes(arrLengthExt));
            }

            option.getValue().setValue(new byte[(int) option.getLengthValue().getValue()]);
            System.arraycopy(data, bodySplitterPos + 1 + lenDeltaExt + lenLengthExt, option.getValue().getPack(), 0, option.getValue().getLength());
            pack.Options.add(option);
            deltaSum=new Uint32(option.getDelta());
            //头长度+delta扩展长度+len
            bodySplitterPos += 1 + lenDeltaExt + lenLengthExt + option.getValue().getLength();

        }
        //有效荷载
        if (data.length > bodySplitterPos && data[bodySplitterPos] == CoAPProtocol.HeaderEnd)
        {
            pack.setPayload(new byte[data.length - bodySplitterPos - 1]);

            System.arraycopy(data, bodySplitterPos + 1, pack.getPayload(), 0, pack.getPayload().length);
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
            _token = new byte[tokenLength];
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
    public byte[] Pack()
    {
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        try {
            byte head = 0b00000000;
            head = (byte) (head | (_version << 6));
            head = (byte) (head | (_msgType.getValue() << 4));
            head = (byte) (head | _tokenLength);

            bos.write(head);
            bos.write((byte) (((byte) _code.getCategory() << 5) | ((byte) (_code.getDetail() << 3) >> 3)));
            bos.write(ByteStreamUtil.charToBytes(_msgId));
            bos.write(_token);
            Uint32 delta = new Uint32(0);

            for (CoAPOption op : Options) {
                op.setDeltaValue(new Uint32(op.getOption().getOptionNumber() - delta.getValue()));
                bos.write(op.getPack());
                delta.minus(op.getDeltaValue());
            }
            if (_payload != null) {
                bos.write(CoAPProtocol.HeaderEnd);
                bos.write(_payload);
            }
            return bos.toByteArray();
        }catch (Exception ex){
            return null;
        }
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
        CoAPOption option = new CoAPOption();
        option.setOption(define);
        option.setValue(optionValue);
        var optGreater = Options.FindIndex(x => x.DeltaValue > option.DeltaValue);
        if (optGreater < 0)
        {
            optGreater = Options.size();
        }
        Options.add(optGreater, option);
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
        CoAPOption option = new CoAPOption();
        option.setOption(define);
        var ao=new ArrayByteOptionValue() { };
        ao.setValue(optionValue);
        option.setValue(ao);
        Options.add(option);
        return this;
    }

    /**
    * 设置uint(32)选项值
    *
    * @param define
    * @param optionValue
    */
    public CoAPPackage SetOption(CoAPOptionDefine define, Uint32 optionValue)
    {
        UnsignedIntegerOptionValue v = new UnsignedIntegerOptionValue() { };
        v.setValue((int)optionValue.getValue());
        return SetOption(define, v);
    }

    /**
    * 设置字符串选项值
    *
    * @param define
    * @param optionValue
    */
    public CoAPPackage SetOption(CoAPOptionDefine define, String optionValue)
    {
        StringOptionValue v = new StringOptionValue() { };
        v.setValue(optionValue);
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
            CoAPOption opt = Options.Find(x => x.Option == define);
            StringOptionValue v = new StringOptionValue() {};
            v.setValue(optionValue);
            //TODO 此处判断有问题
            if (opt == null)
            {
                opt = new CoAPOption() { };
                opt.setOption(define);
                opt.setValue(v);
            }
            else
            {
                opt.setValue(v);
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
        return SetOption(CoAPOptionDefine.ContentFormat, new Uint32(ft.getNum()));
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

