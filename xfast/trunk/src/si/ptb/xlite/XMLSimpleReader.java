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
    private Stack<Node> nodeStack = new Stack<Node>();    //todo replace this with NodeQueue (stack with cursor)
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
            nodeStack.peek().text = new StringBuilder();
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
                node.putAttribute(reader.getAttributeName(i),reader.getAttributeValue(i) );
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
        if(nodeStack.isEmpty()){
            return null;
        }
        return nodeStack.peek().text.toString();
    }

    public QName getName() {
        if(nodeStack.isEmpty()){
            return null;
        }
        return nodeStack.peek().name;
    }

    public Iterator<Map.Entry<QName, String>> getAttributeIterator() {
        return nodeStack.peek().iterator();
    }

    public static class Node implements Iterable{
        public QName name;
        public StringBuilder text;
        private Map<QName, String> attributes = new HashMap<QName, String>();

        public void putAttribute(QName qname, String value){
            attributes.put(qname, value);
        }

        public Iterator<Map.Entry<QName, String>>iterator() {
            return new AttributeIterator(attributes.entrySet());
        }
    }

    public static class AttributeIterator implements Iterator<Map.Entry<QName, String>>{
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

    public boolean findFirstNode(String nodeName) {
        while (true) {
            if (nextNodeBoundary(false)) {
                if (reader.getName().getLocalPart().equals(nodeName)) {
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

}
