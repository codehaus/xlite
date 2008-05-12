package info.documan.xlite.converters;

import info.documan.xlite.XMLSimpleWriter;
import info.documan.xlite.XMLSimpleReader;
import info.documan.xlite.*;

import javax.xml.namespace.QName;

/**
 * @author peter
 */
public class RootMapper {

    private QName rootNodeName;
    private Class rootClass;
    private MappingContext mappingContext;
    private NodeConverter nodeConverter;


    public RootMapper(QName rootNodeName, Class rootClass, MappingContext mappingContext) {
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

    public void toXML(Object object, XMLSimpleWriter writer) {
        writer.predefineNamespaces(mappingContext.getPredefinedNamespaces());
        nodeConverter.toNode(object, rootNodeName, writer, mappingContext);
    }

}