package si.ptb.fastconverter;

import com.thoughtworks.xstream.converters.UnmarshallingContext;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Comparator;

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
    private boolean isList;

    public FieldMapper(String elementName, Field field) {
        this.elementName = elementName;
        this.field = field;
        this.targetClass = field.getType();
        this.isPrimitive = field.getType().isPrimitive();

        // Field points to a List instance?
        this.isList = List.class.isAssignableFrom(field.getType());

        // element names are unique - they map one-to-one fields
        this.hashCode = elementName.hashCode();
    }

    public void set(Object object, String nodeValue, final UnmarshallingContext context) {
        if (isPrimitive) {
            try {
                field.set(object, nodeValue);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if(isList){
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
