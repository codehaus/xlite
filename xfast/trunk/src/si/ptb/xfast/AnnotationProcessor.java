package si.ptb.xfast;

import si.ptb.xfast.converters.ValueConverter;
import si.ptb.xfast.converters.ValueMapper;
import si.ptb.xfast.converters.NodeConverter;
import si.ptb.xfast.AnnotatedClassMapper;

import java.lang.reflect.Field;
import java.util.List;

/**
 * User: peter
 * Date: Feb 17, 2008
 * Time: 4:47:34 PM
 */
public class AnnotationProcessor {

    private List<NodeConverter> nodeConverters;
    private List<ValueConverter> valueConverters;

    public AnnotationProcessor(List<ValueConverter> valueConverters, List<NodeConverter> nodeConverters) {
        this.valueConverters = valueConverters;
        this.nodeConverters = nodeConverters;
    }

    /**
     * Processes @XMLnode, @XMLattribute and @XMLtext annotations in a class tree,
     * starting from the given root class and working down through all referenced classes.
     *
     * @param nodeName  Name of the XMl node that this class maps to.
     * @param rootClass A root class in a tree of classes
     * @return
     */
    public NodeConverter processClassTree(String nodeName, Class rootClass) {
        return processClass(nodeName, rootClass, null);
    }


    /**
     * Processes @XMLnode, @XMLattribute and @XMLtext annotations in a given class.
     * If subnodes are found (@XMLnode), they are processed recursivelly.
     *
     * @param nodeName
     * @param currentClass
     */
    private NodeConverter processClass(String nodeName, Class currentClass, Field parentField) {

        NodeConverter nodeConverter = lookupMapper(currentClass);

        // is a subclass of AnnotatedClassMapper?
        if (nodeConverter.getClass().isAssignableFrom(AnnotatedClassMapper.class)) {
            AnnotatedClassMapper annotatedClassMapper = (AnnotatedClassMapper) nodeConverter;

            // find and process @XMLattribute annotations
            processAttributes(currentClass, annotatedClassMapper);

            // find and process @XMLvalue annotation
            processValue(currentClass, annotatedClassMapper);

            // find and process @XMLnode annotations
            processNodes(currentClass, annotatedClassMapper);
        }

        return nodeConverter;
    }

    private NodeConverter lookupMapper(Class type) {
        for (NodeConverter nodeConverter : nodeConverters) {
            NodeConverter converter = nodeConverter.getConverter(type);
            if (converter != null) {
                return converter;
            }
        }
        return null;
    }

    private ValueConverter lookupConverter(Class type) {
        for (ValueConverter valueConverter : valueConverters) {
            if (valueConverter.canConvert(type)) {
                return valueConverter;
            }
        }
        return null;
    }

    /**
     * Searches class for fields that have @XMLnode annotation.
     *
     * @param currentClass
     * @return Map of XML node names to {@link si.ptb.xfast.converters.ValueMapper} objects.
     */
    private void processNodes(Class currentClass, AnnotatedClassMapper mapper) {
        XMLnode annotation = null;
        for (Field field : currentClass.getDeclaredFields()) {
            annotation = (XMLnode) field.getAnnotation(XMLnode.class);
            if (annotation != null) {

                String nodeName = annotation.value();

                // recursive call that builds a tree of Mappers
                NodeConverter submapper = processClass(nodeName, field.getType(), field);
                mapper.addNodeMapper(nodeName, submapper);
            }
        }
    }

    /**
     * Searches class for fields that have @XMLattribute annotation and creates a ValueMapper for that field
     *
     * @param mapper AnnotatedClassMapper to which the ValueMapper
     * @param currentClass
     * @return Map of XML attribute names to {@link si.ptb.xfast.converters.ValueMapper} objects.
     */
    private void processAttributes(Class currentClass, AnnotatedClassMapper mapper) {
        XMLattribute annotation = null;
        for (Field field : currentClass.getDeclaredFields()) {
            annotation = (XMLattribute) field.getAnnotation(XMLattribute.class);
            if (annotation != null) {
                ValueConverter valueConverter = lookupConverter(field.getType());
                mapper.addAttributeConverter(annotation.value(), new ValueMapper(annotation.value(), field, valueConverter));
            }
        }
    }

    /**
     * Searches class for a field that has @XMLtext annotation.
     *
     * @param currentClass
     * @return {@link si.ptb.xfast.converters.ValueMapper} for the found field.
     */
    private void processValue(Class currentClass, AnnotatedClassMapper mapper) {
        Field targetField = null;
        int found = 0;
        XMLtext annotation = null;
        for (Field field : currentClass.getDeclaredFields()) {
            annotation = (XMLtext) field.getAnnotation(XMLtext.class);
            if (annotation != null) {
                found++;
                targetField = field;
            }
        }
        if (found > 1) {
            throw new XfastException("Error: Multiple @XMLtext annotations in class "
                    + currentClass.getName() + ". Max one @XMLtext annotation can be present in a class.");
        }
        if (found == 1) {
            ValueConverter valueConverter = lookupConverter(targetField.getType());
            mapper.setValueConverter(new ValueMapper("", targetField, valueConverter));
        }
    }
    
}
