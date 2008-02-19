package si.ptb.fastconverter;

import com.thoughtworks.xstream.XStream;
import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.ArrayList;

import si.ptb.fastconverter.docx.BodyConverter;
import si.ptb.fastconverter.docx.DocxDocument;

/**
 * User: peter
 * Date: Feb 1, 2008
 * Time: 3:47:10 PM
 */
public class XStest {

    String xml = "<person personAttribute=\"justPerson\">"
            + "<number lastattr=\"AAA\">42</number>"
            + "<firstname>"
            + "Joe"
            + "</firstname>"
            + "<fax unknownAttrib=\"xxx\">"
            + "justAValue"
            + "<code>321</code>"
            + "<number>9999-999</number>"
            + "</fax>"
            + "<phone newAttrib=\"unknown??\">"
            + "    <code>123</code>"
            + "<number>1234-456</number>"
            + "</phone>"
            + "</person>";

    String xml2 = "<person personAttribute=\"justPerson\">"
            + "<number lastattr=\"AAA\">42</number>"
            + "<firstname>Joe</firstname>"
            + "<numbers>"
            + "<fax unknownAttrib=\"xxx\">"
            + "justAValue"
            + "  <code>321</code>"
            + "  <number>9999-999</number>"
            + "</fax>"
            + "<phone newAttrib=\"unknown??\">"
            + "  <code>123</code>"
            + "  <number>1234-456</number>"
            + "</phone>"
            + "<fax2>fff</fax2>"
            + "</numbers>"
            + "</person>";




    @Test
    public void testPerson() {

        XStream xs = new XStream();
        xs.alias("person", Person.class);
        xs.registerConverter(new BodyConverter());
        xs.registerConverter(new FastClassConverter(Person.class, "person"));

        Person person = (Person) xs.fromXML(xml);

        String out = xs.toXML(person);

        //comment
        System.out.println(out);


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

    public static void printObject(Object object) {
        System.out.println(object.getClass() + " " + object.hashCode());
        Field[] fields = object.getClass().getFields();
        for (int i = 0; i < fields.length; i++) {
            try {
                System.out.println("  field: " + fields[i].get(object).getClass() + " " + fields[i].get(object).hashCode());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

}



