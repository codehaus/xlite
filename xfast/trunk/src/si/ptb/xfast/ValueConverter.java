package si.ptb.xfast;

/**
 * User: peter
 * Date: Feb 28, 2008
 * Time: 1:41:08 PM
 */
public interface ValueConverter {

    public boolean canConvert(Class type);

    public Object fromString(String value);

    public String toString(Object object);
}
