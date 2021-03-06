package com.mozicoder.iot4j.optionvalues;

import com.mozicoder.iot4j.utils.Hex;

/**
* 选项值>=0 bytes
* 空 字节数组 数字 ASCII/UTF-8字符串
 * @author Jason
 * @date 2021/12/29
*/
public class OptionValue
{

    protected  byte[] _pack ;

    public   Object getValue(){
        return _pack;
    }

    public  OptionValue setValue(Object value){_pack=(byte[])value;return this;}

    public  byte[] getPack(){return _pack;}

    public  OptionValue setPack(byte[] pack){_pack=pack;return this;};

    public  int getLength(){
        return _pack!=null?_pack.length:0;
    }
    @Override
    public String toString() {
        if(_pack!=null) {
            return Hex.To((byte[]) getValue());
        }else {
            return "";
        }
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