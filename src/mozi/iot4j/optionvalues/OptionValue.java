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