package si.ptb.xfast.converters;

import javax.xml.stream.XMLStreamReader;

/**
 * @author peter
 */
public class RootMapper extends NodeMapper {

    private String rootNodeName;

    public RootMapper(String rootNodeName, NodeConverter nodeConverter) {
        super(null, nodeConverter);
        this.rootNodeName = rootNodeName;
    }

    public Object getRootObject(XMLStreamReader reader) {
        return nodeConverter.fromNode(reader);
    }
}
