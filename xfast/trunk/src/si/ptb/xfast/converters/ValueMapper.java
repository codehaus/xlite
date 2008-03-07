/**
 * User: peter
 * Date: Feb 15, 2008
 * Time: 9:36:39 PM
 */
package si.ptb.xfast.converters;

import java.lang.reflect.Field;

/**
 * Used to get/set values of a Field. It keeps a reference to a Field.
 * Values are internally converted to/from Strings, using assigned converters.
 * @author peter
 */
public class ValueMapper implements FieldConnector {

    public Field targetField;
    public ValueConverter valueConverter;
    public PrimitiveConverter primitiveConverter;
    private boolean isPrimitive;
    private int primitiveType;

    /**
     * Assigns a value to the Field.
     * @param object Instance of an Object that contains the Field.
     * @param elementValue  Value to be set.
     */
    public void setValue(Object object, String elementValue) {
        if (isPrimitive) {
            try {
                primitiveConverter.setPrimitive(primitiveType, targetField, object, elementValue);
            } catch (IllegalAccessException e) {
                e.printStackTrace();  //todo replace with custom exception
            }
        } else {
            try {
                targetField.set(object, valueConverter.fromValue(elementValue));
            } catch (IllegalAccessException e) {
                e.printStackTrace();    //todo replace with custom exception
            }
        }
    }

    /**
     * Reads a value from a Field.
     * @param object Instance of an Object that contains the Field.
     * @return
     */
    public String getValue(Object object) {
        return null;  //Todo implement this
    }

    public ValueMapper(Field targetField, ValueConverter valueConverter) {
        this.targetField = targetField;
        this.valueConverter = valueConverter;
        this.isPrimitive = targetField.getType().isPrimitive();
        if (isPrimitive) {
            primitiveType = PrimitiveConverter.getPrimitiveCode(targetField.getType());
            primitiveConverter = (PrimitiveConverter) valueConverter;
        }
    }

}
