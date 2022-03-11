package com.mozicoder.iot4j.optionvalues;

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
        public OptionValue setValue(Object value) {
            return this;
        }

        @Override
        public byte[]getPack(){
            return new byte[0];
        }

        @Override
        public OptionValue setPack(byte[] pack) {
            return this;
        }

        @Override
        public int getLength(){
            return 0;
        }

        @Override
        public String toString() {
                return "";
        }
}
