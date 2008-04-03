package si.ptb.xlite.converters;

import si.ptb.xlite.MappingContext;
import si.ptb.xlite.XMLSimpleReader;
import si.ptb.xlite.XliteException;

/**
 * @author peter
 */
public class RootMapper {

    private String rootNodeName;
    private Class rootClass;
    private MappingContext mappingContext;
    private NodeConverter nodeConverter;


    public RootMapper(String rootNodeName, Class rootClass, MappingContext mappingContext) {
        nodeConverter = mappingContext.lookupNodeConverter(rootClass);
        NodeMapper mapper = new NodeMapper(null, nodeConverter, mappingContext);
        this.rootNodeName = rootNodeName;
        this.mappingContext = mappingContext;
        this.rootClass = rootClass;
    }

    public Object getRootObject(XMLSimpleReader reader) {
        if (reader.findFirstNode(rootNodeName)) {
            return nodeConverter.fromNode(reader, rootClass, mappingContext);
        } else {
            throw new XliteException("Root node <" + rootNodeName + "> could not be found in XML data");
        }

    }

}