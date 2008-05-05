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

    public XMLStreamReader reader;  //todo make private
    private XmlStreamSettings settings = new XmlStreamSettings();

    private Stack<Node> nodeStack = new Stack<Node>();
    private boolean isEnd = false;
    private boolean isStoringUnknownNodes;
    private SubTreeStore currentEventCache = new SubTreeStore(200, 200);
    private SubTreeStore lastEventCache;

    private int DP;

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
//             System.out.println("event:" + i);
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

    private boolean nextNodeBoundary(boolean processEvents) {

        // checkAndReset the accumulated Text
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
            settings.encoding = reader.getEncoding() == null ? "UTF-8" : reader.getEncoding();
            settings.version = reader.getVersion() == null ? "1.0" : reader.getEncoding();
        }

        for (int event = nextEvent(); true; event = nextEvent()) {

            // save the xml events in a cache
            if (isStoringUnknownNodes && processEvents) {
                processElement(currentEventCache, "current");
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
                    if (processEvents) {
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
        lastEventCache = currentEventCache;
        currentEventCache = new SubTreeStore(200, 200);
        int event = reader.getEventType();
        if (event == XMLStreamConstants.START_ELEMENT) {
            Node node = new Node();
            node.name = reader.getName();
            int attrCount = reader.getAttributeCount();
            for (int i = 0; i < attrCount; i++) {
                node.putAttribute(reader.getAttributeName(i), reader.getAttributeValue(i));
            }
            nodeStack.push(node);
        } else {
            if (event != XMLStreamConstants.END_ELEMENT && event != XMLStreamConstants.END_DOCUMENT) {
                throw new XliteException("ERROR: this should be a node END. Instead it's a event=" + event);
            }
            String nm = (reader.getEventType() == 1 || reader.getEventType() == 2) ? reader.getName().getLocalPart() : "";
//            System.out.println("-moveDown() false " + nm + "  (" + reader.getEventType() + ":" + nm + ")");
            return false;
        }
        nextNodeBoundary();
        String nm = (reader.getEventType() == 1 || reader.getEventType() == 2) ? reader.getName().getLocalPart() : "";
        DP++;
//        System.out.println("-moveDown() "+DP+" true " + nm + "  (" + reader.getEventType() + ":" + nm + ")");
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
            DP--;
            String nm = (reader.getEventType() == 1 || reader.getEventType() == 2) ? reader.getName().getLocalPart() : "";
//            System.out.println("-moveUp() "+DP+" " + nm + "  (" + reader.getEventType() + ":" + nm + ")");
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
        nodeStack.pop();
        DP--;
        String nm = (reader.getEventType() == 1 || reader.getEventType() == 2) ? reader.getName().getLocalPart() : "";
//        System.out.println("-moveUp() "+depth+" " + nm + "  (" + reader.getEventType() + ":" + nm + ")");
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


    public int saveSubTree(SubTreeStore store, Object object) {
//        printStore(lastEventCache, "LAST");
//        printStore(currentEventCache, "CACHED");
        // save a starting position of this block
        int pos = store.getPosition();
        store.addStart(object);

        // calculate current depth of xml nodes in store
        int depth = lastEventCache.getDepth() + currentEventCache.getDepth();
        int lineNo = reader.getLocation().getLineNumber();
//        System.out.println("depth: " + depth);
//        System.out.println("lineNo: " + lineNo);

        //copy cached xml events from both caches
        store.copyFrom(lastEventCache);
        store.copyFrom(currentEventCache);
        lastEventCache.reset();

        // check if stream is already at the end of subtree (happens when there is only one empty node in a sutree)
        int event = reader.getEventType();
        if (event == XMLStreamConstants.END_ELEMENT) {
            store.addEnd();
//            printLastBlock(store, "STORE");
//            System.out.println("");
            lastEventCache.reset();
            currentEventCache.reset();
            return pos;
        } else {
            event = nextEvent();
        }

        QName qName;
        String name;
        boolean loop = true;
        while (loop) {
            switch (event) {
                case XMLStreamConstants.START_ELEMENT:
                    depth++;
//                    System.out.println("START: " + reader.getName().getLocalPart() + " " + depth);
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    if (--depth == 0) {
                        loop = false;
                    }
//                    depth--;
//                    System.out.println("END: " + reader.getName().getLocalPart() + " " + depth + " loop:" + loop);
                    break;
            }
            processElement(store, "treeStore");
            if (loop) {
                event = nextEvent();
            }
        }
        store.addEnd();
        lastEventCache.reset();
        currentEventCache.reset();
//        printLastBlock(store, "STORE");
//        System.out.println("");
        return pos;
    }

    private void processElement(SubTreeStore store, String desc) {
        int xmlEventType = reader.getEventType();
        QName qName;
        String name;
        switch (xmlEventType) {
            case XMLStreamConstants.START_DOCUMENT:
//                System.out.println("process "+desc+" start document");
                store.addElement(XMLStreamConstants.START_DOCUMENT);
                break;
            case XMLStreamConstants.END_DOCUMENT:
//                System.out.println("process "+desc+" end document");
                store.addElement(XMLStreamConstants.END_DOCUMENT);
                break;
            case XMLStreamConstants.START_ELEMENT:
                qName = reader.getName();
                name = qName.getPrefix() + "=" + qName.getLocalPart() + "=" + "."; //qName.getNamespaceURI();
//                System.out.println("process "+desc+" start element: " + qName.getLocalPart());
                store.addElement(XMLStreamConstants.START_ELEMENT, name, settings.encoding);
                addAtributes(store, settings.encoding);
                addNamespaces(store, settings.encoding);
                break;
            case XMLStreamConstants.END_ELEMENT:
//                System.out.println("process "+desc+" end element: " + reader.getName().getLocalPart());
                store.addElement(XMLStreamConstants.END_ELEMENT, reader.getName().getLocalPart(), settings.encoding);
                break;
            case XMLStreamConstants.CHARACTERS:
                if (!reader.isWhiteSpace()) {
//                    System.out.println("process "+desc+" characters: " + reader.getText());
                    store.addElement(XMLStreamConstants.CHARACTERS, reader.getText(), settings.encoding);
                }
                break;
//            case XMLStreamConstants.CDATA:
//                store.addElement(XMLStreamConstants.CDATA, reader.getText(), settings.encoding);
//                break;
            default:
//                System.out.println("other tag: " + reader.getEventType());
        }
    }

    public void addAtributes(SubTreeStore store, String encoding) {
        QName qName;
        String name;
        for (int i = 0, n = reader.getAttributeCount(); i < n; ++i) {
            qName = reader.getAttributeName(i);
//            name = qName.getPrefix().length() == 0 ? qName.getLocalPart() : (qName.getPrefix() + ":" + qName.getLocalPart());
            name = qName.getPrefix() + "=" + qName.getLocalPart() + "=."; //+ qName.getNamespaceURI();
            store.addElement(XMLStreamConstants.ATTRIBUTE, name + "=" + reader.getAttributeValue(i), encoding);
        }
    }

    public void addNamespaces(SubTreeStore store, String encoding) {
        String uri;
        String prefix;
        for (int i = 0, n = reader.getNamespaceCount(); i < n; ++i) {
            uri = reader.getNamespaceURI(i);
            prefix = reader.getNamespacePrefix(i);
            store.addElement(XMLStreamConstants.NAMESPACE, prefix + "=" + uri, encoding);
        }
    }

    public static void printStore(SubTreeStore store, String name) {

        SubTreeStore.Element element = store.getNextElement(0);
        System.out.print(name + ": ");
        while (element != null) {
            System.out.print(element.command + "(" + new String(element.data) + ") ");
            element = store.getNextElement();
        }
        System.out.println("");
    }

    public static void printLastBlock(SubTreeStore store, String name) {

        SubTreeStore.Element element = store.getNextElement(0);
        int loc = 0;
        while (element != null) {
            if (SubTreeStore.isBlockStart(element)) {
                loc = element.location;
            }
            element = store.getNextElement();
        }

        element = store.getNextElement(loc);
        System.out.print(name + "(" + element.location + "): ");
        while (element != null) {
            System.out.print(element.command + "(" + new String(element.data) + ") ");
            element = store.getNextElement();
        }
        System.out.println("");
    }

}
