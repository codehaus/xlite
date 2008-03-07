package si.ptb.xfast.converters;

import si.ptb.xfast.XfastException;

import javax.xml.stream.XMLStreamReader;
import java.lang.reflect.Field;

/**
 * @author peter
 */
public class NodeMapper {

    private Field targetField;
    public NodeConverter nodeConverter;    //todo make private

    public void setValue(Object targetObject, XMLStreamReader reader) {
        try {
            targetField.set(targetObject, nodeConverter.fromNode(reader));
        } catch (IllegalAccessException e) {
            throw new XfastException("Field could not be set!", e);
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
