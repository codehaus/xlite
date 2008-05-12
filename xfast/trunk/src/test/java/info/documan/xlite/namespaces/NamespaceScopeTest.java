package info.documan.xlite.namespaces;

import info.documan.xlite.XMLnode;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.IOException;

import org.testng.Assert;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XMLAssert;
import org.xml.sax.SAXException;
import info.documan.xlite.Xlite;
import info.documan.xlite.XMLnamespaces;

/**
 * @author peter
 */
public class NamespaceScopeTest {

      static String xml = "<aaa xmlns:lower = \"lowercase\" >\n" +
              "          <lower:BBB xmlns:lower = \"uppercase\" >\n" +
              "               <lower:x111 />\n" +
              "               <ccc xmlns:lower = \"xnumber\" >\n" +
              "                    <lower:x111 />\n" +
              "               </ccc>\n" +
              "          </lower:BBB>\n" +
              "          <lower:x111 />\n" +
              "     </aaa>";

    @org.testng.annotations.Test
    public void test() throws IOException, SAXException {
        StringReader reader = new StringReader(xml);
        Xlite xlite = new Xlite(aaa.class, "aaa");
        aaa a = (aaa) xlite.fromXML(reader);

        Assert.assertNotNull(a.node_BBB.node_ccc.node_x111);
        Assert.assertNotNull(a.node_BBB.node_x111);
        Assert.assertNotNull(a.node_x111);

                // writing back to XML
        StringWriter sw = new StringWriter();
        xlite.toXML(a, sw);
//        System.out.println(sw);
        XMLUnit.setIgnoreWhitespace(true);
        XMLAssert.assertXMLEqual(xml, sw.toString());
    }

    @XMLnamespaces("l=lowercase")
    public static class aaa {

        @XMLnamespaces("l=uppercase")
        @XMLnode("l:BBB")
        public BBB node_BBB;

        @XMLnode("l:x111")
        public x111 node_x111;
    }

    @XMLnamespaces("l=uppercase")
    public static class BBB {

        @XMLnamespaces("l=xnumber")
        @XMLnode("ccc")
        public ccc node_ccc;

        @XMLnode("l:x111")
        public x111 node_x111;
    }

    @XMLnamespaces("l=xnumber")
    public static class ccc {
        @XMLnode("l:x111")
        public x111 node_x111;
    }

    public static class x111 {
    }


}
