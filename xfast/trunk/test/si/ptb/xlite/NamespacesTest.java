package si.ptb.xlite;

import java.io.StringReader;

/**
 * @author peter
 */
public class NamespacesTest {

    static String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<f:one xmlns:f=\"first\" xmlns:s=\"second\">" +
            "<f:two s:data=\"mydata\">firsttext</f:two>" +
            "<s:two s:data=\"mydata\">secondtext</s:two>" +  // same node with different namespace
            "<t:two xmlns:t=\"third\">thirdtext</t:two>" +   // overriden namespace
            "</f:one>";

    @org.testng.annotations.Test
    public void basicTest() {
        StringReader reader = new StringReader(xml);
        Xlite xlite = new Xlite(One.class, "fx:one");
        xlite.addNamespace("fx=first");
//        xlite.addNamespace("s=second");
        One one = (One) xlite.fromXML(reader);

        System.out.println("end");
    }

//    @XMLnamespaces("fx=first")
    public static class One {
        @XMLtext
        public String text;

        @XMLnode("fx:two")
        public Two sub1;

        @XMLnamespaces("sx=second")
        @XMLnode("sx:two")
        public Two sub2;

        // overriden namespace
        @XMLnamespaces("tx=third")
        @XMLnode("tx:two")
        public Two sub3;
    }

    public static class Two {

        @XMLtext
        public String text;

        @XMLnamespaces("sx=second")
        @XMLattribute("sx:data")
        public String attr;

    }

}
