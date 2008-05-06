package si.ptb.xlite.converters;

/**
 * @author peter
 */
public class LongConverter implements ValueConverter {

    public boolean canConvert(Class type) {
        return type.equals(long.class) || Long.class.isAssignableFrom(type);
    }

    public Object fromValue(String value) {
        return Long.valueOf(value);
    }

    public String toValue(Object object) {
        return ((Long) object).toString();
    }
}
