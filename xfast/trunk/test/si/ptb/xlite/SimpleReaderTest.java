package si.ptb.xlite;

import org.testng.Assert;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.StringReader;
import java.util.*;

/**
 * @author peter
 */
public class SimpleReaderTest {

    private XMLSimpleReader getReader(String xmlString) throws XMLStreamException {
        StringReader sreader = new StringReader(xmlString);
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader parser = factory.createXMLStreamReader(sreader);  // todo make this a part of SimpleReader.getInstance()
        return new XMLSimpleReader(parser);
    }

    static String xml1 = "<a><b><c><d attr=\"DDD\" /></c></b></a>";

    @org.testng.annotations.Test
    public void emptyElementWithAttributeTest() throws XMLStreamException {
        XMLSimpleReader reader = getReader(xml1);
        reader.nextNodeBoundary();
        Node rootNode = processSubNodes(reader).get(0);
//        printNodes(rootNode, "");
        Assert.assertEquals(rootNode.name.getLocalPart(), "a");
        Assert.assertEquals(rootNode.subnodes.get(0).name.getLocalPart(), "b");
        Assert.assertEquals(rootNode.subnodes.get(0).subnodes.get(0).name.getLocalPart(), "c");
        Assert.assertEquals(rootNode.subnodes.get(0).subnodes.get(0).subnodes.get(0).name.getLocalPart(), "d");
    }

    static String xml2 = "<a><b/><c/><d></d></a>";

    @org.testng.annotations.Test
    public void simpleTest2() throws XMLStreamException {
        XMLSimpleReader reader = getReader(xml2);
        reader.nextNodeBoundary();
        Node rootNode = processSubNodes(reader).get(0);
        printNodes(rootNode, "");
        Assert.assertEquals(rootNode.name.getLocalPart(), "a");
        Assert.assertEquals(rootNode.subnodes.get(0).name.getLocalPart(), "b");  // first subnode of <a>
        Assert.assertEquals(rootNode.subnodes.get(1).name.getLocalPart(), "c");
        Assert.assertEquals(rootNode.subnodes.get(2).name.getLocalPart(), "d");
    }

    static String xml3 = "<a>1<b>2</b>3</a>";

    @org.testng.annotations.Test
    public void textTest() throws XMLStreamException {
        XMLSimpleReader reader = getReader(xml3);
        reader.nextNodeBoundary();
        reader.moveDown();
        Assert.assertEquals(reader.getName().getLocalPart(), "a");  // inside <a>
        Assert.assertEquals(reader.getText(), "1");
        reader.moveDown();
        Assert.assertEquals(reader.getName().getLocalPart(), "b"); // inside <b>
        Assert.assertEquals(reader.getText(), "2");
        Assert.assertTrue(!reader.moveDown()); // there are no child nodes under <b>
        reader.moveUp();
        Assert.assertEquals(reader.getName().getLocalPart(), "a");  // back to <a>
        Assert.assertEquals(reader.getText(), "3");
        reader.moveUp();
    }

    static String xml4 = "<a><b><c></c><d></d></b></a>";

    @org.testng.annotations.Test
    public void skippingChildNodesTest() throws XMLStreamException {
        XMLSimpleReader reader = getReader(xml4);
        reader.nextNodeBoundary();
        reader.moveDown();
        Assert.assertEquals(reader.getName().getLocalPart(), "a");  // inside <a>
        reader.moveDown();  // down two times
        reader.moveDown();
        Assert.assertEquals(reader.getName().getLocalPart(), "c");  // inside child <c>
        reader.moveUp();  //moving up twice should position reader back into <a>
        reader.moveUp();
        Assert.assertEquals(reader.getName().getLocalPart(), "a");  // back inside <a>

    }

    //todo FINISH this test!!!   This test should ignore "other" nodes - CDATA, comments, DTD, Entity Reference, Processing Instruction
    static String xml5 = "";

    @org.testng.annotations.Test
    public void ignoringOtherNodesTest() throws XMLStreamException {
        XMLSimpleReader reader = getReader(xml4);
        reader.nextNodeBoundary();
        reader.moveDown();

    }

    static String xml6 = "<a><b1>B1</b1><c><d><e/></d></c><b2>B2</b2></a>";

    @org.testng.annotations.Test
    public void skippedNodesTest() throws XMLStreamException {
        XMLSimpleReader reader = getReader(xml6);
        reader.nextNodeBoundary();

        // inside a
        Assert.assertTrue(reader.moveDown());
        Assert.assertEquals(reader.getName().getLocalPart(), "a");

        // inside b1
        Assert.assertTrue(reader.moveDown());
        Assert.assertEquals(reader.getName().getLocalPart(), "b1");
        Assert.assertEquals(reader.getText(), "B1");
        reader.moveUp();

        // inside c
        Assert.assertTrue(reader.moveDown());
        Assert.assertEquals(reader.getName().getLocalPart(), "c");
        // skip d and e entirelly
        reader.moveUp();

        // inside b2
        Assert.assertTrue(reader.moveDown());
        Assert.assertEquals(reader.getName().getLocalPart(), "b2");
        Assert.assertEquals(reader.getText(), "B2");
        reader.moveUp();

        // back to a
        Assert.assertEquals(reader.getName().getLocalPart(), "a");
        reader.moveUp();


    }

     @org.testng.annotations.Test
    public void anotherTest() throws XMLStreamException {
        XMLSimpleReader reader = getReader(SampleXml.xml);
         reader.findFirstNode("one"); // first node we start with
         Node rootNode = processSubNodes(reader).get(0);
    }

    public static List<Node> processSubNodes(XMLSimpleReader reader) {
        List<Node> nodes = new ArrayList<Node>();
        while (reader.moveDown()) {
            Node node = new Node();
            nodes.add(node);
            node.name = reader.getName();
            Iterator<Map.Entry<QName, String>> attrIterator = reader.getAttributeIterator();
            while(attrIterator.hasNext()){
                Map.Entry<QName, String> entry = attrIterator.next();
                node.attributes.put(entry.getKey(), entry.getValue());
            }
//            System.out.println("NODE-"+node.name.getLocalPart());
            List<Node> subNodes = processSubNodes(reader);
            if (!subNodes.isEmpty()) {
                node.subnodes.addAll(subNodes);
            }
            node.value = reader.getText();
            reader.moveUp();
        }
        return nodes;
    }

    public static class Node {
        public QName name;
        public Map<QName, String> attributes = new HashMap<QName, String>();
        public String value;
        List<Node> subnodes = new ArrayList<Node>();
    }

    public static void printNodes(Node node, String prefix) {
        System.out.print(prefix + "<" + node.name.getLocalPart());
        for (QName qName : node.attributes.keySet()) {
            System.out.print(" " + qName + "=\"" + node.attributes.get(qName) + "\"");
        }
        System.out.println(">");
        if (node.value != null && node.value.length() != 0) {
            System.out.println(prefix + node.value);
        }
        for (Node subnode : node.subnodes) {
            printNodes(subnode, prefix + "  ");
        }
        System.out.println(prefix + "</" + node.name.getLocalPart() + ">");

    }

}
