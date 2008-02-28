package si.ptb.xfast;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.Reader;
import java.io.Writer;

/**
 * User: peter
 * Date: Feb 25, 2008
 * Time: 11:48:02 AM
 */
public class Xfast {

    DefaultMapper rootMapper;
    

    public Xfast(Class rootClass, String nodeName) {
       rootMapper =  AnnotationProcessor.processClassTree(nodeName, rootClass);

    }


    public Object fromXML(Reader reader) throws XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader xmlreader = factory.createXMLStreamReader(reader);

        Object obj = rootMapper.deserialize(null, xmlreader);

        return obj;
    }

    public void toXML(Object source, Writer writer) {

    }

}
