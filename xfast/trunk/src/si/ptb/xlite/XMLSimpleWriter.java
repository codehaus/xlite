package si.ptb.xlite;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.Map;

/**
 * @author peter
 */
public class XMLSimpleWriter {

    private XMLStreamWriter writer;
    private XmlStreamSettings settings = new XmlStreamSettings();
    private boolean firstNode = true;

    public final boolean isPrettyPrinting;
    private StringBuilder tabs = new StringBuilder("\n");

    public XMLSimpleWriter(XMLStreamWriter writer, XmlStreamSettings settings, boolean preetyPrint) {
        this.settings = settings;
        this.writer = writer;
        this.isPrettyPrinting = preetyPrint;
    }

    private void prettyPrint() {
        if (isPrettyPrinting) {
            try {
                writer.writeCharacters(tabs.toString());
            } catch (XMLStreamException e) {
                throw new XliteException(e);
            }
        }
    }

    private void ppIncreaseDepth() {
        if (isPrettyPrinting) {
            tabs.insert(1, "  ");
        }
    }

    private void ppDecreaseDepth() {
        if (isPrettyPrinting) {
            tabs.delete(1, 3);
        }
    }

    public void startDocument() {
        try {
            writer.writeStartDocument(settings.encoding, settings.version);
        } catch (XMLStreamException e) {
            throw new XliteException(e);
        }
    }

    public void writeNamespaces(NsContext context) {
        try {
            for (Map.Entry<String, String> nsEntry : context) {
                writer.writeNamespace(nsEntry.getKey(), nsEntry.getValue());
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    public void setDefaultNamespace(String namespaceURI) {
        try {
            writer.setDefaultNamespace(namespaceURI);
        } catch (XMLStreamException e) {
            throw new XliteException(e);
        }
    }

    public void startNode(QName qname) {
        try {
            prettyPrint();
            ppIncreaseDepth();
            String prefix = qname.getPrefix();
            String localName = qname.getLocalPart();
            String namespaceURI = qname.getNamespaceURI();
            writer.writeStartElement(prefix, localName, namespaceURI);
        } catch (XMLStreamException e) {
            throw new XliteException(e);
        }
    }

    public void endNode() {
        try {
            ppDecreaseDepth();
            prettyPrint();
            writer.writeEndElement();
        } catch (XMLStreamException e) {
            throw new XliteException(e);
        }
    }

    public void addAttribute(QName qname, String value) {
        try {
            String prefix = qname.getPrefix();
            String localPart = qname.getLocalPart();
            String namespaceURI = qname.getNamespaceURI();
            writer.writeAttribute(prefix, namespaceURI, localPart, value);

        } catch (XMLStreamException e) {
            throw new XliteException(e);
        }
    }

    public void addText(String text) {
        try {
            prettyPrint();
            writer.writeCharacters(text);
        } catch (XMLStreamException e) {
            throw new XliteException(e);
        }
    }

    public void addCDATA(String data) {
        try {
            prettyPrint();
            writer.writeCData(data);
        } catch (XMLStreamException e) {
            throw new XliteException(e);
        }
    }

    public void addComment(String text) {
        try {
            prettyPrint();
            writer.writeComment(text);
        } catch (XMLStreamException e) {
            throw new XliteException(e);
        }
    }

    public void addNamespaces(NsContext namespaces) {
        for (Map.Entry<String, String> namespace : namespaces) {
            try {
                writer.writeNamespace(namespace.getKey(), namespace.getValue());
            } catch (XMLStreamException e) {
                throw new XliteException(e);
            }
        }
    }


}
