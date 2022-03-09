package com.mozicoder.iot4j.optionvalues;

import com.mozicoder.iot4j.utils.Hex;

/**
 * 字节数组选项值
 * @author Jason
 * @date 2021/12/29
 */
public class ArrayByteOptionValue extends OptionValue
{
    @Override
    public Object getValue() {
        return _pack;
    }

    @Override
    public void setValue(Object value) {
        _pack = (byte[])value;
    }

    @Override
    public byte[] getPack() {
        return _pack;
    }

    @Override
    public void setPack(byte[] pack) {
        _pack = pack;
    }

    @Override
    public int getLength() {
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
