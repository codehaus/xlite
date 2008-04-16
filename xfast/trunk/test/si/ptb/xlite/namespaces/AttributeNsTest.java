package si.ptb.xlite.namespaces;

import si.ptb.xlite.Xlite;
import si.ptb.xlite.XMLnode;
import si.ptb.xlite.XMLnamespaces;
import si.ptb.xlite.XMLattribute;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.IOException;

import org.testng.Assert;
import org.custommonkey.xmlunit.XMLAssert;
import org.xml.sax.SAXException;

/**
 * @author peter
 */
public class AttributeNsTest {

      static String xml = "<lower:aaa xmlns:lower = \"lowercase\" xmlns:upper = \"uppercase\"\n" +
              "          xmlns:xnumber = \"xnumber\" xmlns=\"defaultNS\" >\n" +
              "          <lower:bbb lower:zz = \"11\" >\n" +
              "               <lower:ccc upper:WW = \"22\" />\n" +
              "          </lower:bbb>\n" +
              "          <upper:BBB lower:sss = \"***\" xnumber:S111 = \"???\" />\n" +
              "          <xnumber:x111 RRR=\"rrrdata\" />\n" +
              "     </lower:aaa>";

    @org.testng.annotations.Test
    public void test() throws IOException, SAXException {
        StringReader reader = new StringReader(xml);
        Xlite xlite = new Xlite(aaa.class, "l:aaa");

        // predefined namespaces
        xlite.addNamespace("l=lowercase");
        xlite.addNamespace("u=uppercase");
        xlite.addNamespace("xn=xnumber");
        xlite.addNamespace("defaultNS");      // default namespace
        aaa a = (aaa) xlite.fromXML(reader);

        Assert.assertEquals(a.node_bbb.zz, 11);
        Assert.assertEquals(a.node_bbb.node_ccc.WW, 22);
        Assert.assertEquals(a.node_BBB.sss, "***");
        Assert.assertEquals(a.node_BBB.S111, "???");
        Assert.assertNotNull(a.node_x111);
        Assert.assertEquals(a.node_x111.rrr, "rrrdata");

        StringWriter sw = new StringWriter();
        xlite.toXML(a, sw);
        System.out.println(xml);
        System.out.println(sw.toString());
        aaa a2 = (aaa) xlite.fromXML(new StringReader(sw.toString()));

        XMLAssert.assertXMLEqual(xml, sw.toString());
    }

    public static class aaa {
        @XMLnode("l:bbb")
        public bbb node_bbb;

        @XMLnode("u:BBB")
        public BBB node_BBB;

        @XMLnode("xn:x111")
        public x111 node_x111;
    }

    public static class bbb {
        @XMLnode("l:ccc")
        public ccc node_ccc;

        @XMLattribute("l:zz")
        public int zz;
    }

    public static class ccc {
        @XMLattribute("u:WW")
        public int WW;
    }

    public static class BBB {
        @XMLattribute("l:sss")
        public String sss;

        @XMLattribute("xn:S111")
        public String S111;
    }

    public static class x111 {
        @XMLattribute("RRR")   // no NS defined, but this does NOT mean default ns
        public String rrr;
    }

}
