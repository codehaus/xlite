package si.ptb.xfast;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
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
    static String xml1 = "<a><b><c><d></d></c></b></a>";
    static String xml2 = "<a><b></b><c></c><d></d></a>";

    static String xml5 = "<root r=\"2\">RRR<sub1>SSS</sub1>S1S1<sub2>S2S2<subsub1 s=\"5\">SS1SS1</subsub1>L2L2</sub2>L1L1</root>";

    @org.testng.annotations.Test
    public void testSimple() throws XMLStreamException {
        StringReader sreader = new StringReader(xml5);
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader parser = factory.createXMLStreamReader(sreader);
        XMLSimpleReader reader = new XMLSimpleReader(parser);

        reader.nextNodeBoundary();
//        System.out.println(reader.moveDown());
//        System.out.println(reader.moveDown());
//        reader.moveUp();
        List<Node> rootNode = processSubNodes(reader);
        printNode(rootNode.get(0), "");


    }

    public static List<Node> processSubNodes(XMLSimpleReader reader) {
        List<Node> nodes = new ArrayList<Node>();
        while (reader.moveDown()) {
            Node node = new Node();
            nodes.add(node);
            node.name = reader.getName();
            for (int i = 0; i < reader.getAttributeCount(); i++) {
                node.attributes.put(reader.getAttributeName(i), reader.getAttributeValue(i));
            }
            node.value = reader.getText();
            System.out.println("NODE-"+node.name.getLocalPart());
            List<Node> subNodes = processSubNodes(reader);
            if (!subNodes.isEmpty()) {
                node.subnodes.addAll(subNodes);
            }
            reader.moveUp();
        }
        return nodes;
    }

    public static class Node {
        public QName name;
        public Map<String, String> attributes = new HashMap<String, String>();
        public String value;
        List<Node> subnodes = new ArrayList<Node>();
    }

    public static void printNode(Node node, String prefix) {
        System.out.print(prefix + "<" + node.name.getLocalPart());
        for (String qName : node.attributes.keySet()) {
            System.out.print(" " + qName + "=\"" + node.attributes.get(qName) + "\"");
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
