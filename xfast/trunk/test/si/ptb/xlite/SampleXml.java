package si.ptb.xlite;

/**
 * @author peter
 */
public class SampleXml {

    static String xml =
             "<person personAttribute=\"justPerson\">"
            + "just some text"
            +   "<number lastattr=\"AAA\">42</number>"
            +   "<firstname>"
            +       "Joe"
            +   "</firstname>"
//            +   "<fax unknownAttrib=\"xxx\">"
//            +       "justAValue"
//            +       "<code>321</code>"
//            +       "anotherText"
//            +       "<number>9999-999</number>"
//            +   "</fax>"
            +   "<phone newAttrib=\"unknown??\">"
            +       "<code>123</code>"
            +       "<number>1234-456</number>"
            +   "</phone>"
            + "</person>";


    public static class Person {

        @XMLattribute("personAttribute")
        public String pa;

        @XMLtext
        public String textValue;

        @XMLnode
        public String firstname;

        @XMLnode
        public int number;

//        @XMLnode("numbers")
//        public List numbers = new ArrayList();

        @XMLnode
        public PhoneNumber phone;
//    public PhoneNumber fax;
    }

    public static class PhoneNumber {

        @XMLnode
        public int code;

        @XMLnode
        public String number;
    }
}
