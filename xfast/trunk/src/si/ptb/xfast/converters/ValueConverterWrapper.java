package si.ptb.xfast.converters;

import si.ptb.xfast.XfastException;
import si.ptb.xfast.XMLSimpleReader;
import si.ptb.xfast.XMLSimpleWriter;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.util.List;

/**
 * @author peter
 */
public class ValueConverterWrapper implements NodeConverter {

    private List<ValueConverter> valueConverters;
    public ValueConverter choosenValueConverter;    //todo change to private

    public ValueConverterWrapper(List<ValueConverter> valueConverters) {
        this.valueConverters = valueConverters;
    }

    public ValueConverterWrapper(ValueConverterWrapper converterWrapper, ValueConverter valueConverter) {
        this.choosenValueConverter = valueConverter;
        this.valueConverters = converterWrapper.valueConverters;
    }

    public NodeConverter getConverter(Class type) {
        for (ValueConverter valueConverter : valueConverters) {
            if (valueConverter.canConvert(type)) {
                return new ValueConverterWrapper(this, valueConverter);
            }
        }
        return null;
    }

    public Object fromNode(XMLSimpleReader reader) {
        return choosenValueConverter.fromValue(reader.getText());
    }

    public void toNode(Object object, XMLSimpleWriter writer) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
