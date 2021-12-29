package mozi.iot4j.optionvalues;

/**
 * 空选项值
 * @author Jason
 * @date 2021/12/29
 */
public class EmptyOptionValue extends OptionValue {

        @Override
        public Object getValue() {
            return null;
        }

        @Override
        public void setValue(Object value) {

        }

        @Override
        public byte[]getPack(){
            return new byte[0];
        }

        @Override
        public void setPack(byte[] pack) {

        }

        @Override
        public int getLength(){
            return 0;
        }
}
