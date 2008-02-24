package si.ptb.xfast;

import javax.xml.namespace.QName;
import javax.xml.stream.*;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * This class just compares two XML documents to see if they are equal.
 * <p/>
 * User: peter
 * Date: Feb 24, 2008
 * Time: 12:46:34 AM
 */
public class XmlComparer {  //TODO Finish this FIRST!!

    public static List<CompareResult> compare(Reader xml1, Reader xml2) throws XMLStreamException {
        List<CompareResult> results = new ArrayList<CompareResult>();

        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader r1 = factory.createXMLStreamReader(xml1);
        XMLStreamReader r2 = factory.createXMLStreamReader(xml2);

        QName q1, q2;
        String n1, n2;
        String enclosingNode = null;
        StringBuilder t1 = new StringBuilder();
        StringBuilder t2 = new StringBuilder();
        for (int e1 = r1.getEventType(), e2 = r2.getEventType();
             e1 != XMLStreamConstants.END_DOCUMENT && e2 != XMLStreamConstants.END_DOCUMENT;
             e1 = r1.next(), e2 = r2.next()) {

            Location location = r1.getLocation();

            // nodes don't match
            if (e1 != e2) {
                System.out.println("MISMATCH " + e1 + "-" + e2 +"  "+ location.getLineNumber() + ":" + location.getColumnNumber());
                break;
            }

            switch (e1) {
                case XMLStreamConstants.START_DOCUMENT:
                    String h1 = r1.getEncoding() + "\n" + r1.getVersion() + "\n" + r1.isStandalone();
                    String h2 = r2.getEncoding() + "\n" + r2.getVersion() + "\n" + r2.isStandalone();
//                    System.out.println("START_DOCUMENT ");
//                    System.out.println("   " + h1);
//                    System.out.println("   " + h2);
                    break;
                case XMLStreamConstants.END_DOCUMENT:

                    break;
                case XMLStreamConstants.START_ELEMENT:
                    q1 = r1.getName();
                    n1 = q1.getPrefix().isEmpty() ? q1.getLocalPart() : (q1.getPrefix() + ":" + q1.getLocalPart());
                    q2 = r2.getName();
                    n2 = q2.getPrefix().isEmpty() ? q2.getLocalPart() : (q2.getPrefix() + ":" + q2.getLocalPart());
                    enclosingNode = n1;
                    if (!n1.equals(n2)) {
                        System.out.println("START_ELEMENT " + location.getLineNumber() + ":" + location.getColumnNumber());
                        System.out.println("   " + n1 + " != " + n2);
                        break;
                    } else {
                        System.out.println("OK " + n1 + "  " + location.getLineNumber() + ":" + location.getColumnNumber());
                    }
                    compareAtributes(n1, r1, r2);
                    compareNamespaces(n1, r1, r2);
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    q1 = r1.getName();
                    n1 = q1.getPrefix().isEmpty() ? q1.getLocalPart() : (q1.getPrefix() + ":" + q1.getLocalPart());


                    if (!t1.toString().equals(t2.toString())) {
                        System.out.println("TEXT enclosingNode=" + enclosingNode + "  " + location.getLineNumber() + ":" + location.getColumnNumber());
                        System.out.println(t1);
                        System.out.println("--------------------");
                        System.out.println(t2);
                    }
                    t1 = new StringBuilder();
                    t2 = new StringBuilder();

                    break;
                case XMLStreamConstants.CHARACTERS:
                    t1.append(r1.getText());
                    t2.append(r2.getText());
                    break;
                case XMLStreamConstants.CDATA:
                    break;
                case XMLStreamConstants.SPACE:
                    break;
            }

        }


        return results;
    }

    private static void compareAtributes(String nodeName, XMLStreamReader r1, XMLStreamReader r2) {
        QName q1, q2;
        String n1, n2, v1, v2;
        Location location;
        for (int i = 0, n = r1.getAttributeCount(); i < n; ++i) {
            q1 = r1.getAttributeName(i);
            n1 = q1.getPrefix().isEmpty() ? q1.getLocalPart() : (q1.getPrefix() + ":" + q1.getLocalPart());
            v1 = r1.getAttributeValue(i);
            q2 = r2.getAttributeName(i);
            n2 = q2.getPrefix().isEmpty() ? q2.getLocalPart() : (q2.getPrefix() + ":" + q2.getLocalPart());
            v2 = r2.getAttributeValue(i);
            if (!n1.equals(n2) || !v1.equals(v2)) {
                location = r1.getLocation();
                System.out.println("ATTRIBUTE node=" + nodeName + "  " + location.getLineNumber() + ":" + location.getColumnNumber());
                System.out.print("   " + n1 + "='" + v1 + "'   " + n2 + "='" + v2 + "'  ");
            }
        }
    }

    private static void compareNamespaces(String nodeName, XMLStreamReader r1, XMLStreamReader r2) {
        String u1, u2;
        String p1, p2;
        Location location;
        for (int i = 0, n = r1.getNamespaceCount(); i < n; ++i) {
            u1 = r1.getNamespaceURI(i);
            p1 = r1.getNamespacePrefix(i);
            u2 = r2.getNamespaceURI(i);
            p2 = r2.getNamespacePrefix(i);
            if (!u1.equals(u2) || !p1.equals(p2)) {
                location = r1.getLocation();
                System.out.println("NAMESPACE node=" + nodeName + "  " + location.getLineNumber() + ":" + location.getColumnNumber());
                System.out.print("   " + p1 + "=" + u1 + "   " + p2 + "=" + u2 + "'  ");
            }
        }
    }


    static public class CompareResult {
        public static final int NODE = 1;
        public static final int ATTR = 2;
        public static final int TEXT = 3;

        public int diffType;
        public Location location;
        public String enclosingNode;
        public String attrName;
        public String attrValue;
        public String text;

    }

}
