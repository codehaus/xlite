package si.ptb.xfast;

import si.ptb.xfast.converters.*;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.List;
import java.util.ArrayList;


/**
 * User: peter
 * Date: Feb 25, 2008
 * Time: 11:48:02 AM
 */
public class Xfast {

    RootMapper rootNodeMapper;
    private List<NodeConverter> nodeConverters;
    private List<ValueConverter> valueConverters;
    private AnnotationProcessor annotationProcessor;

    public Xfast(Class rootClass, String nodeName) {
        setupValueConverters();
        setupNodeConverters();
        annotationProcessor = new AnnotationProcessor(valueConverters, nodeConverters);

        rootNodeMapper = annotationProcessor.processClassTree(nodeName, rootClass);
    }

    private void setupNodeConverters() {
        nodeConverters = new ArrayList<NodeConverter>();

    }

    private void setupValueConverters() {
        valueConverters = new ArrayList<ValueConverter>();

        valueConverters.add(new StringConverter());


    }


    public Object fromXML(Reader reader) {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader xmlreader = null;
        try {
            xmlreader = factory.createXMLStreamReader(reader);
        } catch (XMLStreamException e) {
            throw new XfastException("Error reading XML data from Reader", e);
        }

        return rootNodeMapper.getRootObject(xmlreader);
    }

    public void toXML(Object source, Writer writer) {

    }

}
