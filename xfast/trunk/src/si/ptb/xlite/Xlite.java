package si.ptb.xlite;

import si.ptb.xlite.converters.*;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.*;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;


/**
 * User: peter
 * Date: Feb 25, 2008
 * Time: 11:48:02 AM
 */
public class Xlite {

    RootMapper rootNodeMapper;
    private List<NodeConverter> nodeConverters;
    private List<ValueConverter> valueConverters;
    private MappingContext mappingContext;

    private boolean initialized = false;
    public boolean isStoringUnknownNodes;
    private Class rootClass;
    private String rootNodeName;

    private String rootNodeNS = XMLConstants.NULL_NS_URI;
    private boolean isPrettyPrint = true;

    public Xlite(Class rootClass, String nodeName) {
        this(rootClass, nodeName, null);
    }

    public Xlite(Class rootClass, String nodeName, String namespaceURI) {
        setupValueConverters();
        setupNodeConverters();
        this.rootClass = rootClass;
        this.rootNodeName = nodeName;
        this.rootNodeNS = namespaceURI;
        this.mappingContext = new MappingContext(nodeConverters, valueConverters, rootClass);
    }

    public void setPrettyPrint(boolean prettyPrint){
        this.isPrettyPrint = prettyPrint;
    }

    private void initialize() {

        // initialize storing unknown nodes
        if (isStoringUnknownNodes) {
            mappingContext.setNodeStore(new SubTreeStore(1000000));
        } else {
            mappingContext.setNodeStore(null);
        }

        // one-time initialization
        if (!initialized) {

            // split xml node name into prefix and local part
            int index = rootNodeName.indexOf(':');
            String rootNodeLocalpart;
            String rootNodePrefix;
            if (index > 0) {  // with prefix ("prefix:localpart")
                rootNodePrefix = rootNodeName.substring(0, index);
                rootNodeLocalpart = rootNodeName.substring(index + 1, rootNodeName.length());

            } else if (index == 0) { // empty prefix (no prefix defined - e.g ":nodeName")
                rootNodePrefix = XMLConstants.DEFAULT_NS_PREFIX;
                rootNodeLocalpart = rootNodeName.substring(1, rootNodeName.length());

            } else { // no prefix given
                rootNodePrefix = XMLConstants.DEFAULT_NS_PREFIX;
                rootNodeLocalpart = rootNodeName;
            }

            // namespace  of root element is not defined
            if (rootNodeNS == null) {
                rootNodeNS = mappingContext.getPredefinedNamespaces().getNamespaceURI(rootNodePrefix);
            }
            this.rootNodeMapper = new RootMapper(new QName(rootNodeNS, rootNodeLocalpart, rootNodePrefix), rootClass, mappingContext);
            initialized = true;
        }
    }

    private void setupNodeConverters() {
        nodeConverters = new ArrayList<NodeConverter>();
        nodeConverters.add(new CollectionConverter());

        // wraps every ValueConverter so that it can be used as a NodeConverter
        for (ValueConverter valueConverter : valueConverters) {
            nodeConverters.add(new ValueConverterWrapper(valueConverter));
        }
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

    public void addNamespace(String namespace) {
        mappingContext.addNamespace(namespace);
    }


    public Object fromXML(Reader reader) {
        initialize();

        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader xmlreader = null;
        try {
            xmlreader = factory.createXMLStreamReader(reader);
        } catch (XMLStreamException e) {
            throw new XliteException("Error initalizing XMLStreamReader", e);
        }
        XMLSimpleReader simpleReader = new XMLSimpleReader(xmlreader, isStoringUnknownNodes);

        return rootNodeMapper.getRootObject(simpleReader);
    }

    public void toXML(Object source, Writer writer) {
        initialize();

        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        factory.setProperty("javax.xml.stream.isRepairingNamespaces", true);
        XMLStreamWriter parser = null;
        try {
            parser = factory.createXMLStreamWriter(writer);
        } catch (XMLStreamException e) {
            throw new XliteException("Error initalizing XMLStreamWriter", e);
        }
        XMLSimpleWriter simpleWriter = new XMLSimpleWriter(parser, new XmlStreamSettings(), isPrettyPrint);

        rootNodeMapper.toXML(source, simpleWriter);

    }


    public SubTreeStore getNodeStore() {
        return mappingContext.getNodeStore();
    }
}
