package si.ptb.xfast;

import org.testng.annotations.Test;
import si.ptb.xfast.docx.DocxDocument;

import javax.xml.stream.XMLStreamException;
import java.util.ArrayList;
import java.util.List;
import java.io.StringReader;

/**
 * User: peter
 * Date: Feb 26, 2008
 * Time: 11:50:33 PM
 */
public class XfastTest {

    static String xml = "<person personAttribute=\"justPerson\">"
            + "<number lastattr=\"AAA\">42</number>"
            + "<firstname>"
            + "Joe"
            + "</firstname>"
            + "<fax unknownAttrib=\"xxx\">"
            + "justAValue"
            + "<code>321</code>"
            + "anotherText"
            + "<number>9999-999</number>"
            + "</fax>"
            + "<phone newAttrib=\"unknown??\">"
            + "    <code>123</code>"
            + "<number>1234-456</number>"
            + "</phone>"
            + "</person>";

    @Test
    public void mainTest() throws XMLStreamException {

        StringReader reader = new StringReader(xml);
        Xfast xf = new Xfast(Person.class, "person");

//        Person person = (Person) xf.fromXML(reader);

        System.out.println("end!");
    }

    public static class Person {
        @XMLnode("firstname")
        public String firstname;

        @XMLnode("number")
        public int number;

        @XMLnode("numbers")
        public List numbers = new ArrayList();

        @XMLnode("phone")
        public PhoneNumber phone;
//    public PhoneNumber fax;
    }

    public static class PhoneNumber {

        @XMLnode("code")
        public int code;

        @XMLnode("number")
        public String number;
    }
}
