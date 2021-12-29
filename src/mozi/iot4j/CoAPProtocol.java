package mozi.iot4j;

import mozi.iot4j.optionvalues.ArrayByteOptionValue;
import mozi.iot4j.optionvalues.OptionValue;

import java.util.ArrayList;

/*
 * CoAP协议
 * @author Jason
 * @date 2021/12/29
 */
public class CoAPProtocol {
    /**
     * 默认服务端口UDP
     */
    public static final int Port = 5683;
    /**
     * 默认DTLS服务端口
     */
    public static final int SecurePort = 5684;
    /**
     * 默认服务端口TCP
     */
    public static final int TcpPort = 5685;
    /**
     * 头部和内容分割数据
     */
    public static final byte HeaderEnd = (byte) 0xFF;
};

/**
 * CoAP头属性
 *
 * @author Jason
 * @date 2021/12/29
 */
public class CoAPOption {
    private Uint32 _lenValue = new Uint32(0);

    private char _lengthExtend, _deltaExtend;

    private byte _delta, _length;

    private OptionValue _optValue;
    /**
     * Delta总值${_delta}+${_deltaExtend}
     */
    private Uint32 _deltaValue;

    /**
     * 首部字节码
     */
    private byte _optionHead;
    /**
     * 选项序号
     */
    private CoAPOptionDefine _option;

    public CoAPOptionDefine getOption() {
        return _option;
    }

    public void setOption(CoAPOptionDefine option) {
        _option = option;
    }

    public byte getOptionHead() {
        return (byte) (((byte) (_delta << 4)) | _length);
    }

    public void setOptionHead(byte head) {
        _delta = (byte) (head >> 4);
        _length = (byte) ((byte) (head << 4) >> 4);
    }


    public Uint32 getDeltaValue() {
        return (Uint32) (_delta + _deltaExtend);
    }

    public void setDeltaValue(Uint32 deltaValue) {
        _deltaValue = deltaValue;
        if (_deltaValue <= 12) {
            _delta = (byte) _deltaValue;
            _deltaExtend = 0;
        } else if (_deltaValue < 269) {
            _delta = 13;
            _deltaExtend = (byte) (_deltaValue - 13);
        } else if (_deltaValue < 65804) {
            _delta = 14;
            _deltaExtend = (char) (_deltaValue - 269);
        }
    }

    /**
     * 长度总值<see cref="Length"/>+<see cref="LengthExtend"/>
     */
    public Uint32 getLengthValue() {
        return (Uint32) (_length + _lengthExtend);
    }

    public void setLengthValue(Uint32 _lengthValue) {
        _lenValue = _lengthValue;
        if (_lenValue <= 12) {
            _length = (byte) _lenValue;
            _lengthExtend = 0;
        } else if (_lenValue < 269) {
            _length = 13;
            _lengthExtend = (byte) (_lenValue - 13);
        } else if (_lenValue < 65804) {
            _length = 14;
            _lengthExtend = (char) (_lenValue - 269);
        }
    }

    /**
     * 偏移值 4bits
     * 取值范围：0-15
     * 保留特殊：
     *    13:  An 8-bit unsigned integer follows the initial byte and indicates the Option Delta minus 13.
     *    14:  A 16-bit unsigned integer in network byte order follows the initial byte and indicates the Option Delta minus 269.
     *    15:  Reserved for the Payload Marker.If the field is set to thisvalue but the entire byte is not the payload marker, this MUST be processed as a message format error.
     */
    public byte getDelta() {
        return _delta;
    }

    public void setDelta(byte delta) {
        _delta = delta;
    }

    /**
     * Delta扩展值
     */
    public char getDeltaExtend() {
        return _deltaExtend;
    }

    public void setDeltaExtend(char deltaExtend) {
        _deltaExtend = deltaExtend;
    }

    /**
     * 值长度 4bits
     * 取值范围：0-15
     *     13:  An 8-bit unsigned integer precedes the Option Value and indicates the Option Length minus 13.
     *     14:  A 16-bit unsigned integer in network byte order precedes the Option Value and indicates the Option Length minus 269.
     *     15:  Reserved for future use.If the field is set to this value, it MUST be processed as a message format error.
     */
    public byte getLength() {
        return _length;
    }

    public void setLength(byte length) {
        _length = length;
    }

    /**
     * Length扩展值
     */
    public char getLengthExtend() {
        return _lengthExtend;
    }

    public void setLengthExtend(char lengthExtend) {
        _lengthExtend = lengthExtend;
    }

    /**
     * 选项值>=0 bytes
     * 空 字节数组 数字 ASCII/UTF-8字符串
     */
    public OptionValue getValue() {
        return _optValue;
    }

    public void setValue(OptionValue optValue) {
        _optValue = optValue;
        _lenValue = optValue != null ? (Uint32) optValue.getLength() : 0;
    }

    private byte[] Pack;

    public byte[] getPack() {
        byte head = (byte) ((byte) (_delta << 4) | _length);

        ArrayList<Byte> data = new ArrayList<Byte>();
        data.add(head);
        //delta
        if (_delta == 14) {
            data.addAll(BitConverter.GetBytes(_deltaExtend));
        } else if (_delta == 13) {
            data.add((byte) _deltaExtend);
        }
        //length
        if (_length == 14) {
            data.addAll(BitConverter.GetBytes(_lengthExtend));
        } else if (_length == 13) {
            data.add((byte) _lengthExtend);
        }
        data.addAll((byte[]) _optValue.getPack());
        return data.toArray();
    }

    public CoAPOption() {
        _optValue = new ArrayByteOptionValue();
    }
}

