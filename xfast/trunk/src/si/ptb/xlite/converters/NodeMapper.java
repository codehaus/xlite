package si.ptb.xlite.converters;

import si.ptb.xlite.XLiteException;
import si.ptb.xlite.XMLSimpleReader;

import java.lang.reflect.Field;

/**
 * @author peter
 */
public class NodeMapper {

    private Field targetField;
    public NodeConverter nodeConverter;    //todo make private

    public void setValue(Object targetObject, XMLSimpleReader reader) {
        try {
            Object value = nodeConverter.fromNode(reader);
            targetField.set(targetObject, value);
        } catch (IllegalAccessException e) {
            throw new XLiteException("Field could not be set!", e);
        }
    }

    public String getValue(Object object) {
        return null;  //Todo Implement body
    }

    public NodeMapper(Field targetField, NodeConverter nodeConverter) {
        this.targetField = targetField;
        this.nodeConverter = nodeConverter;
    }
}
