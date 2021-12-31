package org.mozi.iot4j.optionvalues;

import org.mozi.iot4j.utils.ByteStreamUtil;
import org.mozi.iot4j.utils.Uint32;

/**
 * uint选项值，.Net的数值类型与网络包数据包排序不同，故字节数组会进行数组翻转
 * @author Jason
 * @date 2021/12/29
 */
public class UnsignedIntegerOptionValue extends OptionValue
{
    @Override
    public Object getValue() {

        byte[] data = new byte[4];
        if (_pack != null)
        {

            System.arraycopy(_pack, 0, data, data.length - _pack.length, _pack.length);

            return ByteStreamUtil.uint32FromBytes(data);
        }
        else
        {
            return 0;
        }

    }

    @Override
    public void setValue(Object value) {

        Uint32 num = new Uint32((Integer)value);
        byte[] data = ByteStreamUtil.uint32ToBytes(num);

        if (num .lt( 256)) //2~8
        {
            _pack = new byte[] { data[0] };
        }
        else if (num.lt( 65536)) //2~16
        {
            _pack = new byte[] { data[0], data[1] };
        }
        else if (num.lt( 16777216)) //2~24
        {
            _pack = new byte[] { data[0], data[1], data[2] };
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
