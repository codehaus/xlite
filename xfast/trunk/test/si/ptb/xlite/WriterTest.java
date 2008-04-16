package si.ptb.xlite;

import javax.xml.stream.XMLStreamException;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * @author peter
 */
public class WriterTest {

    static String xml1 = "<a xmlns=\"nsURI\"><b><c><d attr=\"DDD\" /></c></b></a>";

    @org.testng.annotations.Test
    public void test() throws XMLStreamException {
        StringReader reader = new StringReader(xml1);
        StringWriter sw = new StringWriter();

        Xlite xlite = new Xlite(A.class, "a", "nsURI");
        A a = (A) xlite.fromXML(reader);
        System.out.println("");
        xlite.toXML(a, sw);
        System.out.println("");
    }

    public static class A {
        @XMLnode
        public B b;
    }

    public static class B {
        @XMLnode
        public C c;
    }

    public static class C {
        @XMLnode
        public D d;
    }

    public static class D {
        @XMLattribute
        public String attr;
    }

}
