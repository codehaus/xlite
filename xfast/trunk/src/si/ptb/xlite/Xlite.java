package si.ptb.xlite;

import si.ptb.xlite.converters.*;

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
public class Xlite {

    RootMapper rootNodeMapper;
    private List<NodeConverter> nodeConverters;
    private List<ValueConverter> valueConverters;

    public Xlite(Class rootClass, String nodeName) {
        setupValueConverters();
        setupNodeConverters();
        AnnotationProcessor annotationProcessor = new AnnotationProcessor(valueConverters, nodeConverters);

        rootNodeMapper = annotationProcessor.processClassTree(nodeName, rootClass);
    }

    private void setupNodeConverters() {
        nodeConverters = new ArrayList<NodeConverter>();

        // wraps all ValueConverters so that they can be used as NodeConverters
        nodeConverters.add(new ValueConverterWrapper(valueConverters));

    }

    private void setupValueConverters() {
        valueConverters = new ArrayList<ValueConverter>();

        valueConverters.add(new StringConverter());
        valueConverters.add(new IntConverter());
        valueConverters.add(new DoubleConverter());
        valueConverters.add(new FloatConverter());
        valueConverters.add(new LongConverter());
        valueConverters.add(new ShortConverter());
        valueConverters.add(new BooleanConverter());
        valueConverters.add(new ByteConverter());
        valueConverters.add(new CharConverter());


    }


    public Object fromXML(Reader reader) {

        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader xmlreader = null;
        try {
            xmlreader = factory.createXMLStreamReader(reader);
        } catch (XMLStreamException e) {
            throw new XLiteException("Error reading XML data from Reader", e);
        }
        XMLSimpleReader simpleReader = new XMLSimpleReader(xmlreader);

        return rootNodeMapper.getRootObject(simpleReader);
    }

    public void toXML(Object source, Writer writer) {

    }

}
