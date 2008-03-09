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

    @Test
    public void mainTest() throws XMLStreamException {

        StringReader reader = new StringReader(SampleXml.xml);
        Xfast xf = new Xfast(SampleXml.Person.class, "person");

        SampleXml.Person person = (SampleXml.Person) xf.fromXML(reader);

        System.out.println("end!");
    }


}
