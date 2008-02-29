package si.ptb.xfast;

import java.lang.reflect.Field;

/**
 * ValueConverter implementations are responsible for converting xml node's textual data to Java objects and back.
 * User: peter
 * Date: Feb 29, 2008
 * Time: 12:25:21 AM
 */
public interface ValueConverter {

    /**
     * Indicates if an implementation of Mapper interface can map XML value to a given type.
     */
    public boolean canConvertValue(Class type);

    public Object fromValue(String value);

    public String toValue(Object object);
}
