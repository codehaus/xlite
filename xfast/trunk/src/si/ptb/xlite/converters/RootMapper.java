package si.ptb.xlite.converters;

import si.ptb.xlite.XMLSimpleReader;
import si.ptb.xlite.XLiteException;

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
        if(reader.findFirstNode(rootNodeName)){
            return nodeConverter.fromNode(reader);
        } else {
            throw new XLiteException("Root node \""+rootNodeName+"\" could not be found in XML data");
        }

    }

}