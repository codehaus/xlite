package si.ptb.xlite;

import org.testng.Assert;

import java.io.StringReader;
import java.util.ArrayList;

/**
 * @author peter
 */
public class CollectionConverterTest {

    static String xml =
            "<one>" +
                    "just some text" +
                    "<item>" +
                    "first item text" +
                    "</item>" +
                    "<item>" +
                    "second item text" +
                    "</item>" +
                    "</one>";

    @org.testng.annotations.Test
    public void collectionConverterTest() {

        StringReader reader = new StringReader(xml);
        Xlite xlite = new Xlite(One.class, "one");
        One one = (One) xlite.fromXML(reader);

        Assert.assertEquals(one.text, "just some text"); // should be converted to upper case

    }

    public static class One {

        @XMLtext
        public String text;

        @XMLnode(value = "item", targetClass = Item.class)
        public ArrayList list;
    }

    public static class Item {

        @XMLtext
        public String text;
    }
}
