package si.ptb.xlite;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.*;

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

    private XMLStreamReader reader;
    private XmlStreamSettings settings;

    private Stack<Node> nodeStack = new Stack<Node>();
    private boolean isEnd = false;

    public XMLSimpleReader(XMLStreamReader reader) {
        this.reader = reader;
    }

    private int nextEvent() {
        try {
            int i = reader.next();
//            System.out.println("event:" + i);
            return i;
        } catch (XMLStreamException e) {
            throw new XliteException("Error reading XML stream.", e);
        }
    }

    /**
     * Finds next START or END of a XML node.
     * Accumulates the CHARACTER data for the current node.
     *
     * @return True if START, false if END.
     */
    public boolean nextNodeBoundary() {
        return nextNodeBoundary(true);
    }

    public boolean nextNodeBoundary(boolean processText) {

        // reset the accumulated Text
        if (!nodeStack.isEmpty()) {
//            nodeStack.peek().text = new StringBuilder();
            StringBuilder sb = nodeStack.peek().text;
            if (sb == null) {
                nodeStack.peek().text = new StringBuilder();
            } else {
                sb.delete(0, sb.length());
            }
        }

        for (int event = nextEvent(); true; event = nextEvent()) {
            switch (event) {
                case XMLStreamConstants.START_ELEMENT:
//                    System.out.println("start: " + reader.getName());
                    return true;
                case XMLStreamConstants.END_DOCUMENT:
//                    System.out.println("end document ");
                    isEnd = true;
                    return false;
                case XMLStreamConstants.END_ELEMENT:
//                    System.out.println("end: " + reader.getName());
                    return false;
                case XMLStreamConstants.CHARACTERS:
                    if (processText) {
                        nodeStack.peek().text.append(reader.getText());
                    }
//                    System.out.println(" text:" + nodeStack.peek().name.getLocalPart() + " - " + nodeStack.peek().text);
                    break;
            }
        }

    }

    /**
     * Checks if a next child node exists and moves into it.<br><br>
     * Postions the underlying xml stream to the opening element of the child node.
     *
     * @return True if next child node exists, otherwise false.
     */
    public boolean moveDown() {
        int event = reader.getEventType();
        if (event == XMLStreamConstants.START_ELEMENT) {
            Node node = new Node();
            node.name = reader.getName();
            int attrCount = reader.getAttributeCount();
            for (int i = 0; i < attrCount; i++) {
                node.putAttribute(reader.getAttributeName(i), reader.getAttributeValue(i));
            }
//            System.out.println("push:" + node.name.getLocalPart());
            nodeStack.push(node);
        } else {
            if (event != XMLStreamConstants.END_ELEMENT && event != XMLStreamConstants.END_DOCUMENT) {
                throw new XliteException("ERROR: this should be a node END. Instead it's a event=" + event);
            }
//            System.out.println("-moveDown() false "+getName());
            return false;
        }
        nextNodeBoundary();
//        System.out.println("-moveDown() true "+getName());
        return true;
    }

    /**
     * Moves back from a child node into the parent node.
     * Postions the underlying xml stream to the closing element of the child node.
     */
    public void moveUp() {
        if (reader.getEventType() == XMLStreamConstants.END_ELEMENT) {
//            System.out.println("pop:" + nodeStack.peek().name.getLocalPart());
            nodeStack.pop();
            nextNodeBoundary();
//            System.out.println("-moveUp() "+getName());
            return;
        }
        int depth = 1;
        boolean continueLooping = true;
        while (continueLooping) {
            if (nextNodeBoundary()) {  // node START
                depth++;
            } else {      // node END
                if (depth-- == 0) {
                    continueLooping = false;
                    nextNodeBoundary();
                }
            }
        }
//        System.out.println("pop:" + nodeStack.peek().name.getLocalPart());
        nodeStack.pop();
//        System.out.println("-moveUp() "+getName());
    }

    public String getText() {
        if (nodeStack.isEmpty()) {
            return null;
        }
        return nodeStack.peek().text.toString();
    }

    public QName getName() {
        if (nodeStack.isEmpty()) {
            return null;
        }
        return nodeStack.peek().name;
    }

    public Iterator<Map.Entry<QName, String>> getAttributeIterator() {
        return nodeStack.peek().iterator();
    }

    public static class Node implements Iterable {
        public QName name;
        public StringBuilder text;
        private Map<QName, String> attributes = new HashMap<QName, String>();

        public void putAttribute(QName qname, String value) {
            attributes.put(qname, value);
        }

        public Iterator<Map.Entry<QName, String>> iterator() {
            return new AttributeIterator(attributes.entrySet());
        }
    }

    public static class AttributeIterator implements Iterator<Map.Entry<QName, String>> {
        private Iterator<Map.Entry<QName, String>> iterator;

        public AttributeIterator(Set<Map.Entry<QName, String>> entries) {
            this.iterator = entries.iterator();
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }

        public Map.Entry<QName, String> next() {
            return iterator.next();
        }

        public void remove() {
            throw new UnsupportedOperationException("AttributeIterator does not implement method remove().");
        }
    }

    public boolean findFirstNode(QName qName) {
        while (true) {
            if (nextNodeBoundary(false)) {
                if (reader.getName().equals(qName)) {
                    moveDown();
                    return true;
                }
            } else {
                if (isEnd) {
                    return false;
                }
            }
        }
    }


    public int saveSubTree(SubTreeStore store) throws XMLStreamException {
        int pos = store.getPosition();
        QName qName;
        boolean emptyElement = false;
        int emptyElementIndex = 0;
        String name;
        StringBuffer elementText = new StringBuffer();
        for (int event = reader.getEventType(); event != XMLStreamConstants.END_DOCUMENT; event = reader.next()) {
            switch (event) {
                case XMLStreamConstants.START_DOCUMENT:
                    settings.encoding = reader.getCharacterEncodingScheme();
                    store.addElement(XMLStreamConstants.START_DOCUMENT);
                    break;
                case XMLStreamConstants.END_DOCUMENT:
                    store.addElement(XMLStreamConstants.END_DOCUMENT);
                    break;
                case XMLStreamConstants.START_ELEMENT:
                    qName = reader.getName();
                    name = qName.getPrefix().length() == 0 ? qName.getLocalPart() : (qName.getPrefix() + ":" + qName.getLocalPart());
                    store.addElement(XMLStreamConstants.START_ELEMENT, name, settings.encoding);
                    store.addAtributes(reader, settings.encoding);
                    store.addNamespaces(reader, settings.encoding);
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    qName = reader.getName();
                    name = qName.getPrefix().length() == 0 ? qName.getLocalPart() : (qName.getPrefix() + ":" + qName.getLocalPart());
                    store.addElement(XMLStreamConstants.END_ELEMENT, name, settings.encoding);
                case XMLStreamConstants.CHARACTERS:
                    store.addElement(XMLStreamConstants.CHARACTERS, reader.getText(), settings.encoding);
                    break;
                case XMLStreamConstants.CDATA:
                    store.addElement(XMLStreamConstants.CDATA, reader.getText(), settings.encoding);
                    break;
                case XMLStreamConstants.SPACE:
                    store.addElement(XMLStreamConstants.SPACE, reader.getText(), settings.encoding);
                    break;
                default:
                    System.out.println("other tag: " + reader.getEventType());
            }
        }
        return pos;
    }


}
