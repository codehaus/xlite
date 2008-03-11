package si.ptb.xfast.converters;

import si.ptb.xfast.XfastException;

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

    public Object fromNode(XMLStreamReader reader) {
        StringBuilder chars = new StringBuilder();
        QName qname;
        String name;

        // we are already inside first child node
        int depth = 1;
        boolean continueLoop = true;
        try {
            for (int event = reader.getEventType(); continueLoop; event = reader.next()) {
                switch (event) {
                    case XMLStreamConstants.START_ELEMENT:
                        depth++;
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        depth--;
                        continueLoop = (depth != 0);
                        break;
                    case XMLStreamConstants.CHARACTERS:
                        // Only collecting value of the top node.
                        // Values of child nodes are ignored.
                        if (depth == 1) {
                            chars.append(reader.getText());
                        }
                        break;
                }
            }
        } catch (XMLStreamException ex) {
            throw new XfastException(ex);
        }

        return choosenValueConverter.fromValue(chars.toString());
    }

    public void toNode(Object object, XMLStreamWriter writer) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
