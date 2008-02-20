package si.ptb.fastconverter;

import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;

/**
 * User: peter
 * Date: Feb 15, 2008
 * Time: 9:36:39 PM
 */
public class FieldMapper {

    public Field field;
    public String elementName;
    public Class targetClass;

    private int hashCode;
    private boolean isPrimitive;
    private boolean isImmutable;
    private boolean isList;

    private int primitiveType = 0;

    public FieldMapper(String elementName, Field field) {
        this.elementName = elementName;
        this.field = field;
        this.targetClass = field.getType();
        this.isPrimitive = field.getType().isPrimitive();
        this.primitiveType = getPrimitiveCode(targetClass);

        // Field points to a List instance?
        this.isList = List.class.isAssignableFrom(field.getType());

        // element names are unique - they map one-to-one to fields
        this.hashCode = elementName.hashCode();
    }

    public void set(Object object, String elementValue, final UnmarshallingContext context) {
        if (isPrimitive) {
            try {
                setPrimitive(object, elementValue);      //todo Introduce valueConverters!
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Attributes can only map to value types."); 
        }
    }

    public void set(Object object, HierarchicalStreamReader reader, final UnmarshallingContext context) {
        if (isPrimitive) {
            try {
                String value = reader.getValue().trim();
                setPrimitive(object, value);      //todo Introduce valueConverters!
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (isList) {
            List list;
            try {
                list = (List) field.get(object);
//                list.add();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            context.convertAnother(object, field.getType());
        }
    }

    private void setPrimitive(Object object, String nodeValue) throws IllegalAccessException {
        if (primitiveType == 1) {// boolean
            if (nodeValue.equals("true")) {
                field.setBoolean(object, true);
            } else if (nodeValue.equals("false")) {

            } else {
                throw new FastConverterException("Error converting XML element value to target type.",
                        elementName, nodeValue, targetClass, field.getName(), field.getType());
            }
        } else if (primitiveType == 2) { // byte
            try {
                byte val = Byte.parseByte(nodeValue);
                field.setByte(object, val);
            } catch (NumberFormatException nfe) {
                throw new FastConverterException("Error converting XML element value to target type.",
                        nfe.getCause(), elementName, nodeValue, targetClass, field.getName(), field.getType());
            }
        } else if (primitiveType == 3) { // char
            if (nodeValue.length() == 1) {
                field.setChar(object, nodeValue.charAt(0));
            } else if (nodeValue.length() > 1) {
                throw new FastConverterException("Error converting XML element value to target type.",
                        elementName, nodeValue, targetClass, field.getName(), field.getType());
            }
        } else if (primitiveType == 4) { // double
            try {
                double val = Double.parseDouble(nodeValue);
                field.setDouble(object, val);
            } catch (NumberFormatException nfe) {
                throw new FastConverterException("Error converting XML element value to target type.",
                        nfe.getCause(), elementName, nodeValue, targetClass, field.getName(), field.getType());
            }
        } else if (primitiveType == 5) { // float
            try {
                float val = Float.parseFloat(nodeValue);
                field.setFloat(object, val);
            } catch (NumberFormatException nfe) {
                throw new FastConverterException("Error converting XML element value to target type.",
                        nfe.getCause(), elementName, nodeValue, targetClass, field.getName(), field.getType());
            }
        } else if (primitiveType == 6) { // integer
            try {
                int val = Integer.parseInt(nodeValue);
                field.setInt(object, val);
            } catch (NumberFormatException nfe) {
                throw new FastConverterException("Error converting XML element value to target type.",
                        nfe.getCause(), elementName, nodeValue, targetClass, field.getName(), field.getType());
            }
        } else if (primitiveType == 7) { // long
            try {
                long val = Long.parseLong(nodeValue);
                field.setLong(object, val);
            } catch (NumberFormatException nfe) {
                throw new FastConverterException("Error converting XML element value to target type.",
                        nfe.getCause(), elementName, nodeValue, targetClass, field.getName(), field.getType());
            }
        } else if (primitiveType == 8) { // short
            try {
                short val = Short.parseShort(nodeValue);
                field.setShort(object, val);
            } catch (NumberFormatException nfe) {
                throw new FastConverterException("Error converting XML element value to target type.",
                        nfe.getCause(), elementName, nodeValue, targetClass, field.getName(), field.getType());
            }
        }
    }

    private int getPrimitiveCode(Class clazz) {
        if (boolean.class.equals(clazz)) {
            return 1;
        } else if (byte.class.equals(clazz)) {
            return 2;
        } else if (char.class.equals(clazz)) {
            return 3;
        } else if (double.class.equals(clazz)) {
            return 4;
        } else if (float.class.equals(clazz)) {
            return 5;
        } else if (int.class.equals(clazz)) {
            return 6;
        } else if (long.class.equals(clazz)) {
            return 7;
        } else if (short.class.equals(clazz)) {
            return 8;
        }
        return 0;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    public static class FieldMapperComparer implements Comparator<FieldMapper> {
        public int compare(FieldMapper f1, FieldMapper f2) {
            return f1.elementName.compareTo(f2.elementName);
        }
    }
}
