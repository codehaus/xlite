package si.ptb.xfast;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

/**
 * User: peter
 * Date: Feb 25, 2008
 * Time: 11:48:02 AM
 */
public class Xfast {

    Mapper rootMapper;
    private List<Mapper> mappers;
    private List<ValueConverter> converters;
    private AnnotationProcessor annotationProcessor;

    public Xfast(Class rootClass, String nodeName) {

        setupMappers();
        setuprConverters();
        annotationProcessor = new AnnotationProcessor(converters, mappers);

        rootMapper = annotationProcessor.processClassTree(nodeName, rootClass);

    }

    private void setupMappers() {

    }

    private void setuprConverters() {

    }


    public Object fromXML(Reader reader) throws XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader xmlreader = factory.createXMLStreamReader(reader);

        Object obj = rootMapper.fromNode(null, xmlreader);

        return obj;
    }

    public void toXML(Object source, Writer writer) {

    }

}
