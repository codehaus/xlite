package si.ptb.xfast.converters;

/**
 * @author peter
 */
public class ByteConverter implements ValueConverter {

    public boolean canConvert(Class type) {
        return type.equals(byte.class) || type.equals(Byte.class);
    }

    public Object fromValue(String value) {
        return Byte.valueOf(value);
    }

    public String toValue(Object object) {
        return ((Byte) object).toString();
    }
}
