package si.ptb.xfast;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author peter
 */
public class SimpleReaderTest {

    static String xml0 = "<a>A Value</a>";
    static String xml1 = "<a>AA<b>BB</b></a>";
    static String xml2 = "<a><b></b><c></c></a>";

    static String xml5 = "<root r=\"2\">RRR<sub1>SSS</sub1>S1S1<sub2>S2S2<subsub1 s=\"5\">SS1SS1</subsub1>L2L2</sub2>L1L1</root>";

    @org.testng.annotations.Test
    public void testSimple() throws XMLStreamException {
        StringReader sreader = new StringReader(xml2);
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader parser = factory.createXMLStreamReader(sreader);
        XMLSimpleReader reader = new XMLSimpleReader(parser);

//        reader.moveDown();
//        Node rootNode = processSubNodes(reader);
//        printNode(rootNode, "");

        reader.moveDown();
        reader.getText();
        reader.moveDown();
        reader.getText();
        reader.moveUp();
        reader.getText();
        reader.moveDown();
        reader.getText();
        reader.moveUp();
        reader.moveUp();
        

    }

    public static Node processSubNodes(XMLSimpleReader reader) {
        Node node = new Node(), subNode;
        if (reader.getEventType() == XMLStreamConstants.START_ELEMENT) {
            node.name = reader.getName();
            for (int i = 0; i < reader.getAttributeCount(); i++) {
                node.attributes.put(reader.getAttributeName(i), reader.getAttributeValue(i));
            }
        }
//        node.value = reader.getText();
        while (reader.moveDown()) {
            subNode = processSubNodes(reader);
            node.subnodes.add(subNode);
            reader.moveUp();
        }
        return node;
    }

    public static class Node {
        public QName name;
        public Map<QName, String> attributes = new HashMap<QName, String>();
        public String value;
        List<Node> subnodes = new ArrayList<Node>();
    }

    public static void printNode(Node node, String prefix) {
        System.out.print(prefix + "<" + node.name.getLocalPart());
        for (QName qName : node.attributes.keySet()) {
            System.out.print(" " + qName.getLocalPart() + "=\"" + node.attributes.get(qName) + "\"");
        }
        System.out.println(">");
        if (node.value != null && node.value.length() != 0) {
            System.out.println(prefix + node.value);
        }
        for (Node subnode : node.subnodes) {
            printNode(subnode, prefix + "  ");
        }
        System.out.println(prefix + "</" + node.name.getLocalPart() + ">");

    }

}
