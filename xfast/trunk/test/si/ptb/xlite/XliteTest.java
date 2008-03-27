package si.ptb.xlite;

import org.testng.Assert;
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
    public void basicTest() throws XMLStreamException {

        System.out.println(SampleXml.xml);

        StringReader reader = new StringReader(SampleXml.xml);
        Xlite xlite = new Xlite(SampleXml.One.class, "one");

        SampleXml.One one = (SampleXml.One) xlite.fromXML(reader);
        Assert.assertEquals(one.attr, "text1");
        Assert.assertEquals(one.attr2, 1111);
        Assert.assertEquals(one.attr3, 1.1f, 0.0f);
        Assert.assertEquals(one.text, "textOne");

        Assert.assertTrue(one.two.attr4);
        Assert.assertEquals(one.two.character, 'x');        
        Assert.assertEquals(one.two.text, "textTwo");

        Assert.assertEquals(one.two.three1.attr, 42);
        Assert.assertEquals(one.two.three1.textField, "textThree");

        Assert.assertEquals(one.two.threeTwo.attr, 43);
        Assert.assertEquals(one.two.threeTwo.textField, "");


    }


}
