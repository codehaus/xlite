package si.ptb.xfast;

import si.ptb.xfast.converters.NodeConverter;
import si.ptb.xfast.converters.ValueConverter;

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

    NodeConverter rootNodeConverter;
    private List<NodeConverter> nodeConverters;
    private List<ValueConverter> valueConverters;
    private AnnotationProcessor annotationProcessor;

    public Xfast(Class rootClass, String nodeName) {
        setupMappers();
        setuprConverters();
        annotationProcessor = new AnnotationProcessor(valueConverters, nodeConverters);

        rootNodeConverter = annotationProcessor.processClassTree(nodeName, rootClass);
    }

    private void setupMappers() {

    }

    private void setuprConverters() {

    }


    public Object fromXML(Reader reader) throws XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader xmlreader = factory.createXMLStreamReader(reader);

        Object obj = rootNodeConverter.fromNode(null, xmlreader);

        return obj;
    }

    public void toXML(Object source, Writer writer) {

    }

}
