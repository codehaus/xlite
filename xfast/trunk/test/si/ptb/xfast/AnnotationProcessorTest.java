package si.ptb.xfast;

import org.testng.annotations.Test;

import java.io.StringReader;

import si.ptb.xfast.converters.NodeMapper;

/**
 * @author peter
 */
public class AnnotationProcessorTest {

    @Test
    public void printMapperTree() {
        StringReader reader = new StringReader(SampleXml.xml);
        Xfast xf = new Xfast(SampleXml.Person.class, "person");

        System.out.println("end");

    }
   

}
