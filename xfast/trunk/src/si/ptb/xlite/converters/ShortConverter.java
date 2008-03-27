package si.ptb.xlite.converters;

/**
 * @author peter
 */
public class ShortConverter implements ValueConverter {

    public boolean canConvert(Class type) {
        return type.equals(short.class) || type.equals(Short.class);
    }

    public Object fromValue(String value) {
        return Short.valueOf(value);
    }

    public String toValue(Object object) {
        return ((Short) object).toString();
    }
}
