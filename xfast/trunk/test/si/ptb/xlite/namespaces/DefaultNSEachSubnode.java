package si.ptb.xlite.namespaces;

import si.ptb.xlite.Xlite;
import si.ptb.xlite.XMLnode;
import si.ptb.xlite.XMLnamespaces;

import java.io.StringReader;

import org.testng.Assert;

/**
 * Test where each subnode defines it's default namespace
 * @author peter
 */
public class DefaultNSEachSubnode {

       static String xml = "<aaa >\n" +
               "          <bbb xmlns = \"lowercase\" >\n" +
               "               <ccc />\n" +
               "          </bbb>\n" +
               "          <BBB xmlns = \"uppercase\" >\n" +
               "               <CCC />\n" +
               "          </BBB>\n" +
               "          <x111 xmlns = \"xnumber\" >\n" +
               "               <x222 />\n" +
               "          </x111>\n" +
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

    public static class aaa {
        @XMLnamespaces("lowercase")
        @XMLnode("bbb")
        public bbb node_bbb;

        @XMLnamespaces("uppercase")
        @XMLnode("BBB")
        public BBB node_BBB;

        @XMLnamespaces("xnumber")
        @XMLnode("x111")
        public x111 node_x111;
    }

    @XMLnamespaces("lowercase")
    public static class bbb {
        @XMLnode("ccc")
        public ccc node_ccc;
    }

    public static class ccc {
    }

    @XMLnamespaces("uppercase")
    public static class BBB {
        @XMLnode("CCC")
        public CCC node_CCC;
    }

    public static class CCC {
    }

    @XMLnamespaces("xnumber")
    public static class x111 {
        @XMLnode("x222")
        public x222 node_x222;
    }

    public static class x222 {
    }
}
