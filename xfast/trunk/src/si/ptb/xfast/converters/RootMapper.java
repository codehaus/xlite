package si.ptb.xfast.converters;

import javax.xml.stream.XMLStreamReader;
import java.lang.reflect.Field;

/**
 * @author peter
 */
public class RootMapper extends NodeMapper {

    public RootMapper(Field targetField, NodeConverter nodeConverter) {
        super(targetField, nodeConverter);
    }

    public Object getRootObject(XMLStreamReader reader){
        return nodeConverter.fromNode(reader);
    }
}
