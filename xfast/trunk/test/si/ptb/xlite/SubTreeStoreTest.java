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
            "<i:ignored xmlns:i=\"iii\">" +
            "IGNORED" +
            "<subignored><subsubignored/></subignored>" +
            "</i:ignored>\n" +
            "<d attr=\"DDD\" ></d>\n" +
            "<ign></ign>" +
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

        printStore(xlite.getNodeStore());

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
        if (store == null) {
            return;
        }

        store.setPosition(0);
        boolean loop = true;
        SubTreeStore.Element element = store.getNextElement(0);
        while (loop) {
            if (element == null) {
                break;
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
