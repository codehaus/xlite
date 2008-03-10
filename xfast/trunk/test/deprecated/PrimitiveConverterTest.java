package deprecated;

import org.testng.Assert;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.Test;
import deprecated.PrimitiveConverter;

import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import si.ptb.xfast.Xfast;
import si.ptb.xfast.XMLattribute;
import si.ptb.xfast.XMLtext;

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
            converter.setPrimitive(field, target, value);
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

//    @Test
    public void primitiveVersusImmutablebenchmark() throws NoSuchFieldException, IllegalAccessException {

        String[] data = {"true", "127", "f", "-1.6", "1.1", "1000"};

        PrimitiveConverter converter = new PrimitiveConverter();
        Field bool = null, byt = null, ch = null, db = null, fl = null, i = null;
        bool = Primitive.class.getField("bool");
        byt = Primitive.class.getField("byt");
        ch = Primitive.class.getField("ch");
        db = Primitive.class.getField("db");
        fl = Primitive.class.getField("fl");
        i = Primitive.class.getField("i");

        // initialize
        int size = 100000;
        int repeat = 100;
        List<Primitive> primitives = new ArrayList<Primitive>(size);
        for (int c = 0; c < size; c++) {
            primitives.add(new Primitive());
        }

        long start;

        start = System.currentTimeMillis();
        for (int j = 0; j < repeat; j++) {
            for (Primitive primitive : primitives) {
                converter.setPrimitive(bool, primitive, data[0]);
                converter.setPrimitive(byt, primitive, data[1]);
                converter.setPrimitive(ch, primitive, data[2]);
                converter.setPrimitive(db, primitive, data[3]);
                converter.setPrimitive(fl, primitive, data[4]);
                converter.setPrimitive(i, primitive, randomInt());
            }
        }
        System.out.println("duration primitive: " + (System.currentTimeMillis() - start));

        start = System.currentTimeMillis();
        for (int j = 0; j < repeat; j++) {
            for (Primitive primitive : primitives) {
                bool.set(primitive, Boolean.valueOf(data[0]));
                byt.set(primitive, Byte.valueOf(data[1]));
                ch.set(primitive, Character.valueOf(data[2].charAt(0)));
                db.set(primitive, Double.valueOf(data[3]));
                fl.set(primitive, Float.valueOf(data[4]));
                i.set(primitive, (Object) Integer.valueOf(randomInt()));
            }
        }
        System.out.println("duration immutable direct: " + (System.currentTimeMillis() - start));

        start = System.currentTimeMillis();
        for (int j = 0; j < repeat; j++) {
            for (Primitive primitive : primitives) {
                converter.setImmutable(bool, primitive, data[0]);
                converter.setImmutable(byt, primitive, data[1]);
                converter.setImmutable(ch, primitive, data[2]);
                converter.setImmutable(db, primitive, data[3]);
                converter.setImmutable(fl, primitive, data[4]);
                converter.setImmutable(i, primitive, randomInt());
            }
        }
        System.out.println("duration immutable: " + (System.currentTimeMillis() - start));

    }

    static public String randomInt() {
        Random rand = new Random();
        return String.valueOf(rand.nextInt());
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
