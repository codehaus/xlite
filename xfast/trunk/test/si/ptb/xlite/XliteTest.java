package si.ptb.xlite;

import org.testng.annotations.Test;

import javax.xml.stream.XMLStreamException;
import java.io.StringReader;

/**
 * User: peter
 * Date: Feb 26, 2008
 * Time: 11:50:33 PM
 */
public class XliteTest {

    @Test
    public void mainTest() throws XMLStreamException {

        StringReader reader = new StringReader(SampleXml.xml);
        Xlite xf = new Xlite(SampleXml.Person.class, "person");

        SampleXml.Person person = (SampleXml.Person) xf.fromXML(reader);
        System.out.println("end!");

    }


}
