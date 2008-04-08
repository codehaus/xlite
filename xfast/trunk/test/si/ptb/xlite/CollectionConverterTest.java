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
                    "<ignored>Ignored<subignored/><subignored2/><subignored3/></ignored>" +
                    "<subitem>sub12</subitem>" +
                    "</item>" +
                    "<ignored>Ignored<subignored/><subignored2/><subignored3/></ignored>" +
                    "<item>" +
                    "second item text" +
                    "<subitem>sub21<ignored>Ignored</ignored></subitem>" +
                    "<ignored>Ignored<subignored/><subignored2/><subignored3/></ignored>" +
                    "<subitem>sub22</subitem>" +
                    "<subitem>sub23</subitem>" +
                    "</item>" +
                    "</one>";

    @org.testng.annotations.Test
    public void collectionConverterTest() {

        StringReader reader = new StringReader(xml);
        Xlite xlite = new Xlite(One.class, "one");
        One one = (One) xlite.fromXML(reader);

        Assert.assertEquals(one.text, "just some text"); // should be converted to upper case
        Assert.assertEquals(one.list.size(), 2);
        Assert.assertEquals(one.list.get(0).text, "first item text");
        Assert.assertEquals(one.list.get(1).text, "second item text");
        Assert.assertEquals(one.list.get(0).subs.size(), 2);
        Assert.assertEquals(one.list.get(1).subs.size(), 3);
        Assert.assertEquals(one.list.get(1).subs.get(0).text, "sub21");
        Assert.assertEquals(one.list.get(1).subs.get(1).text, "sub22");
        Assert.assertEquals(one.list.get(1).subs.get(2).text, "sub23");

    }

    public static class One {

        @XMLtext
        public String text;

        @XMLnode(value = "item", itemType = Item.class)
        public List<Item> list;
    }

    public static class Item {

        @XMLtext
        public String text;

        @XMLnode(value = "subitem", itemType = SubItem.class)
        public List<SubItem> subs;
    }

    public static class SubItem {

        @XMLtext
        public String text;
    }
}
