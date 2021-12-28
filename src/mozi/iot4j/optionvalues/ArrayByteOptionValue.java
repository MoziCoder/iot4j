package mozi.iot4j.optionvalues;

/**
 * 字节数组选项值
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
}
