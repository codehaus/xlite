package si.ptb.xlite.namespaces;

import org.testng.Assert;
import si.ptb.xlite.XMLnamespaces;
import si.ptb.xlite.XMLnode;
import si.ptb.xlite.Xlite;

import java.io.StringReader;

/**
 * Test where default namespaces are used, but namespaces for chosen elements can still be explicitly stated.
 * @author peter
 */
public class DefaultNsOverridingTest {

    static String xml = "<aaa xmlns:upper = \"uppercase\" xmlns:xnumber = \"xnumber\" >\n" +
            "          <bbb xmlns = \"lowercase\" >\n" +
            "               <ccc />\n" +
            "               <upper:WWW />\n" +
            "               <xnumber:x666 />\n" +
            "          </bbb>\n" +
            "          <BBB xmlns = \"uppercase\" >\n" +
            "               <upper:WWW />\n" +
            "               <xnumber:x666 />\n" +
            "               <CCC />\n" +
            "          </BBB>\n" +
            "          <x111 xmlns = \"xnumber\" >\n" +
            "               <x222 />\n" +
            "               <upper:WWW />\n" +
            "               <xnumber:x666 />\n" +
            "          </x111>\n" +
            "     </aaa>";

    @org.testng.annotations.Test
    public void test() {
        StringReader reader = new StringReader(xml);
        Xlite xlite = new Xlite(aaa.class, "l:aaa");

        // predefined namespaces
        xlite.addNamespace("u=uppercase");
        xlite.addNamespace("xn=xnumber");
        aaa a = (aaa) xlite.fromXML(reader);

        Assert.assertTrue(a.node_bbb.node_ccc != null);
        Assert.assertTrue(a.node_bbb.node_WWW != null);
        Assert.assertTrue(a.node_bbb.node_x666 != null);
        Assert.assertTrue(a.node_BBB.node_CCC != null);
        Assert.assertTrue(a.node_BBB.node_WWW != null);
        Assert.assertTrue(a.node_BBB.node_x666 != null);
        Assert.assertTrue(a.node_x111.node_x222 != null);
        Assert.assertTrue(a.node_x111.node_WWW != null);
        Assert.assertTrue(a.node_x111.node_x666 != null);
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

        @XMLnode("u:WWW")
        public WWW node_WWW;

        @XMLnode("xn:x666")
        public x666 node_x666;
    }

    public static class ccc {
    }

    @XMLnamespaces("uppercase")
    public static class BBB {
        @XMLnode("CCC")
        public CCC node_CCC;

        @XMLnode("u:WWW")
        public WWW node_WWW;

        @XMLnode("xn:x666")
        public x666 node_x666;
    }

    public static class CCC {
    }

    @XMLnamespaces("xnumber")
    public static class x111 {
        @XMLnode("x222")
        public x222 node_x222;

        @XMLnode("u:WWW")
        public WWW node_WWW;

        @XMLnode("xn:x666")
        public x666 node_x666;
    }

    public static class x222 {
    }

    public static class x666 {
    }

    public static class WWW {
    }
}

