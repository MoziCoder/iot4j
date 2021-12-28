package mozi.iot4j.optionvalues;

/**
 * string选项值
 */
public class StringOptionValue extends OptionValue
{
    private byte[] _pack;

    @Override
    public Object getValue() {
        try {
            return new String(_pack,"UTF-8");
        }catch (Exception ex){
           return null;
        }
    }

    @Override
    public void setValue(Object value) {
        try {
            _pack = ((String) value).getBytes("UTF-8");
        }catch (Exception ex){

        }
    }

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
