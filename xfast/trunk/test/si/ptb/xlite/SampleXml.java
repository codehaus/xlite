package si.ptb.xlite;

/**
 * @author peter
 */
public class SampleXml {

    static String xml =
            "<one attr1=\"text1\" attr2=\"1111\" attr3=\"1.1\">" +
                    "just a <!-- comment should be ignored -->text" +
                    "<emptyNode attrEmpty=\"-1.6\"/>" +
                    "<two attr4=\"true\" attr5=\"x\" >" +
                    "textTwo" +
                    "<three1 val=\"42\">" +
                    "textThree" +
                    "</three1>" +
                    "<nodeWithSubnodes>" +
                    "<integer>2008</integer>" +
                    "<bool>true</bool>" +
                    "<char>f</char>" +
                    "<float>-15.555</float>" +
                    "</nodeWithSubnodes>" +
                    "</two>" +
                    "</one>";


    public static class One {

        @XMLattribute("attr1")
        public String attr;

        @XMLattribute
        public int attr2;

        @XMLattribute
        public float attr3;

        @XMLtext
        public String text;

        @XMLnode
        public Empty emptyNode;

        @XMLnode
        public Two two;

    }

    public static class Two {

        @XMLattribute
        public boolean attr4;

        @XMLattribute("attr5")
        public char character;

        @XMLtext
        public String text;

        @XMLnode
        public Three three1;

        @XMLnode("nodeWithSubnodes")
        public Four four;
    }

    public static class Three {

        @XMLattribute("val")
        public int attr;

        @XMLtext
        public String textField;
    }

    public static class Four {

        @XMLnode("integer")
        public int i;

        @XMLnode("bool")
        public boolean b;

        @XMLnode("char")
        public char c;

        @XMLnode("float")
        public float f;

    }

    public static class Empty {
        @XMLattribute
        public double attrEmpty;
    }
}
