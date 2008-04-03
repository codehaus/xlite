package si.ptb.xlite.converters;

import si.ptb.xlite.XliteException;
import si.ptb.xlite.XMLSimpleReader;
import si.ptb.xlite.MappingContext;

import java.lang.reflect.Field;

/**
 * @author peter
 */
public class NodeMapper {

    private Field targetField;
    public NodeConverter nodeConverter;    //todo make private
    private MappingContext mappingContext;

    public void setValue(Object targetObject, XMLSimpleReader reader) {
        try {
            Object value = nodeConverter.fromNode(reader, mappingContext);
            targetField.set(targetObject, value);
        } catch (IllegalAccessException e) {
            throw new XliteException("Field could not be set!", e);
        }
    }

    public String getValue(Object object) {
        return null;  //Todo Implement body
    }

    public NodeMapper(Field targetField, NodeConverter nodeConverter, MappingContext mappingContext) {
        this.targetField = targetField;
        this.nodeConverter = nodeConverter;
        this.mappingContext = mappingContext;
    }
}
