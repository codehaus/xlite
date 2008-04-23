package si.ptb.xlite;

import org.custommonkey.xmlunit.*;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * User: peter
 * Date: Feb 24, 2008
 * Time: 2:35:33 PM
 */
public class SubTreeStoreTest {

    static String xml = "<a xmlns=\"ns1\" xmlns:s=\"ns2\">\n" +
            "<s:b>\n" +
            "<c>\n" +
            "<d attr=\"DDD\" ></d>\n" +
            "<ignored >" +
            "IGNORED" +
            "<subignored><subsubignored/></subignored>" +
            "</ignored>\n" +
            "</c>\n" +
            "</s:b>\n" +
            "</a>";

//        static String xml = "<a> "+
//            "<ignored >" +
//            "IGNORED" +
//            "<subignored><subsubignored/></subignored>" +
//            "</ignored>\n" +
//            "</a>";

    @Test
    public void testStoringNodes() throws IOException, SAXException, XpathException {
        StringReader reader = new StringReader(xml);

        Xlite xlite = new Xlite(A.class, "a");
        xlite.isStoringUnknownNodes = true;
        xlite.addNamespace("ns1");
//        xlite.addNamespace("s=ns2");
        A a = (A) xlite.fromXML(reader);

//        printStore(xlite.getNodeStore());

        // writing back to XML
        StringWriter sw = new StringWriter();
        xlite.toXML(a, sw);
        String ssw = sw.toString();
        System.out.println("");
        System.out.println(xml);
        System.out.println("");
        System.out.println(ssw);

        XMLUnit.setIgnoreWhitespace(true);
        XMLAssert.assertXMLEqual(xml, ssw);
    }

    public static void printStore(SubTreeStore store) {

        store.setPosition(0);
        boolean processingBlocks = true;
        SubTreeStore.Element element = store.getNextElement(0);
        while (processingBlocks) {
            if (element == null) {
                break;
            }
            if (element.command != SubTreeStore.START_BLOCK) {
                throw new IllegalArgumentException("Error: XMLSimpleWriter.restoreSubTree was given a wrong location " +
                        "argument: no saved data block is found on given location!");
            }
            while (element.command != SubTreeStore.END_BLOCK) {
                System.out.println("command:" + element.command + " data:" + new String(element.data));
                element = store.getNextElement();
            }
            System.out.println("command:" + element.command + " data:" + new String(element.data));
            element = store.getNextElement();
        }
    }

    public static class A {
        @XMLnamespaces("s=ns2")
        @XMLnode("s:b")
        public B b;
    }

    //    @XMLnamespaces("ns2")
    public static class B {
        @XMLnode
        public C c;
    }

    public static class C {
        @XMLnode
        public D d;
    }

    public static class D {
        @XMLattribute
        public String attr;
    }

}
