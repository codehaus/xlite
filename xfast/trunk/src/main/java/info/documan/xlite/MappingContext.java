package info.documan.xlite;

import info.documan.xlite.converters.NodeConverter;
import info.documan.xlite.converters.ValueConverter;

import javax.xml.namespace.QName;
import java.util.List;

/**
 * @author peter
 */
public class MappingContext {

    public List<NodeConverter> nodeConverters;
    private List<ValueConverter> valueConverters;
    private AnnotationProcessor annotationProcessor;
    private NsContext predefinedNamespaces = new NsContext();

    private SubTreeStore nodeStore;

    public MappingContext(List<NodeConverter> nodeConverters, List<ValueConverter> valueConverters, Class rootClass) {
        this.nodeConverters = nodeConverters;
        this.valueConverters = valueConverters;
        annotationProcessor = new AnnotationProcessor(this);
    }

    public SubTreeStore getNodeStore() {
        return nodeStore;
    }

    public void setNodeStore(SubTreeStore nodeStore) {
        this.nodeStore = nodeStore;
    }

    public NsContext getPredefinedNamespaces() {
        return predefinedNamespaces;
    }

    public void addNamespace(String namespace){
        predefinedNamespaces.addNamespace(namespace);
    }

    public Object processNextNode(Class targetType, XMLSimpleReader reader) {
        // find the converter for given Class
        NodeConverter converter = lookupNodeConverter(targetType);
        return converter.fromNode(reader, this);
    }


    public void processNextObject(Object object, QName nodeName, XMLSimpleWriter writer) {
        // find the converter for given Object
        NodeConverter converter = lookupNodeConverter(object.getClass());
        converter.toNode(object, nodeName, writer, this);
    }

    public ValueConverter lookupValueConverter(Class type) {
        for (ValueConverter valueConverter : valueConverters) {
            if (valueConverter.canConvert(type)) {
                return valueConverter;
            }
        }
        return null;
    }

    public NodeConverter lookupNodeConverter(Class type) {
        for (NodeConverter nodeConverter : nodeConverters) {
            if (nodeConverter.canConvert(type)) {
                return nodeConverter;
            }
        }
        return annotationProcessor.processClass(type);
    }
}
