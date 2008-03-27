package si.ptb.xlite.converters;

/**
 * @author peter
 */
public class FloatConverter implements ValueConverter {

    public boolean canConvert(Class type) {
        return type.equals(float.class) || type.equals(Float.class);
    }

    public Object fromValue(String value) {
        return Float.valueOf(value);
    }

    public String toValue(Object object) {
        return ((Float) object).toString();
    }

}
