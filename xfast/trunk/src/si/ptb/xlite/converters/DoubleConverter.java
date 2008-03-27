package si.ptb.xlite.converters;

/**
 * @author peter
 */
public class DoubleConverter implements ValueConverter{

    public boolean canConvert(Class type) {
        return type.equals(double.class) || type.equals(Double.class);
    }

    public Object fromValue(String value) {
        return Double.valueOf(value);
    }

    public String toValue(Object object) {
        return ((Double) object).toString();
    }

}
