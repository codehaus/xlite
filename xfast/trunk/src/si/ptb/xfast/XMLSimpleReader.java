package si.ptb.xfast;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.Stack;

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
    private Stack<Node> nodeStack = new Stack<Node>(); ;    //todo replace this with NodeQueue (stack with cursor)

    public XMLSimpleReader(XMLStreamReader reader) {
        this.reader = reader;
    }

    private int nextEvent() {
        try {
            int i = reader.next();
            System.out.println("event:" + i);
            return i;
        } catch (XMLStreamException e) {
            throw new XfastException("Error reading XML stream.", e);
        }
    }

    private void move() {
        for (int event = nextEvent(); true; event = nextEvent()) {
            switch (event) {
                case XMLStreamConstants.START_ELEMENT:
                case XMLStreamConstants.START_DOCUMENT:
                    Node node = new Node();
                    node.name = reader.getName();
                    System.out.println("push:"+node.name.getLocalPart());
                    nodeStack.push(node);
                    return;
                case XMLStreamConstants.END_ELEMENT:
                case XMLStreamConstants.END_DOCUMENT:
                    Node poped = nodeStack.pop();
                    System.out.println(" pop:"+poped.name.getLocalPart());
                    nodeStack.pop();
                    return;
                case XMLStreamConstants.CHARACTERS:
                   nodeStack.peek().text.append(reader.getText());
                    System.out.println(" text:"+nodeStack.peek().name.getLocalPart()+" - "+nodeStack.peek().text);
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
        int size = nodeStack.size();
        while(size <= nodeStack.size()){
             move();
        }


        return false;
    }

    /**
     * Moves back from a child node into the parent node.
     * Postions the underlying xml stream to the closing element of the child node.
     */
    public void moveUp() {
        int depth = 1;
        for (int event = reader.getEventType(); true; event = nextEvent()) {
            switch (event) {
                case XMLStreamConstants.START_ELEMENT:
                    System.out.println("up: start " + reader.getName().getLocalPart());
                    depth++;
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    depth--;
                    if (depth == 0) {
                        System.out.println("up: end " + reader.getName().getLocalPart());
                        nextEvent();
                        return;
                    }
                    break;
                case XMLStreamConstants.END_DOCUMENT:
                    System.out.println("up: end document");
                    return;
            }
        }
    }

    public String getText() {
        String text = null;
        StringBuilder textBuilder = null;
        for (int event = reader.getEventType(); true; event = nextEvent()) {
            switch (event) {
                case XMLStreamConstants.CHARACTERS:
                    System.out.println("text: (characters) " + event);

                    if (text == null) { // first text element
                        text = reader.getText();

                    } else if (textBuilder == null) {  // second text element
                        textBuilder = new StringBuilder();
                        textBuilder.append(text).append(reader.getText());

                    } else {  // all other text elements
                        textBuilder.append(reader.getText());
                    }
                    break;
                case XMLStreamConstants.COMMENT: // let the comments pass
                    System.out.println("text: (comment) " + event);
                    break;
                default:
                    System.out.println("text: (other) " + event);
                    return textBuilder != null ? textBuilder.toString() : (text != null ? text : "");
            }
        }
    }

    public QName getName() {
        return reader.getName();
    }

    public int getEventType() {
        return reader.getEventType();
    }

    public int getAttributeCount() {
        return reader.getAttributeCount();
    }

    public QName getAttributeName(int index) {
        return reader.getAttributeName(index);
    }

    public String getAttributeValue(int index) {
        return reader.getAttributeValue(index);
    }

    public static class Node {
        public QName name;
        public StringBuilder text = new StringBuilder();
    }

}
