package si.ptb.xfast.converters;

/**
 * @author peter
 */
public class IntConverter implements ValueConverter {

    public boolean canConvert(Class type) {
        return type.equals(int.class) || type.equals(Integer.class);
    }

    public Object fromValue(String value) {
        return Integer.valueOf(value);
    }

    public String toValue(Object object) {
        return ((Integer) object).toString();
    }
}
