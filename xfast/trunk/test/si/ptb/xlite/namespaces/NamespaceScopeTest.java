package si.ptb.xlite.namespaces;

import si.ptb.xlite.Xlite;
import si.ptb.xlite.XMLnode;
import si.ptb.xlite.XMLnamespaces;

import java.io.StringReader;

import org.testng.Assert;

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
    public void test() {
        StringReader reader = new StringReader(xml);
        Xlite xlite = new Xlite(aaa.class, "aaa");
        aaa a = (aaa) xlite.fromXML(reader);

        Assert.assertNotNull(a.node_BBB.node_ccc.node_x111);
        Assert.assertNotNull(a.node_BBB.node_x111);
        Assert.assertNotNull(a.node_x111);

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
