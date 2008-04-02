package si.ptb.xlite;

import si.ptb.xlite.converters.NodeConverter;
import si.ptb.xlite.converters.ValueConverter;

import java.util.List;

/**
 * @author peter
 */
public class MappingContext {

    public List<NodeConverter> nodeConverters;
    public List<ValueConverter> valueConverters;

    public MappingContext(List<NodeConverter> nodeConverters, List<ValueConverter> valueConverters, Class rootClass) {
        this.nodeConverters = nodeConverters;
        this.valueConverters =valueConverters;
        AnnotationProcessor annotationProcessor = new AnnotationProcessor(this);
        annotationProcessor.processClass(rootClass);
    }

    public Object processNextNode(Class targetType, XMLSimpleReader reader) {
        // find the converter for given Class
        NodeConverter converter = lookupNodeConverter(targetType);
        return converter.fromNode(reader, this);
    }

    public Object processNextNode(NodeConverter converter, XMLSimpleReader reader) {
        return converter.fromNode(reader, this);
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
        return null;
    }
}
