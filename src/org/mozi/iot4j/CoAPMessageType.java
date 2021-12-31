package org.mozi.iot4j;

/**
 * 消息类型
 * <list type="table">
 * <listheader>取值范围</listheader>
 * <item><term>0</term><see cref="Confirmable"/></item>
 * <item><term>1</term><see cref="NonConfirmable"/></item>
 * <item><term>2</term><see cref="Acknowledgement"/></item>
 * <item><term>3</term><see cref="Reset"/></item>
 * </list>
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
