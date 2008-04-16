package si.ptb.xlite;

import javax.xml.namespace.QName;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.Map;
import java.util.Iterator;

/**
 * @author peter
 */
public class XMLSimpleWriter {

    private XMLStreamWriter writer;
    private XmlStreamSettings settings = new XmlStreamSettings();
    private boolean firstNode = true;

    public XMLSimpleWriter(XMLStreamWriter writer, XmlStreamSettings settings) {
        this.settings = settings;
        this.writer = writer;
    }

    public void startDocument() {
        try {
            writer.writeStartDocument(settings.encoding, settings.version);
        } catch (XMLStreamException e) {
            throw new XliteException(e);
        }
    }

    public void writeNamespaces(NsContext context){
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
            writer.writeCharacters(text);
        } catch (XMLStreamException e) {
            throw new XliteException(e);
        }
    }

    public void addCDATA(String data) {
        try {
            writer.writeCData(data);
        } catch (XMLStreamException e) {
            throw new XliteException(e);
        }
    }

    public void addComment(String text) {
        try {
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
