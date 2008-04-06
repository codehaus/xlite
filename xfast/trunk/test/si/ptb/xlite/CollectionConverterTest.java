package si.ptb.xlite;

import org.testng.Assert;

import java.io.StringReader;
import java.util.List;

/**
 * @author peter
 */
public class CollectionConverterTest {

    static String xml =
            "<one>" +
                    "just some text" +
                    "<item>" +
                    "first item text" +
                    "<subitem>sub11</subitem>" +
                    "<subitem>sub12</subitem>" +
                    "</item>" +
                    "<ignored>Ignored<subignored/></ignored>" +
//                    "<ignored>Ignored</ignored>" +
                    "<item>" +
                    "second item text" +
                    "<subitem>sub21<ignored>Ignored</ignored></subitem>" +
                    "<subitem>sub22</subitem>" +
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

        @XMLnode(value = "item", itemType = Item.class)
        public List list;
    }

    public static class Item {

//        @XMLtext
//        public String text;

        @XMLnode(value = "subitem", itemType = SubItem.class)
        public List subs;
    }

    public static class SubItem {

        @XMLtext
        public String text;
    }
}
