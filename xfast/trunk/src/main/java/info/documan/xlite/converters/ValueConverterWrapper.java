package info.documan.xlite.converters;

import info.documan.xlite.XMLSimpleReader;
import info.documan.xlite.XMLSimpleWriter;
import info.documan.xlite.MappingContext;

import javax.xml.namespace.QName;

/**
 * @author peter
 */
public class ValueConverterWrapper implements NodeConverter {

    public ValueConverter valueConverter;

    public ValueConverterWrapper(ValueConverter valueConverter) {
        this.valueConverter = valueConverter;
    }

    public boolean canConvert(Class type) {
        return valueConverter.canConvert(type);
    }

    public Object fromNode(XMLSimpleReader reader, MappingContext mappingContext) {
        return valueConverter.fromValue(reader.getText());
    }

    public void toNode(Object object, QName nodeName, XMLSimpleWriter writer, MappingContext mappingContext) {
        writer.startNode(nodeName);
        writer.addText(valueConverter.toValue(object));
        writer.endNode();
    }
}
