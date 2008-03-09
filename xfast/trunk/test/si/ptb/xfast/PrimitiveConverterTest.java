package si.ptb.xfast;

import org.testng.Assert;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.Test;
import si.ptb.xfast.converters.PrimitiveConverter;

import javax.xml.stream.XMLStreamException;
import java.io.StringReader;
import java.lang.reflect.Field;

/**
 * @author peter
 */
public class PrimitiveConverterTest {

    public static String xml = "<primitive i=\"1000\" bool=\"true\" byt=\"127\" db=\"-1.6\" fl=\"1.1\" ch=\"f\" >" +
                    "just a some text</primitive>";


    @Test
    public void basicTest() throws IllegalAccessException {

        String[] data = {"true", "127", "f", "-1.6", "1.1", "1000"};


        PrimitiveConverter converter = new PrimitiveConverter();
        Primitive target = new Primitive();

        assertTrue(converter.canConvert(int.class));
        assertTrue(converter.canConvert(boolean.class));
        assertTrue(converter.canConvert(byte.class));
        assertTrue(converter.canConvert(float.class));
        assertTrue(converter.canConvert(char.class));

        for (Field field : target.getClass().getDeclaredFields()) {
            int code = PrimitiveConverter.getPrimitiveCode(field.getType());
            String value = data[code];
            converter.setPrimitive(code, field, target, value);
        }

        Assert.assertEquals(target.i, 1000);
        Assert.assertEquals(target.bool, true);
        Assert.assertEquals(target.byt, 127);
        Assert.assertEquals(target.db, -1.6d, 0.0d);
        Assert.assertEquals(target.fl, 1.1f, 0.0f);
        Assert.assertEquals(target.ch, 'f');

    }

    @Test
    public void parsePrimitives() {
        StringReader reader = new StringReader(xml);
        Xfast xf = new Xfast(Primitive.class, "primitive");

        Primitive target = (Primitive) xf.fromXML(reader);
        System.out.println("end");
    }

    public static class Primitive {
        @XMLattribute
        public int i;

        @XMLattribute
        public boolean bool;

        @XMLattribute
        public byte byt;

        @XMLattribute
        public float fl;

        @XMLattribute
        public double db;

        @XMLattribute
        public char ch;

        @XMLtext
        public String value;
    }

}
