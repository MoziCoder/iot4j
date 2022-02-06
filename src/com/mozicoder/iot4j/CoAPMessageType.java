package com.mozicoder.iot4j;

/**
 * 消息类型
 * <br/>
 * <ul>
 *      <li>0-{@CoAPMessageType.Confirmable}</li>
 *      <li>1-{@CoAPMessageType.NonConfirmable}</li>
 *      <li>2-{@CoAPMessageType.Acknowledgement}</li>
 *      <li>3-{@CoAPMessageType.Reset}</li>
 * </ul>
 *
 * @author Jason
 * @date 2021/12/29
 */
public class CoAPMessageType extends AbsClassEnum {

    public static final CoAPMessageType Confirmable = new CoAPMessageType("Confirmable", (byte) 0);
    public static final CoAPMessageType NonConfirmable = new CoAPMessageType("NonConfirmable", (byte) 1);
    public static final CoAPMessageType Acknowledgement = new CoAPMessageType("Acknowledgement", (byte) 2);
    public static final CoAPMessageType Reset = new CoAPMessageType("Reset", (byte) 3);

    private String _name = "";
    private byte _typeValue;

    protected CoAPMessageType(String name, byte typeValue) {
        _name = name;
        _typeValue = typeValue;
    }

    public byte getValue() {
        return _typeValue;
    }

    public String getName() {
        return _name;
    }

    @Override
    protected String getTag() {
        return String.valueOf(_typeValue);
    }
}
