package mozi.iot4j;

public class Uint32 implements Comparable {

    public final static int SIZE = 32;

    private final static long MIN = 0L;
    private final static long MAX = (1L << SIZE) - 1;

    public final static Uint32 MIN_VALUE = new Uint32(MIN);
    public final static Uint32 MAX_VALUE = new Uint32(MAX);

    private Long value;

    public Uint32() {
        this(0L);
    }

    public Uint32(long value) {
        setValue(value);
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        check(value);
        this.value = value & MAX;
    }

    private void check(long value) {
        if (value < MIN || value > MAX) {
            throw new IllegalArgumentException(value + " 值必须在 " + MIN + " 到 " + MAX + " 之间");
        }
    }

    @Override
    public int hashCode() {
        return ((Long) value).hashCode();
    }

    public String toString() {
        return String.valueOf(value);
    }

    public int compareTo(Uint32 obj) {
        if (obj == null) {
            return -1;
        }
        if (this.value == obj.value) {
            return 0;
        }
        return this.value > obj.value ? 1 : 0;
    }
    public int compareTo(int obj) {
        if (this.value<obj) {
            return -1;
        }else if (this.value>obj){
            return 1;
        }else{
            return 0;
        }
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Uint32 other = (Uint32) obj;
        if (value != other.value)
            return false;
        return true;
    }

    public Uint32 plus(Uint32 that){
        setValue(this.value+that.getValue());
        return this;
    }

    public Uint32 plus(byte that){
        setValue(this.value+that);
        return this;
    }
    public Uint32 minus(Uint32 that){
        setValue(this.value-that.getValue());
        return this;
    }
    public Uint32 minus(byte that){
        setValue(this.value-that);
        return this;
    }
    public Uint32 times(Uint32 that){
        setValue(this.value*that.getValue());
        return this;
    }
    public Uint32 div(Uint32 that){
        setValue(this.value/that.getValue());
        return this;
    }
    public Uint32 rem(Uint32 that){
        setValue(this.value%that.getValue());
        return this;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
