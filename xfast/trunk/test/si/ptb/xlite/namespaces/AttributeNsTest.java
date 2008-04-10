package si.ptb.xlite.namespaces;

import si.ptb.xlite.Xlite;
import si.ptb.xlite.XMLnode;
import si.ptb.xlite.XMLnamespaces;
import si.ptb.xlite.XMLattribute;

import java.io.StringReader;

import org.testng.Assert;

/**
 * @author peter
 */
public class AttributeNsTest {

      static String xml = "<lower:aaa xmlns:lower = \"lowercase\" xmlns:upper = \"uppercase\"\n" +
              "          xmlns:xnumber = \"xnumber\" >\n" +
              "          <lower:bbb lower:zz = \"11\" >\n" +
              "               <lower:ccc upper:WW = \"22\" />\n" +
              "          </lower:bbb>\n" +
              "          <upper:BBB lower:sss = \"***\" xnumber:S111 = \"???\" />\n" +
              "          <xnumber:x111 />\n" +
              "     </lower:aaa>";

    @org.testng.annotations.Test
    public void test() {
        StringReader reader = new StringReader(xml);
        Xlite xlite = new Xlite(aaa.class, "l:aaa");

        // predefined namespaces
        xlite.addNamespace("l=lowercase");
        xlite.addNamespace("u=uppercase");
        xlite.addNamespace("xn=xnumber");
        aaa a = (aaa) xlite.fromXML(reader);

        Assert.assertEquals(a.node_bbb.zz, 11);
        Assert.assertEquals(a.node_bbb.node_ccc.WW, 22);
        Assert.assertEquals(a.node_BBB.sss, "***");
        Assert.assertEquals(a.node_BBB.S111, "???");
        Assert.assertNotNull(a.node_x111);


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
    }

}
