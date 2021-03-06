package com.mozicoder.iot4j.optionvalues;

import com.mozicoder.iot4j.utils.ByteStreamUtil;
import com.mozicoder.iot4j.Logarithm;
import com.mozicoder.iot4j.utils.Uint32;

/**
 * 分块选项 数据结构 适用Block1 Block2 长度为可变长度，可为8bits 16bits 24bits
 * @author Jason
 * @date 2021/12/29
 *
 * 分块选项 数据结构 适用Block1 Block2 长度为可变长度，可为8bits 16bits 24bits
 *
 * Block1|Block2
 *
 * a.描述性用法
 * 描述性用法表示正在传输的数据的大小。
 *     NUM为块序号
 *     M为是否还有更多块
 *     SZX为当前Payload大小
 *
 * 使用方法：
 * 1,Block1 用于请求；Block2用于响应
 * 2,Block1 出现在Request中
 * 3,Block2 出现在Response中
 *
 * b.控制性用法
 * 1,Block2 出现在Request
 *
 * 表示期望服务器常用的传输规格，这是一种协商机制，网络受限情况下通讯包的承载能力受限比较严重
 *
 *      Num为期望的块号，
 *      M无意义，
 *      SZX为期望采用的块大小。取值0表示期望服务端回应该块的大小；取值为非0，就使用上一Response的块大小。
 *
 * 2,Block1 出现在Response 表示接收端正在确认的块信息
 *
 * NUM为正在确认的块序号
 * M为最终响应信息，0表示这是服务端的最终响应，1表示这不是最终响应
 *
 * SZX表明服务端期望接收的块的大小
 *
 * Size1|Size2
 * a.描述性用法
 *      Size1出现在Block1 Request中，用于向服务端指示当前传输的Body的大小
 *      Size2出现在Block2 Response中，用于服务端向客户端指示当前正在响应的资源的大小
 * b.控制性用法
 *      Size1出现在Response中,用于表示服务端期望并能处理的Body大小
 *      Size2出现在Request中，用于客户端向服务器请求Body的大小
 *
 *
 */
public class BlockOptionValue extends OptionValue
{
    /**
     * 块序号 占位4-20bits 长度可以4bits，12bits，20bits
     */
    private Uint32 _num;
    /**
     * 是否最后一个包 占位1bit 倒数第4位
     */
    private boolean _moreFlag;
    /**
     * 数据包总大小 占位3bits 低3位为其储存区间 值大小为1-6，表值范围16bytes-1024bytes 2**(value+4)
     */
    private char _size;

    public Uint32 getNum(){
        return _num;
    }

    public void setNum(Uint32 num){
        _num=num;
    }

    public boolean getMoreFlag(){
        return _moreFlag;
    }

    public void  setMoreFlag(boolean moreFlag){
        _moreFlag=moreFlag;
    }

    public char getSize(){
        return _size;
    }

    public void setSize(char size){
        _size=size;
    }

    /**
     * 解析BlockOptionValue值
     * 字符串格式为 0/0/0 Num/MoreFlag/Size。
     * 例如：
     * 包序号为1，无更多包，分包尺寸大小为128byte
     *     转为字符串： 1/0/128
     * @param value
     * @return
     */
    public static BlockOptionValue Parse(String value)
    {
        BlockOptionValue bv = null;
        String[] pms = value.split("/");
        if (pms.length == 3)
        {
            bv=new BlockOptionValue();
            bv.setNum(new Uint32(Integer.parseInt(pms[0])));
            bv.setMoreFlag(pms[1].equals("1"));
            //这种转换会不会有问题？
            bv.setSize((char)Integer.parseInt(pms[2]));
        }
        return bv;
    }
    public String toString()
    {
        return getPack() == null ? "0/0/0" : String.format("%d/%d/%d", _num.getValue(), (_moreFlag ? 1 : 0), (long)_size);
    }
    /**
     * 属性赋值器无效，因为BlockValue不是由单一要素构造
     */
    @Override
    public Object getValue() {
        return getSize();
    }

    @Override
    public OptionValue setValue(Object value) {
        return this;
    }

    @Override
    public byte[] getPack() {
        byte[] data;
        Uint32 code = new Uint32((_num.getValue() << 4));

        if (_num.lt( 16))
        {
            data = new byte[1];
            //2022/3/7 此处取值错误，现已修正
            data[0] = (byte)code.getValue();
        }
        else if (_num.lt( 4096))
        {
            data = ByteStreamUtil.charToBytes((char)code.getValue());
        }
        else if(_num.lt(1048576))
        {
            data = new byte[3];
            System.arraycopy(ByteStreamUtil.uint32ToBytes(code), 1, data, 0, data.length);
        }else {
            data = new byte[] {(byte) 0xFF, (byte) 0xFF, (byte) 0xF0};
        }
        byte blockLog=(byte) Logarithm.log(_size, 2);
        //最大值为7 11-4
        if (blockLog >= (7 + 4))
        {
            blockLog = 11;
        }
        data[data.length - 1] =(byte)(data[data.length - 1] |(blockLog>=4?(blockLog-4):(byte)0));

        if (_moreFlag)
        {
           data[data.length - 1] =(byte)(data[data.length - 1] | 8);
        }
        return data;
    }

    @Override
    public OptionValue setPack(byte[] pack) {

        _size = (char)Math.pow(2, (byte)(pack[pack.length-1]&0b00000111) + 4);
        _moreFlag = (pack[pack.length-1] & 8) == 8;
        byte[] data = new byte[4];
        System.arraycopy(pack, 0, data, data.length - pack.length, pack.length);
        //此处应右移4位
        _num = new Uint32((ByteStreamUtil.uint32FromBytes(data).getValue()>>4)&0b00001111);
        return  this;
    }

    @Override
    public int getLength() {
        return  getPack() != null ? getPack().length : 0;
    }
}
