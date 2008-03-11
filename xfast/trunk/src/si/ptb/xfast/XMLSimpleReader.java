package si.ptb.xfast;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.Map;

/**
 * A wrapper around {@link javax.xml.stream.XMLStreamReader}, that simplifies usage. It's no longer necessary to create
 * a loop pulling and processing events. A simple usage example:<br><br>
 * {@code XMLSimpleReader reader = new XMLSimpleReader(xmlStreamReader);}<br>
 * {@code reader.getNodeName();}
 * {@code reader.getAttributes();}
 *
 * @author peter
 */
public class XMLSimpleReader {

    /**
     * Forwards XML stream to the end of current node. This is achieved by skipping all events, including subnodes,
     * until an END_ELEMENT of the current node is reached.
     *
     * @throws XMLStreamException
     */
    public static void skipNode(XMLStreamReader reader) throws XMLStreamException {
        QName nodeToSkip = reader.getName();
        // counting child nodes with the same name
        int count = 0;
        for (int event = reader.next(); true; event = reader.next()) {
            switch (event) {
                case XMLStreamConstants.START_ELEMENT:
                    if (reader.getName().equals(nodeToSkip)) {
                        count++;
                    }
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    if (reader.getName().equals(nodeToSkip)) {
                        if (count == 0) {
                            return;
                        }
                        count--;
                    }
                    break;
            }
        }
    }

    /**
     * Reads first text value from current node and forwards XML stream to the end of current node.
     * This is achieved by skipping all events, including subnodes, until an END_ELEMENT of the current node is reached.
     *
     * @return First text value of the current node.
     * @throws XMLStreamException
     */
    public static String getTextAndSkipNode(XMLStreamReader reader) throws XMLStreamException {
        QName nodeToSkip = reader.getName();
        String text = null;
        // counting child nodes with the same name
        int depth = 0;
        for (int event = reader.next(); true; event = reader.next()) {
            switch (event) {
                case XMLStreamConstants.START_ELEMENT:
                    if (reader.getName().equals(nodeToSkip)) {
                        depth++;
                    }
                    break;
                case XMLStreamConstants.CHARACTERS:
                    if (depth == 0 && reader.getName().equals(nodeToSkip)) {
                        text = reader.getText();
                    }
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    if (reader.getName().equals(nodeToSkip)) {
                        if (depth == 0) {
                            return text;
                        }
                        depth--;
                    }
                    break;
            }
        }
    }


}
