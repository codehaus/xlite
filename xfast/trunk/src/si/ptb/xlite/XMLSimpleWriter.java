package si.ptb.xlite;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
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

    public XMLSimpleWriter(XMLStreamWriter writer, XmlStreamSettings settings, boolean prettyPrint) {
        this.settings = settings;
        this.writer = writer;
        this.isPrettyPrinting = prettyPrint;
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
//                System.out.println("namespace: " + nsEntry.getKey() + "=" + nsEntry.getValue());
                writer.writeNamespace(nsEntry.getKey(), nsEntry.getValue());
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    public void writeNamespace(String prefix, String namespaceURI) {
        try {
            writer.writeNamespace(prefix, namespaceURI);
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

    public void startNode(String prefix, String localName, String namespaceURI) {
        try {
            prettyPrint();
            ppIncreaseDepth();
//            System.out.println("start: " + prefix + ":" + localName + "  ns=" + namespaceURI);
            writer.writeStartElement(prefix, localName, namespaceURI);
        } catch (XMLStreamException e) {
            throw new XliteException(e);
        }
    }

    public void startNode(QName qname) {
        startNode(qname.getPrefix(), qname.getLocalPart(), qname.getNamespaceURI());
    }

    public void endNode() {
        try {
            ppDecreaseDepth();
            prettyPrint();
//            System.out.println("end:");
            writer.writeEndElement();
        } catch (XMLStreamException e) {
            throw new XliteException(e);
        }
    }

    public void endDocument() {
        try {
//            System.out.println("end document:");
            writer.writeEndDocument();
        } catch (XMLStreamException e) {
            throw new XliteException(e);
        }
    }

    public void addAttribute(String prefix, String localName, String namespaceURI, String value) {
        try {
//            System.out.println("attr: " + prefix + ":" + localName + "=" + value + "  ns=" + namespaceURI);
            writer.writeAttribute(prefix, namespaceURI, localName, value);
        } catch (XMLStreamException e) {
            throw new XliteException(e);
        }
    }

    public void addAttribute(QName qname, String value) {
        String prefix = qname.getPrefix();
        String localName = qname.getLocalPart();
        String namespaceURI = qname.getNamespaceURI();
        addAttribute(prefix, localName, namespaceURI, value);
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

    public void restoreSubTrees(SubTreeStore store, Object reference) {
        List<Integer> locations = store.getLocations(reference);
        if (locations != null) {
            for (Integer location : locations) {
                try {
                    restoreSubTree(store, location);
                } catch (XMLStreamException e) {
                    throw new XliteException(e);
                } catch (UnsupportedEncodingException e) {
                    throw new XliteException(e);
                }
            }
        }
    }

    private void restoreSubTree(SubTreeStore store, int location) throws XMLStreamException, UnsupportedEncodingException {

        String prefix, localName, nsURI, value, data;
        String encoding = settings.encoding;  // default encoding
        int first, second, third;

        SubTreeStore.Element element = store.getNextElement(location);
        if (!SubTreeStore.isBlockStart(element)) {
            throw new IllegalArgumentException("Error: XMLSimpleWriter.restoreSubTree was given a wrong location " +
                    "argument: no saved data block is found on given location!");
        }
        while (!SubTreeStore.isBlockEnd(element)) {
            switch (element.command) {
                case XMLStreamConstants.START_DOCUMENT:
                    String header = new String(element.data);
                    String[] headers = header.split("\n");
                    encoding = headers[0].equals("null") ? encoding : headers[0];  // default encoding is UTF-8
//                    System.out.println("Header written: " + headers[0] + " " + headers[1]);
                    startDocument();
                    break;
                case XMLStreamConstants.END_DOCUMENT:
                    endDocument();
                    break;
                case XMLStreamConstants.START_ELEMENT:
                    data = new String(element.data);
                    first = data.indexOf('=');
                    second = data.indexOf('=', first + 1);
                    prefix = data.substring(0, first);
                    localName = data.substring(first + 1, second);
                    nsURI = data.substring(second + 1, data.length());
                    startNode(prefix, localName, nsURI);
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    endNode();
                    break;
                case XMLStreamConstants.ATTRIBUTE:
                    data = new String(element.data);
                    first = data.indexOf('=');
                    second = data.indexOf('=', first + 1);
                    third = data.indexOf('=', second + 1);
                    prefix = data.substring(0, first);
                    localName = data.substring(first + 1, second);
                    nsURI = data.substring(second + 1, third);
                    value = data.substring(third + 1, data.length());
                    addAttribute(prefix, localName, nsURI, value);
                    break;
                case XMLStreamConstants.NAMESPACE:
                    data = new String(element.data);
                    first = data.indexOf('=');
                    prefix = data.substring(0, first);
                    nsURI = data.substring(first + 1, data.length());
                    writeNamespace(prefix, nsURI);
                    break;
                case XMLStreamConstants.CHARACTERS:
                    addText(new String(element.data, encoding));
                    break;
                case XMLStreamConstants.CDATA:
                    addComment(new String(element.data, encoding));
                    break;
//                default:
//                    System.out.println("other: type="+reader.getEventType());
            }
            element = store.getNextElement();
        }
    }

//       public void restoreSubTree2(XMLSimpleWriter writer, Object refeence) throws XMLStreamException, UnsupportedEncodingException {
//
//        String prefix, localName, nsURI, value, data;
//        String encoding = settings.encoding;  // default encoding
//        int first, second, third;
//
//
//        SubTreeStore.Element element = getNextElement();
//        if(element.command != SubTreeStore.START_BLOCK){
//            throw new IllegalArgumentException("Error: XMLSimpleWriter.restoreSubTree was given a wrong location " +
//                    "argument: no saved data block is found on given location!");
//        }
//        while (element.command != SubTreeStore.END_BLOCK) {
//            switch (element.command) {
//                case XMLStreamConstants.START_DOCUMENT:
//                    String header = new String(element.data);
//                    String[] headers = header.split("\n");
//                    encoding = headers[0].equals("null") ? encoding : headers[0];  // default encoding is UTF-8
////                    System.out.println("Header written: " + headers[0] + " " + headers[1]);
//                    writer.startDocument();
//                    break;
//                case XMLStreamConstants.END_DOCUMENT:
//                    writer.endDocument();
//                    break;
//                case XMLStreamConstants.START_ELEMENT:
//                    data = new String(element.data);
//                    first = data.indexOf('=');
//                    second = data.indexOf('=', first + 1);
//                    prefix = data.substring(0, first);
//                    localName = data.substring(first + 1, second);
//                    nsURI = data.substring(second + 1, data.length());
//                    writer.startNode(prefix, localName, nsURI);
//                    break;
//                case XMLStreamConstants.END_ELEMENT:
//                    writer.endNode();
//                    break;
//                case XMLStreamConstants.ATTRIBUTE:
//                    data = new String(element.data);
//                    first = data.indexOf('=');
//                    second = data.indexOf('=', first + 1);
//                    third = data.indexOf('=', second + 1);
//                    prefix = data.substring(0, first);
//                    localName = data.substring(first + 1, second);
//                    nsURI = data.substring(second + 1, third);
//                    value = data.substring(third + 1, data.length());
//                    writer.addAttribute(prefix, localName, nsURI, value);
//                    break;
//                case XMLStreamConstants.NAMESPACE:
//                    data = new String(element.data);
//                    first = data.indexOf('=');
//                    prefix = data.substring(0, first);
//                    nsURI = data.substring(first + 1, data.length());
//                    writer.writeNamespace(prefix, nsURI);
//                    break;
//                case XMLStreamConstants.CHARACTERS:
//                    writer.addText(new String(element.data, encoding));
//                    break;
//                case XMLStreamConstants.CDATA:
//                    writer.addComment(new String(element.data, encoding));
//                    break;
////                default:
////                    System.out.println("other: type="+reader.getEventType());
//            }
//            element = getNextElement();
//        }
//    }
}
