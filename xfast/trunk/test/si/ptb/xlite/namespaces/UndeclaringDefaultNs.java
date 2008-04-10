package si.ptb.xlite.namespaces;

import si.ptb.xlite.Xlite;
import si.ptb.xlite.XMLnode;
import si.ptb.xlite.XMLnamespaces;

import java.io.StringReader;

import org.testng.Assert;

/**
 * @author peter
 */
public class UndeclaringDefaultNs {
       static String xml = "<aaa xmlns = \"lowercase\" >\n" +
               "          <bbb >\n" +
               "               <ccc xmlns = \"\" >\n" +
               "                    <ddd />\n" +
               "               </ccc>\n" +
               "          </bbb>\n" +
               "     </aaa>";

    @org.testng.annotations.Test
    public void test() {
        StringReader reader = new StringReader(xml);
        Xlite xlite = new Xlite(aaa.class, "aaa");

        // predefined default namespace
        xlite.addNamespace("lowercase");
        aaa a = (aaa) xlite.fromXML(reader);

        Assert.assertTrue(a.node_bbb.node_ccc.node_ddd != null);

    }
    public static class aaa {
        @XMLnode("bbb")
        public bbb node_bbb;
    }

    public static class bbb {
        @XMLnamespaces("")
        @XMLnode("ccc")
        public ccc node_ccc;
    }

    @XMLnamespaces("")
    public static class ccc {
        @XMLnode("ddd")
        public ddd node_ddd;
    }

    public static class ddd {
    }

}
