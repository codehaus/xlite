package si.ptb.xfast.converters;

import si.ptb.xfast.XfastException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.util.List;
import java.lang.reflect.Field;

/**
 * Wraps {@link ValueConverter} to make it behave as {@link si.ptb.xfast.converters.NodeConverter}. This is useful in situations
 * where xml subnode contains only a value and can be simply converted with one of the ValueConverters.
 * User: peter
 * Date: Mar 3, 2008
 * Time: 11:06:36 AM
 */
public class ConverterWrapper implements NodeConverter {

    private List<ValueConverter> valueConverters;
    private ValueConverter choosenValueConverter;

    public ConverterWrapper(List<ValueConverter> valueConverters) {
        this.valueConverters = valueConverters;
    }

    public ConverterWrapper(ConverterWrapper converterWrapper, ValueConverter valueConverter) {
        this.valueConverters = converterWrapper.valueConverters;
        this.choosenValueConverter = valueConverter;
    }

    public NodeConverter getConverter(Class type) {
        for (ValueConverter valueConverter : valueConverters) {
            if (valueConverter.canConvert(type)) {
                return new ConverterWrapper(this, valueConverter);
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
                        chars.append(reader.getText());
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
