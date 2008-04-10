package si.ptb.xlite.namespaces;

import si.ptb.xlite.Xlite;
import si.ptb.xlite.XMLnode;
import si.ptb.xlite.XMLnamespaces;

import java.io.StringReader;

import org.testng.Assert;

/**
 * Test where xml elements belong to different namespaces although they have the same prefixes.
 * @author peter
 */
public class DifferentNsSamePrefixesTest {

      static String xml = "<aaa >\n" +
              "          <lower:bbb xmlns:lower = \"lowercase\" >\n" +
              "               <lower:ccc />\n" +
              "          </lower:bbb>\n" +
              "          <lower:BBB xmlns:lower = \"uppercase\" >\n" +
              "               <lower:CCC />\n" +
              "          </lower:BBB>\n" +
              "          <lower:x111 xmlns:lower = \"xnumber\" >\n" +
              "               <lower:x222 />\n" +
              "          </lower:x111>\n" +
              "     </aaa>";

    @org.testng.annotations.Test
    public void test() {
        StringReader reader = new StringReader(xml);
        Xlite xlite = new Xlite(aaa.class, "aaa");

        aaa a = (aaa) xlite.fromXML(reader);

        Assert.assertTrue(a.node_bbb.node_ccc != null);
        Assert.assertTrue(a.node_BBB.node_CCC != null);
        Assert.assertTrue(a.node_x111.node_x222 != null);

    }

    // node aaa is in default namespace
    public static class aaa {
        @XMLnamespaces("lower=lowercase")
        @XMLnode("lower:bbb")
        public bbb node_bbb;

        @XMLnamespaces("lower=uppercase")
        @XMLnode("lower:BBB")
        public BBB node_BBB;

        @XMLnamespaces("lower=xnumber")
        @XMLnode("lower:x111")
        public x111 node_x111;
    }

    public static class bbb {
        @XMLnamespaces("lower=lowercase")
        @XMLnode("lower:ccc")
        public ccc node_ccc;
    }

    public static class ccc {
    }

    public static class BBB {
        @XMLnamespaces("lower=uppercase")
        @XMLnode("lower:CCC")
        public CCC node_CCC;
    }

    public static class CCC {
    }

    @XMLnamespaces("lower=xnumber")
    public static class x111 {
        @XMLnode("lower:x222")
        public x222 node_x222;
    }

    public static class x222 {
    }
}
