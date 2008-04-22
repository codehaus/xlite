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
    private XmlStreamSettings settings = new XmlStreamSettings();

    private Stack<Node> nodeStack = new Stack<Node>();
    private boolean isEnd = false;
    private boolean isStoringUnknownNodes;
    private SubTreeStore lastNodeCache = new SubTreeStore(2000, 1000);

    public XMLSimpleReader(XMLStreamReader reader) {
        this(reader, false);
    }

    public XMLSimpleReader(XMLStreamReader reader, boolean isStoringUnknownNodes) {
        this.reader = reader;
        this.isStoringUnknownNodes = isStoringUnknownNodes;
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
    private boolean nextNodeBoundary() {
        return nextNodeBoundary(true);
    }

    private boolean nextNodeBoundary(boolean processText) {

        // reset the accumulated Text
        if (!nodeStack.isEmpty()) {
            StringBuilder sb = nodeStack.peek().text;
            if (sb == null) {
                nodeStack.peek().text = new StringBuilder();
            } else {
                sb.delete(0, sb.length());
            }
        }
        // read stream settings at the beginning of the document
        if (reader.getEventType() == XMLStreamConstants.START_DOCUMENT) {
            settings.encoding = reader.getEncoding();
            settings.version = reader.getVersion();
            settings.isStandalone = reader.isStandalone();
        }

        for (int event = nextEvent(); true; event = nextEvent()) {

            // save the xml events in a cache
            if (isStoringUnknownNodes) {
                // if it's a new node - reset the cache contents
                if (event == XMLStreamConstants.START_ELEMENT) {
//                    System.out.println("cache reset");
                    lastNodeCache.reset();
                }
                System.out.print("cache ");
                processElement(lastNodeCache);
            }
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
            System.out.println("-moveDown() false "+getName());
            return false;
        }
        nextNodeBoundary();
        System.out.println("-moveDown() true "+getName());
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
            System.out.println("-moveUp() "+getName());
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
        System.out.println("-moveUp() "+getName());
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

    public boolean findFirstNode() {
        return findFirstNode((QName) null);
    }

    public boolean findFirstNode(String nodeName) {
        return findFirstNode(new QName(nodeName));
    }

    public boolean findFirstNode(QName qName) {
        // handle empty argument
        if (qName == null || qName.getLocalPart().equals("")) {
            return nextNodeBoundary(false);
        }
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


    public int saveSubTree(SubTreeStore store) {
//        printStore(lastNodeCache,"cache ");

        int pos = store.getPosition();
        int event = reader.getEventType();
        int depth = 0;
        QName qName;
        String name;
        boolean loop = true;
        store.addElement(SubTreeStore.START_BLOCK);
        while (loop) {
            switch (event) {
                case XMLStreamConstants.START_ELEMENT:
                    depth++;
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    if (depth == 0) {
                        loop = false;
                    }
                    depth--;
                    break;
            }
            System.out.print("store ");
            processElement(store);
//            printStore(store,"store ");
            System.out.println("-------------------------");
            event = nextEvent();
        }
        store.addElement(SubTreeStore.END_BLOCK);
        return pos;
    }

    private void processElement(SubTreeStore store) {
        int xmlEventType = reader.getEventType();
        QName qName;
        String name;
        switch (xmlEventType) {
            case XMLStreamConstants.START_DOCUMENT:
//                    settings.encoding = reader.getCharacterEncodingScheme();
                System.out.println("start document");

                store.addElement(XMLStreamConstants.START_DOCUMENT);
                break;
            case XMLStreamConstants.END_DOCUMENT:
                store.addElement(XMLStreamConstants.END_DOCUMENT);
                break;
            case XMLStreamConstants.START_ELEMENT:
                qName = reader.getName();
                name = qName.getPrefix() + "=" + qName.getLocalPart() + "=" + qName.getNamespaceURI();
                System.out.println("start element: " + qName.getLocalPart());
                store.addElement(XMLStreamConstants.START_ELEMENT, name, settings.encoding);
                break;
            case XMLStreamConstants.END_ELEMENT:
                System.out.println("end element: " + reader.getName().getLocalPart());
                store.addElement(XMLStreamConstants.END_ELEMENT);
                break;
            case XMLStreamConstants.ATTRIBUTE:
                System.out.println("attribute: " + reader.getAttributeCount());
                store.addAtributes(reader, settings.encoding);
                break;
            case XMLStreamConstants.NAMESPACE:
                System.out.println("namespace: ");
                store.addNamespaces(reader, settings.encoding);
                break;
            case XMLStreamConstants.CHARACTERS:
                if (!reader.isWhiteSpace()) {
                    System.out.println("characters: " + reader.getText());
                    store.addElement(XMLStreamConstants.CHARACTERS, reader.getText(), settings.encoding);
                }
                break;
//            case XMLStreamConstants.CDATA:
//                store.addElement(XMLStreamConstants.CDATA, reader.getText(), settings.encoding);
//                break;
            case XMLStreamConstants.SPACE:
                System.out.println("space: ");
                store.addElement(XMLStreamConstants.SPACE, reader.getText(), settings.encoding);
                break;
            default:
                System.out.println("other tag: " + reader.getEventType());
        }
    }

    public static void printStore(SubTreeStore store, String name) {

        boolean processingBlocks = true;
        SubTreeStore.Element element = store.getNextElement(0);
        while (element != null) {
            System.out.println(name + " command:" + element.command + " data:" + new String(element.data));
            element = store.getNextElement();
        }
    }

}
