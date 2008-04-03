package si.ptb.xlite;

import org.testng.Assert;
import si.ptb.xlite.converters.NodeConverter;
import si.ptb.xlite.converters.ValueConverter;

import java.io.StringReader;

/**
 * @author peter
 */
public class CustomConverterTest {

    static String xml =
            "<one>" +
                    "should be upper case" +
                    "<custom>" +
                    "this is a text of a custom field" +
                    "<three val=\"SHOULD BE LOWER CASE\">" +
                    "textThree" +
                    "</three>" +
                    "<ignored>this node is ignored</ignored>" +
                    "</custom>" +
                    "</one>";

    @org.testng.annotations.Test
    public void customConverterTest() {

        StringReader reader = new StringReader(xml);
        Xlite xlite = new Xlite(One.class, "one");
        One one = (One) xlite.fromXML(reader);

        Assert.assertEquals(one.text, "SHOULD BE UPPER CASE"); // should be converted to upper case
        Assert.assertEquals(one.custom.getClass(), Custom.class);
        Assert.assertEquals(one.custom.value, "this is a text of a custom field");
        Assert.assertEquals(one.custom.three.attr, "should be lower case"); // should be converted to lower case
        Assert.assertEquals(one.custom.three.textField, "textThree");

    }

    public static class CustomNodeConverter implements NodeConverter {

        public boolean canConvert(Class type) {
            return Custom.class.equals(type);
        }


        public Object fromNode(XMLSimpleReader reader, MappingContext mappingContext) {
            Custom custom = new Custom();
            custom.value = reader.getText();
            while (reader.moveDown()) {
                if (reader.getName().getLocalPart().equals("three")) {
                    custom.three = (Three) mappingContext.processNextNode(Three.class, reader);
                }
                reader.moveUp();
            }
            return custom;
        }

        public void toNode(Object object, XMLSimpleWriter writer, MappingContext mappingContext) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

    }

    public static class UpperCaseConverter implements ValueConverter {

        public boolean canConvert(Class type) {
            return Custom.class.equals(type);
        }

        public Object fromValue(String value) {
            return value.toUpperCase();
        }

        public String toValue(Object object) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }

     public static class LowerCaseConverter implements ValueConverter {

        public boolean canConvert(Class type) {
            return Custom.class.equals(type);
        }

        public Object fromValue(String value) {
            return value.toLowerCase();
        }

        public String toValue(Object object) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    public static class Custom {

        public String value;
        public Three three;
    }

    public static class One {

        @XMLtext(converter = UpperCaseConverter.class)
        public String text;

        @XMLnode(converter = CustomNodeConverter.class)
        public CustomConverterTest.Custom custom;

    }

    public static class Three {

        @XMLattribute(value = "val", converter = LowerCaseConverter.class)
        public int attr;

        @XMLtext
        public String textField;
    }
}
