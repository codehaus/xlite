package si.ptb.xlite.converters;

import si.ptb.xlite.XMLSimpleReader;
import si.ptb.xlite.XMLSimpleWriter;
import si.ptb.xlite.MappingContext;

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

    public Object fromNode(XMLSimpleReader reader, Class targetType, MappingContext mappingContext) {
        return valueConverter.fromValue(reader.getText());
    }

    public void toNode(Object object, XMLSimpleWriter writer, MappingContext mappingContext) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
