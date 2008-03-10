package si.ptb.xfast.converters;

/**
 * @author peter
 */
public class LongConverter implements ValueConverter {

    public boolean canConvert(Class type) {
        return type.equals(long.class) || type.equals(Long.class);
    }

    public Object fromValue(String value) {
        return Long.valueOf(value);
    }

    public String toValue(Object object) {
        return ((Long) object).toString();
    }
}
