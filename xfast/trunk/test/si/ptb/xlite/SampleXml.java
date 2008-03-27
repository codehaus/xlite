package si.ptb.xlite;

/**
 * @author peter
 */
public class SampleXml {

    static String xml =
            "<one attr1=\"text1\" attr2=\"1111\" attr3=\"1.1\">" +
                    "textOne" +
                    "<two attr4=\"true\" attr5=\"x\" >" +
                    "textTwo"+
                    "<three1 val=\"42\">" +
                    "textThree"+
                    "</three1>" +
                    "<three2 val=\"43\">" +
                    "</three2>" +
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
        public Two two;

        @XMLnode("three")
        public Three otherNode;
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

        @XMLnode("three2")
        public Three threeTwo;
    }

    public static class Three {

        @XMLattribute("val")
        public int attr;

        @XMLtext
        public String textField;
    }
}
