package com.mozicoder.iot4j;

import com.mozicoder.iot4j.optionvalues.*;
import com.mozicoder.iot4j.utils.ByteStreamUtil;
import com.mozicoder.iot4j.utils.StringUtil;
import com.mozicoder.iot4j.utils.Uint32;
import com.mozicoder.iot4j.utils.UriInfo;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * CoAP协议包
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
    private ArrayList<CoAPOption> _options = new ArrayList<CoAPOption>();
    private byte _version = 1;
    private CoAPMessageType _msgType;
    private CoAPCode _code;
    private char _msgId;
    private byte[] _token,_payload;
    private CoAPPackageType _packType=CoAPPackageType.Request;

    //TODO Java byte 取值范围等同于C中的char,故此处要进行转换
    /**
    * 解析数据包
    * @param data 数据包
    * @param packType 是否请求
    */
    public static CoAPPackage parse(byte[] data, CoAPPackageType packType)
    {
        CoAPPackage pack = new CoAPPackage();
        byte head = data[0];
        pack.setVersion((byte)(head >> 6));
        //TODO Java只有有符号的数值类型，此处的方法是直接对高位进行&运算
        pack.setMessageType((CoAPMessageType)AbsClassEnum.get(String.valueOf(((byte)(head<<2)&0xC0)>>6),CoAPMessageType.class));
        pack.setTokenLength((byte)((byte)(head << 4) >> 4));

        pack.setCode(packType==CoAPPackageType.Request ? (CoAPCode)AbsClassEnum.get(String.valueOf(data[1]),CoAPRequestMethod.class) : (CoAPCode)AbsClassEnum.get(String.valueOf(data[1]),CoAPResponseCode.class));
        pack.setPackageType(packType);
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
            option.setOption((CoAPOptionDefine) AbsClassEnum.get(option.getDeltaValue().plus(deltaSum).toString(),CoAPOptionDefine.class));
            //TODO 此处需要验证Java语言下的执行效果

            if (null==option.getOption())
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
            pack._options.add(option);
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
     * 设置包类型
     * @param packType
     */
    private void setPackageType(CoAPPackageType packType) {
        _packType=packType;
    }

    /**
     * 获取包类型 {@CoAPPackageType.Request}|{@CoAPPackageType.Response}
     * @return
     */
    public  CoAPPackageType getPackageType(){
        return _packType;
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

    //TODO 实现RFC8974
    /**
     * RFC7252定义：
     *
     * Token长度 4bits
     * 0-8bytes取值范围
     * 9-15为保留使用
     *
     * RFC8974定义
     * 13-指示TokenLength>8  Token=4+8bits  TKL-Ext=Token.Lenght-13
     * 14-指示TokenLength>269 Token=4+16bits TKL-Ext=Token.Length-269
     * 15-报错
     *
     *
     */
    public byte getTokenLength() {
        return (byte)(_token == null ? 0 : _token.length);
    }

    //TODO 实现RFC8974
    /**
     * RFC7252定义：
     *
     * Token长度 4bits
     * 0-8bytes取值范围
     * 9-15为保留使用
     *
     * RFC8974定义
     * 13-指示TokenLength>8  Token=4+8bits  TKL-Ext=Token.Lenght-13
     * 14-指示TokenLength>269 Token=4+16bits TKL-Ext=Token.Length-269
     * 15-报错
     *
     *
     */
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
    public byte[] pack()
    {
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        try {
            byte head = 0b00000000;
            head = (byte) (head | (_version << 6));
            head = (byte) (head | (_msgType.getValue() << 4));
            head = (byte) (head | getTokenLength());

            bos.write(head);
            bos.write((byte) (((byte) _code.getCategory() << 5) | ((byte) (_code.getDetail() << 3) >> 3)));
            bos.write(ByteStreamUtil.charToBytes(_msgId));
            bos.write(_token);
            Uint32 delta = new Uint32(0);

            for (CoAPOption op : _options) {

                op.setDeltaValue(new Uint32((long)op.getOption().getOptionNumber() - delta.getValue()));
                bos.write(op.getPack());
                delta.plus(op.getDeltaValue());

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
    * @param define
    */
    public CoAPPackage setOption(CoAPOptionDefine define)
    {
        return setOption(define, new EmptyOptionValue());
    }

    //DONE 此处方法有问题
    /**
    * 设置选项值，此方法可以设置自定义的选项值类型
    * @param define
    * @param optionValue
    */
    public CoAPPackage setOption(CoAPOptionDefine define, OptionValue optionValue)
    {
        CoAPOption option = new CoAPOption();
        option.setOption(define);
        option.setValue(optionValue);
        int optGreater=0;

        for (CoAPOption op: _options) {
            if(op.getOption().getOptionNumber()>(define.getOptionNumber())){
                optGreater= _options.indexOf(op);
            }
            //var optGreater = Options.FindIndex(x => x.DeltaValue > option.DeltaValue);
        }
        if (optGreater < 0)
        {
            optGreater = _options.size();
        }
        _options.add(optGreater, option);
        return this;
    }
    /**
    * 设置字节流选项值
    * @param define
    * @param optionValue
    */
    public CoAPPackage setOption(CoAPOptionDefine define, byte[] optionValue)
    {
        ArrayByteOptionValue ao=new ArrayByteOptionValue() { };
        ao.setValue(optionValue);
        setOption(define,ao);
        return this;
    }
    /**
    * 设置uint(32)选项值
    * @param define
    * @param optionValue
    */
    public CoAPPackage setOption(CoAPOptionDefine define, Uint32 optionValue)
    {
        UnsignedIntegerOptionValue v = new UnsignedIntegerOptionValue() { };
        v.setValue((int)optionValue.getValue());
        return setOption(define, v);
    }
    /**
    * 设置字符串选项值
    * @param define
    * @param optionValue
    */
    public CoAPPackage setOption(CoAPOptionDefine define, String optionValue)
    {
        StringOptionValue v = new StringOptionValue() { };
        v.setValue(optionValue);
        return setOption(define, v);
    }
    /**
    * 设置Block1|Block2选项值，此处会作去重处理。设置非Block1|Block2会被忽略掉
    * @param define
    * @param optionValue
    */
    public CoAPPackage setOption(CoAPOptionDefine define, BlockOptionValue optionValue)
    {
        if (define == CoAPOptionDefine.Block1 || define == CoAPOptionDefine.Block2)
        {
            CoAPOption opt = null;
            for (CoAPOption op: _options){
                if(op.getOption()==define){
                    opt=op;
                }
            }
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

            return setOption(define, v);
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
    public CoAPPackage setBlock1(BlockOptionValue optionValue)
    {
        return setOption(CoAPOptionDefine.Block1, optionValue);
    }
    /**
    * 设置Block2
    *
    * @param optionValue
    */
    public CoAPPackage setBlock2(BlockOptionValue optionValue)
    {
        return setOption(CoAPOptionDefine.Block2, optionValue);
    }
    /**
    * 设置内容格式类型Content-Format,Http中的Content-Type
    *
    * @param ft
    */
    public CoAPPackage setContentType(ContentFormat ft)
    {
        return setOption(CoAPOptionDefine.ContentFormat, new Uint32(ft.getNum()));
    }

    /**
     * 获取域名信息，{@CoAPOptionDefine.UriHost}
     * @return
     */
    public String getDomain(){
        String domain = "";
        for (CoAPOption op: _options)
        {
            if (op.getOption() == CoAPOptionDefine.UriHost)
            {
                StringOptionValue opv=new StringOptionValue();
                opv.setPack(op.getPack());
                domain = (String)(opv.getValue());
            }
        }
        return domain;
    }

    /**
     * 获取路径信息，{@CoAPOptionDefine.UriPath}
     * @return
     */
    public String getPath(){
        StringBuilder path = new StringBuilder();
        for(CoAPOption op: _options)
        {
            if (op.getOption() == CoAPOptionDefine.UriPath)
            {
                StringOptionValue opv=new StringOptionValue();
                opv.setPack(op.getPack());
                path.append("/").append((String) opv.getValue());
            }
        }
        return path.toString();
    }

    /**
     * 获取查询字符串，{@CoAPOptionDefine.UriQuery}
     * @return
     */
    public String getQuery(){
        ArrayList<String> query = new ArrayList<String>();
        for (CoAPOption op: _options)
        {
            if (op.getOption() == CoAPOptionDefine.UriQuery)
            {
                StringOptionValue opv=new StringOptionValue();
                opv.setPack(op.getPack());
                query.add((String)(opv.getValue()));
            }
        }
        return String.join("&",query);
    }
    /**
     * 将URI信息配置到包中，即domain,port,paths,queries注入到"Options"中
     * 自动注入的Option:
     *  <ul>
     *      <li>{@CoAPOptionDefine.UriHost}如果URL中的主机地址为域名，则注入此Option</li>
     *      <li>{@CoAPOptionDefine.UriPort}</li>
     *      <li>{@CoAPOptionDefine.UriPath}以'/'分割Option</li>
     *      <li>{@CoAPOptionDefine.UriQuery}以'&'分割Option</li>
     *  </ul>
     * @param uri
     * @return
     */
    public CoAPPackage setUri(UriInfo uri) {

        //注入域名
        if (!StringUtil.isNullOrEmpty(uri.Domain)) {
            setOption(CoAPOptionDefine.UriHost, uri.Domain);
        }
        //注入端口号
        if (uri.Port > 0 && !(uri.Port == CoAPProtocol.Port || uri.Port == CoAPProtocol.SecurePort)) {
            setOption(CoAPOptionDefine.UriPort, new Uint32(uri.Port));
        }
        //注入路径
        for (int i = 0; i < uri.Paths.length; i++) {
            setOption(CoAPOptionDefine.UriPath, uri.Paths[i]);
        }
        //注入查询参数
        for (int i = 0; i < uri.Queries.length; i++) {
            setOption(CoAPOptionDefine.UriQuery, uri.Queries[i]);
        }
        return this;
    }
    public CoAPPackage(){

    }

    /**
     * 带参数实例化，最小参数量实例化
     * @param method
     * @param token
     * @param msgId
     * @param msgType
     */
    public CoAPPackage(CoAPRequestMethod method,byte[] token,char msgId,CoAPMessageType msgType)
    {
        _code = method;
        _token = token;
        _msgId = msgId;
        _msgType = msgType;
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

