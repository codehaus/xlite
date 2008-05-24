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
    private ElementConverter elementConverter;


    public RootMapper(QName rootNodeName, Class rootClass, MappingContext mappingContext) {
        elementConverter = mappingContext.lookupElementConverter(rootClass);
//        ElementMapper mapper = new ElementMapper(null, null, null, elementConverter, mappingContext);
        this.rootNodeName = rootNodeName;
        this.mappingContext = mappingContext;
        this.rootClass = rootClass;
    }

    public Object getRootObject(XMLSimpleReader reader) {
        if (reader.findFirstElement(rootNodeName)) {
            return elementConverter.fromElement(reader, mappingContext);
        } else {
            throw new XliteException("Root node <" + rootNodeName + "> could not be found in XML data");
        }

    }

    public void toXML(Object object, XMLSimpleWriter writer) {
        writer.predefineNamespaces(mappingContext.getPredefinedNamespaces());
        elementConverter.toElement(object, rootNodeName, writer, mappingContext);
    }

}