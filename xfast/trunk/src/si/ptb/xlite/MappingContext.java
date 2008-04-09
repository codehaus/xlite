package si.ptb.xlite;

import si.ptb.xlite.converters.NodeConverter;
import si.ptb.xlite.converters.ValueConverter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author peter
 */
public class MappingContext {

    public List<NodeConverter> nodeConverters;
    private List<ValueConverter> valueConverters;
    private AnnotationProcessor annotationProcessor;
    private NsContext predefinedNamespaces = new NsContext();

    public NsContext getPredefinedNamespaces() {
        return predefinedNamespaces;
    }

    public void addNamespace(String namespace){
        predefinedNamespaces.addNamespace(namespace);
    }

    public MappingContext(List<NodeConverter> nodeConverters, List<ValueConverter> valueConverters, Class rootClass) {
        this.nodeConverters = nodeConverters;
        this.valueConverters = valueConverters;
        annotationProcessor = new AnnotationProcessor(this);

        // start of annotation processing
        annotationProcessor.processClass(rootClass);
    }

    public Object processNextNode(Class targetType, XMLSimpleReader reader) {
        // find the converter for given Class
        NodeConverter converter = lookupNodeConverter(targetType);
        return converter.fromNode(reader, targetType, this);
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
