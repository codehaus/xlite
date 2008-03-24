package si.ptb.xfast.converters;

import si.ptb.xfast.XMLSimpleReader;
import si.ptb.xfast.XfastException;

/**
 * @author peter
 */
public class RootMapper extends NodeMapper {

    private String rootNodeName;

    public RootMapper(String rootNodeName, NodeConverter nodeConverter) {
        super(null, nodeConverter);
        this.rootNodeName = rootNodeName;
    }

    public Object getRootObject(XMLSimpleReader reader) {
        if(reader.findNode(rootNodeName)){
            return nodeConverter.fromNode(reader);
        } else {
            throw new XfastException("Root node \""+rootNodeName+"\" could not be found in XML data");
        }

    }

}