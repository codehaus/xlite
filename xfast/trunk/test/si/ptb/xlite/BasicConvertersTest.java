package si.ptb.xlite;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.StringReader;
import java.lang.reflect.Field;

/**
 * @author peter
 */
public class BasicConvertersTest {

    static String xml = "<primitives i=\"1000\" bool=\"true\" byt=\"127\" db=\"-1.6\" fl=\"1.1\" ch=\"f\" >" +
            "A text value" +
            "<iv>999</iv>" +
            "<boolv>false</boolv>" +
            "<bytv>-127</bytv>" +
            "<dbv>1.6</dbv>" +
            "<flv>-1.1</flv>" +
            "<chv>g</chv>" +
            "</primitives> ";


    @Test
    public void mainTest() throws IllegalAccessException {
        StringReader reader = new StringReader(xml);
        Xlite xf = new Xlite(Primitives.class, "primitives");

        Primitives primitives = (Primitives) xf.fromXML(reader);

        for (Field field : Primitives.class.getDeclaredFields()) {
            System.out.println(field.getName() + "=" + field.get(primitives));
        }

        //attributes
        Assert.assertEquals(primitives.i, 1000);
        Assert.assertTrue(primitives.bool);
        Assert.assertEquals(primitives.byt, 127);
        Assert.assertEquals(primitives.db, -1.6d, 0.0d);
        Assert.assertEquals(primitives.fl, 1.1f, 0.0f);
        Assert.assertEquals(primitives.ch,'f');

        //node text
        Assert.assertEquals(primitives.value, "A text value");

        // subnodes
        Assert.assertEquals(primitives.iv, 999);
        Assert.assertFalse(primitives.boolv);
        Assert.assertEquals(primitives.bytv, -127);
        Assert.assertEquals(primitives.dbv, 1.6d, 0.0d);
        Assert.assertEquals(primitives.flv, -1.1f, 0.0f);
        Assert.assertEquals(primitives.chv,'g');

    }

    public static class Primitives {
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

        @XMLnode
        public int iv;

        @XMLnode
        public boolean boolv;

        @XMLnode
        public byte bytv;

        @XMLnode
        public float flv;

        @XMLnode
        public double dbv;

        @XMLnode
        public char chv;

    }
}