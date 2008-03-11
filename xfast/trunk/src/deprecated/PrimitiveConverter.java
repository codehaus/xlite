package deprecated;

import si.ptb.xfast.converters.ValueConverter;

import java.lang.reflect.Field;

/**
 * User: peter
 * Date: Mar 1, 2008
 * Time: 11:40:35 PM
 */
public class PrimitiveConverter implements ValueConverter {

    public boolean canConvert(Class type) {
        return type.isPrimitive();
    }

    public Object fromValue(String value) {
        return null;
    }

    public String toValue(Object object) {
        return null;
    }

    //todo Replace all Exceptions with XfastExceptions

    public void setPrimitive(Field field, Object object, String nodeValue) throws IllegalAccessException {
        if (boolean.class.equals(field.getType())) {
            field.setBoolean(object,Boolean.valueOf(nodeValue));
        } else if (byte.class.equals(field.getType())) {
            field.setByte(object,Byte.valueOf(nodeValue));
        } else if (char.class.equals(field.getType())) {
            Character c = Character.valueOf(nodeValue.charAt(0));
            field.setChar(object, c);
        } else if (double.class.equals(field.getType())) {
            field.setDouble(object,Double.valueOf(nodeValue));
        } else if (float.class.equals(field.getType())) {
            field.setFloat(object,Float.valueOf(nodeValue));
        } else if (int.class.equals(field.getType())) {
            field.setInt(object,Integer.valueOf(nodeValue));
        } else if (long.class.equals(field.getType())) {
            field.setLong(object,Long.valueOf(nodeValue));
        } else if (short.class.equals(field.getType())) {
            field.setShort(object,Short.valueOf(nodeValue));
        }
    }

       public void setImmutable(Field field, Object object, String nodeValue) throws IllegalAccessException {
        if (boolean.class.equals(field.getType())) {
            if (nodeValue.equals("true")) {
                field.setBoolean(object, true);
            } else if (nodeValue.equals("false")) {
                field.setBoolean(object, false);
            } else {
                new RuntimeException("PrimitiveConverter: could not set field type boolean.");
            }
        } else if (byte.class.equals(field.getType())) {
            try {
                byte val = Byte.parseByte(nodeValue);
                field.setByte(object, val);
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }
        } else if (char.class.equals(field.getType())) {
            if (nodeValue.length() == 1) {
                field.setChar(object, nodeValue.charAt(0));
            } else if (nodeValue.length() > 1) {
                new RuntimeException("PrimitiveConverter: could not set field type char - element value longer than 1.");
            }
        } else if (double.class.equals(field.getType())) {
            try {
                double val = Double.parseDouble(nodeValue);
                field.setDouble(object, val);
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }
        } else if (float.class.equals(field.getType())) {
            try {
                float val = Float.parseFloat(nodeValue);
                field.setFloat(object, val);
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }
        } else if (int.class.equals(field.getType())) {
            try {
                int val = Integer.parseInt(nodeValue);
                field.setInt(object, val);
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }
        } else if (long.class.equals(field.getType())) {
            try {
                long val = Long.parseLong(nodeValue);
                field.setLong(object, val);
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }
        } else if (short.class.equals(field.getType())) {
            try {
                short val = Short.parseShort(nodeValue);
                field.setShort(object, val);
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }
        }
    }


    public static int getPrimitiveCode(Class clazz) {
        if (boolean.class.equals(clazz)) {
            return 0;
        } else if (byte.class.equals(clazz)) {
            return 1;
        } else if (char.class.equals(clazz)) {
            return 2;
        } else if (double.class.equals(clazz)) {
            return 3;
        } else if (float.class.equals(clazz)) {
            return 4;
        } else if (int.class.equals(clazz)) {
            return 5;
        } else if (long.class.equals(clazz)) {
            return 6;
        } else if (short.class.equals(clazz)) {
            return 7;
        }
        return -1;
    }
}
