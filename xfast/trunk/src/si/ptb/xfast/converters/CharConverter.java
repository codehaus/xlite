package si.ptb.xfast.converters;

/**
 * @author peter
 */
public class CharConverter implements ValueConverter{
    public boolean canConvert(Class type) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Object fromValue(String value) {
         if (value.length() == 0) {
            return new Character('\0');
        } else {
            return new Character(value.charAt(0));
        }
    }

    public String toValue(Object object) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
