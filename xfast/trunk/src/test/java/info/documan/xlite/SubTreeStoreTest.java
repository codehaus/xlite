package info.documan.xlite;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import info.documan.xlite.Xlite;
import info.documan.xlite.XMLattribute;
import info.documan.xlite.XMLelement;
import info.documan.xlite.XMLtext;

/**
 * User: peter
 * Date: Feb 24, 2008
 * Time: 2:35:33 PM
 */
public class SubTreeStoreTest {

//    static String xml = "<a xmlns=\"ns1\" xmlns:s=\"ns2\">\n" +
//            "<s:b>\n" +
//            "<c>\n" +
//            "<i:ignored xmlns:i=\"iii\" ia=\"11\" ia2=\"12\">" +
//            "<ign/>" +
//            "IGNORED" +
//            "<subignored asub=\"666\"><subsub/></subignored>" +
//            "</i:ignored>\n" +
//            "<d attrD=\"DDD\" ></d>\n" +
//            "</c>\n" +
//            "</s:b>\n" +
//            "</a>";


    static String xml = "<a xmlns=\"ns1\" xmlns:s=\"ns2\" xmlns:w=\"ns3\">\n" +
            "<s:b>\n" +
            "<c>CCCCCCC\n" +
            "<e>EEEE</e>\n"+
            "<w:emptico xmlns:w=\"www\"/>\n" +
            "<w:one aa=\"prvi\">\n" +
            "<w:empty/>\n" +
            "</w:one>\n" +
            "<subignored asub=\"666\"><subsub/></subignored>\n" +
            "<d attrD=\"DDD\" ></d>\n" +
            "</c>\n" +
            "</s:b>\n" +
            "</a>";

    @Test
    public void testStoringNodes() throws IOException, SAXException, XpathException {
        StringReader reader = new StringReader(xml);

        Xlite xlite = new Xlite(A.class, "a", "ns1");
        xlite.setStoringUnknownElements(true);
        xlite.addNamespace("ns1");
        xlite.addNamespace("s=ns2");
        xlite.addNamespace("w=ns3");
        A a = (A) xlite.fromXML(reader);

//        XMLSimpleReader.printStore(xlite.getElementStore(), "STORE");

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

    public static class A {
//        @XMLnamespaces("s=ns2")
        @XMLelement("s:b")
        public B b;
    }

    //    @XMLnamespaces("ns2")
    public static class B {
        @XMLelement
        public C c;
    }

    public static class C {
        @XMLelement
        public D d;

        @XMLtext
        public String text;
    }

    public static class D {
        @XMLattribute
        public String attrD;
    }

}
