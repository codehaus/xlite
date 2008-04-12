package si.ptb.xlite.namespaces;

import org.testng.Assert;
import si.ptb.xlite.XMLnamespaces;
import si.ptb.xlite.XMLnode;
import si.ptb.xlite.Xlite;

import java.io.StringReader;

/**
 * Test where all namespaces are defined in root node
 * @author peter
 */
public class RootNsTest {

    static String xml = "<lower:aaa xmlns:lower=\"lowercase\" xmlns:upper=\"uppercase\" xmlns:xnumber=\"xnumber\">\n" +
            "          <lower:bbb >\n" +
            "               <lower:ccc />\n" +
            "          </lower:bbb>\n" +
            "          <upper:BBB >\n" +
            "               <upper:CCC />\n" +
            "          </upper:BBB>\n" +
            "          <xnumber:x111 >\n" +
            "               <xnumber:x222 />\n" +
            "          </xnumber:x111>\n" +
            "     </lower:aaa>";

    @org.testng.annotations.Test
    public void test() {
        StringReader reader = new StringReader(xml);
        Xlite xlite = new Xlite(aaa.class, "l:aaa");

        // predefined namespaces
        xlite.addNamespace("l=lowercase");
        xlite.addNamespace("u=uppercase");
        aaa a = (aaa) xlite.fromXML(reader);

        Assert.assertTrue(a.node_bbb.node_ccc != null);
        Assert.assertTrue(a.node_BBB.node_CCC != null);
        Assert.assertTrue(a.node_x111.node_x222 != null);
    }

    public static class aaa {
        @XMLnode("l:bbb")
        public bbb node_bbb;

        @XMLnode("u:BBB")
        public BBB node_BBB;

        @XMLnamespaces("xn=xnumber")
        @XMLnode("xn:x111")
        public x111 node_x111;
    }

    public static class bbb {
        @XMLnode("l:ccc")
        public ccc node_ccc;
    }

    public static class ccc {
    }

    public static class BBB {
        @XMLnode("u:CCC")
        public CCC node_CCC;
    }

    public static class CCC {
    }

    @XMLnamespaces("xn=xnumber")
    public static class x111 {
        @XMLnode("xn:x222")
        public x222 node_x222;
    }

    public static class x222 {
    }

}
