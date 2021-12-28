package mozi.iot4j.optionvalues;

/**
* 选项值>=0 bytes
* 空 字节数组 数字 ASCII/UTF-8字符串
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
 * uint选项值，.Net的数值类型与网络包数据包排序不同，故字节数组会进行数组翻转
 */
public class UnsignedIntegerOptionValue extends OptionValue
{
    @Override
    public Object getValue() {

        byte[] data = new byte[4];
        if (_pack != null)
        {

            System.arraycopy(_pack, 0, data, data.length - _pack.length, _pack.length);
            return BitConverter.ToUInt32(data.revert(), 0);

        }
        else
        {
            return 0;
        }

    }

    @Override
    public void setValue(Object value) {

        uint num = (uint)value;
        byte[] data = BitConverter.GetBytes(num);

        if (num < 256) //2~8
        {
            _pack = new byte[1] { data[3] };
        }
        else if (num < 65536) //2~16
        {
            _pack = new byte[2] { data[2], data[3] }.Revert();
        }
        else if (num < 16777216) //2~24
        {
            _pack = new byte[3] { data[1], data[2], data[3] }.Revert();
        }
        else {
            _pack = data;
        }

    }

    /**
     * 高位在前，低位在后，且去掉所有高位0x00字节
     */
    @Override
    public byte[] getPack() {
        return _pack;
    }

    @Override
    public void setPack(byte[] pack) {
        _pack=pack;
    }

    @Override
    public int getLength() {
        return _pack != null ? _pack.length : 0;
    }
}

/**
 * 分块选项 数据结构 适用Block1 Block2 长度为可变长度，可为8bits 16bits 24bits
 */
public class BlockOptionValue extends OptionValue
{
    /**
     * 块内位置 占位4-20bits 4 12 20
     */
    private  uint _num;
    /**
     * 是否最后一个包 占位1bit 倒数第4位
     */
    private boolean _moreFlag;
    /**
     * 数据包总大小 占位3bits 低3位为其储存区间 值大小为1-6，表值范围16bytes-1024bytes
     */
    private char _size;

    public uint getNum(){
        return _num;
    }

    public void setNum(uint num){
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

    public  String ToString()
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
        uint num = (_num << 4) | (byte)((byte)Math.log(_size, 2) - 4);
        if (_moreFlag)
        {
            num |= 8;
        }

        if (_num < 16)
        {
            data = new byte[1];
            data[0] = (byte)_num;
        }
        else if (_num < 4096)
        {
            data = BitConverter.GetBytes((char)num).Revert();
        }
        else
        {
            data = new byte[3];
            System.arraycopy(BitConverter.GetBytes(num).Revert(), 1, data, 0, data.length);
        }
        return data;
    }

    @Override
    public void setPack(byte[] pack) {

        _size = (char)Math.pow(2, (((byte)(pack[pack.length-1] << 5)) >> 5) + 4);
        _moreFlag = (pack[pack.length-1] & 8) == 8;
        byte[] data = new byte[4];
        System.arraycopy(value.Revert(), 0, data, data.length - pack.length, pack.length);
        _num = BitConverter.ToUInt32(data, 0);

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