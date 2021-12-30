package mozi.iot4j.optionvalues;

import mozi.iot4j.Uint32;

/**
* 选项值>=0 bytes
* 空 字节数组 数字 ASCII/UTF-8字符串
 * @author Jason
 * @date 2021/12/29
*/
public abstract class OptionValue
{

    protected   Object _value;
    protected  byte[] _pack ;
    protected  int _length ;

    public abstract Object getValue();

    public abstract void setValue(Object value);

    public abstract byte[] getPack();

    public abstract void setPack(byte[] pack) ;

    public abstract int getLength();

}

/**
 * 分块选项 数据结构 适用Block1 Block2 长度为可变长度，可为8bits 16bits 24bits
 * @author Jason
 * @date 2021/12/29
 */
public class BlockOptionValue extends OptionValue
{
    /**
     * 块内位置 占位4-20bits 4 12 20
     */
    private Uint32 _num;
    /**
     * 是否最后一个包 占位1bit 倒数第4位
     */
    private boolean _moreFlag;
    /**
     * 数据包总大小 占位3bits 低3位为其储存区间 值大小为1-6，表值范围16bytes-1024bytes
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

    public  String toString()
    {
        return getPack() == null ? "null" : String.format("%s,Num:%d,M:%d,SZX:%d(bytes)", "Block", _num, (_moreFlag ? 1 : 0), _size);
    }
    /**
     * 属性赋值器无效，因为BlockValue不是由单一要素构造
     */
    @Override
    public Object getValue() {
        return getSize();
    }

    @Override
    public void setValue(Object value) {

    }

    @Override
    public byte[] getPack() {
        byte[] data;
        Uint32 num = (_num.getValue() << 4) | (byte)((byte)Math.log(_size, 2) - 4);
        if (_moreFlag)
        {
            num.setValue(num.getValue() | 8);
        }
        if (_num.lt( 16))
        {
            data = new byte[1];
            data[0] = (byte)_num.getValue();
        }
        else if (_num.lt( 4096))
        {
            data = ByteStreamUtil.charToBytes((char)num.getValue());
        }
        else
        {
            data = new byte[3];
            System.arraycopy(ByteStreamUtil.uint32ToBytes(num), 1, data, 0, data.length);
        }
        return data;
    }

    @Override
    public void setPack(byte[] pack) {

        _size = (char)Math.pow(2, (((byte)(pack[pack.length-1] << 5)) >> 5) + 4);
        _moreFlag = (pack[pack.length-1] & 8) == 8;
        byte[] data = new byte[4];
        System.arraycopy(pack, 0, data, data.length - pack.length, pack.length);
        _num = ByteStreamUtil.uint32FromBytes(data);

    }

    @Override
    public int getLength() {
        return  getPack() != null ? getPack().length : 0;
    }
}
//RFC8974
///**
//* Extended-Token-Length Option 长度0-3 bytes
//*/
//internal class ExtendedTokenLengthOptionValue extends UnsignedIntegerOptionValue
//{
//    public override object Value
//    {
//        get { return base.Value; }
//        set
//        {
//            if ((uint)value< 8){
//                base.Value = 8;
//            }else if((uint)value> 65804)
//            {
//                base.Value = 65804;
//            }
//            else
//            {
//                base.Value = value;
//            }
//        }
//    }
//}