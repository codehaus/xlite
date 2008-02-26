package si.ptb.xfast;

import org.testng.annotations.Test;
import si.ptb.xfast.SubTreeStore;
import si.ptb.xfast.XmlComparer;

import javax.xml.stream.*;
import java.io.*;

/**
 * User: peter
 * Date: Feb 24, 2008
 * Time: 2:35:33 PM
 */
public class SubTreeStoreTest {

    static String xml = "<?xml version=\"1.0\" ?>"
            + "<person personAttribute=\"justPerson\">"
            + "<number lastattr=\"AAA\">44</number>"
            + "<firstname>"
            + "Joe"
            + "</firstname>"
            + "<emptyEl/>"
            + "<fax unknownAttrib=\"xxx\">"
            + "justAValue"
            + "<code>321</code>"
            + "anotherText"
            + "<number>9999-999</number>"
            + "</fax>"
            + "<phone newAttrib=\"unknown??\">"
            + "<code>123</code>"
            + "<number>1234-456</number>"
            + "</phone>"
            + "</person>";

    @Test
    public void testMain() throws IOException, XMLStreamException {

        for (int i = 0; i < 1; i++) {

            long start = System.currentTimeMillis();
//            String infile = "/home/peter/vmware/shared/Office Open XML Part 4 - Markup Language Reference/word/document.xml";
//            String outfile = "/home/peter/vmware/shared/Office Open XML Part 4 - Markup Language Reference/word/document2.xml";
            String infile = "/home/peter/vmware/shared/Office Open XML Part 3 - Primer/word/document.xml";
//            String infile = "/home/peter/vmware/shared/Office Open XML Part 3 - Primer/word/document2.xml";
//            String infile = "/home/peter/vmware/shared/testdoc2/word/document.xml";

            FileReader freader = new FileReader(infile);
            StringReader sreader = new StringReader(xml);

            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader reader = factory.createXMLStreamReader(freader);

            SubTreeStore sub = new SubTreeStore(500);
            sub.saveSubTree(reader);

//            Element element;
//            int c = 30;
//            while ((element = sub.getNextElement()) != null && c != 0) {
//                c--;
//                System.out.println(element.command + " : " + new String(element.data));
//            }

//            System.out.println("Length: " + sub.data.length);
//            for (int j = 0; j < sub.data.length; j++) {
//                byte b = sub.data[j];
//                System.out.println(j + ": " + b + " " + new String(sub.data, j, 1));
//            }


            XMLOutputFactory ofactory = XMLOutputFactory.newInstance();
            StringWriter sout = new StringWriter();
            FileWriter out = new FileWriter("/home/peter/vmware/shared/Office Open XML Part 3 - Primer/word/document2.xml");
            XMLStreamWriter ostr = ofactory.createXMLStreamWriter(out);
            sub.restoreSubTree(0, ostr);
//            System.out.println(xml);
//            System.out.println("------------------");
//            System.out.println(sout.toString());
            ostr.close();
            reader.close();

//            System.out.println("duration: " + (System.currentTimeMillis() - start));
//            System.out.println("elements: " + sub.elementNumber);
//            System.out.println("length: " + sub.data.length);

            FileReader r1 = new FileReader("/home/peter/vmware/shared/Office Open XML Part 3 - Primer/word/document.xml");
            FileReader r2 = new FileReader("/home/peter/vmware/shared/Office Open XML Part 3 - Primer/word/document2.xml");
            StringReader sr1 = new StringReader(xml);
            StringReader sr2 = new StringReader(sout.toString());


            XmlComparer.compare(r1, r2);
        }
    }
}
