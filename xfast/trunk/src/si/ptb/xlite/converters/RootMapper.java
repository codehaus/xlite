package si.ptb.xlite.converters;

import si.ptb.xlite.MappingContext;
import si.ptb.xlite.XliteException;
import si.ptb.xlite.XMLSimpleReader;

/**
 * @author peter
 */
public class RootMapper extends NodeMapper {

    private String rootNodeName;
    private MappingContext mappingContext;

    public RootMapper(String rootNodeName, NodeConverter nodeConverter, MappingContext mappingContext) {
        super(null, nodeConverter, mappingContext);
        this.rootNodeName = rootNodeName;
        this.mappingContext = mappingContext;
    }

    public Object getRootObject(XMLSimpleReader reader) {
        if (reader.findFirstNode(rootNodeName)) {
            return nodeConverter.fromNode(reader, mappingContext);
        } else {
            throw new XliteException("Root node <" + rootNodeName + "> could not be found in XML data");
        }

    }

}