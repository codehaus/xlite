package si.ptb.xlite.namespaces;

import si.ptb.xlite.*;

import java.io.StringReader;

/**
 * @author peter
 */
public class RootNsTest {

    static String xml = "<lower:aaa xmlns:lower = \"lowercase\" xmlns:upper = \"uppercase\"\n" +
            "          xmlns:xnumber = \"xnumber\" >\n" +
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
    public void basicTest() {
        StringReader reader = new StringReader(xml);
        Xlite xlite = new Xlite(aaa.class, "l:aaa");
        xlite.addNamespace("l=lowercase");
        xlite.addNamespace("u=uppercase");
        aaa one = (aaa) xlite.fromXML(reader);

        System.out.println("end");
    }

    public static class aaa {
        @XMLnode("BBB")
        public BBB node_BBB;

        @XMLnode("bbb")
        public bbb node_bbb;

        @XMLnode("x111")
        public x111 node_x111;
    }

    public static class bbb {

    }

    public static class BBB {

    }

    public static class ccc {

    }

    public static class CCC {

    }

    public static class x111 {

    }

    public static class x222 {

    }

}
