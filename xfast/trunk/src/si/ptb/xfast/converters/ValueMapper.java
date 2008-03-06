package si.ptb.xfast.converters;

import si.ptb.xfast.converters.ValueConverter;
import si.ptb.xfast.converters.PrimitiveConverter;

import java.lang.reflect.Field;

/**
 * User: peter
 * Date: Feb 15, 2008
 * Time: 9:36:39 PM
 */
public class ValueMapper {

    public Field field;
    private ValueConverter valueConverter;
    private PrimitiveConverter primitiveConverter;
    private String elementName;
    private boolean isPrimitive;
    private int primitiveType;

    public void setValue(Object object, String elementValue) {
        if (isPrimitive) {
            try {
                primitiveConverter.setPrimitive(primitiveType, field, object,  elementValue);
            } catch (IllegalAccessException e) {
                e.printStackTrace();  //todo replace with custom exception
            }
        } else {
            try {
                field.set(object, valueConverter.fromValue(elementValue));
            } catch (IllegalAccessException e) {
                e.printStackTrace();    //todo replace with custom exception
            }
        }
    }

    public ValueMapper(String elementName, Field field, ValueConverter valueConverter) {
        this.elementName = elementName;
        this.field = field;
        this.valueConverter = valueConverter;
        this.isPrimitive = field.getType().isPrimitive();
        if (isPrimitive) {
            primitiveType = PrimitiveConverter.getPrimitiveCode(field.getType());
            primitiveConverter = (PrimitiveConverter) valueConverter;
        }
    }

}
