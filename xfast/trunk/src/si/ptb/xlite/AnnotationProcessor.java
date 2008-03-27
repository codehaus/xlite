package si.ptb.xlite;

import si.ptb.xlite.converters.*;

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
    public RootMapper processClassTree(String nodeName, Class rootClass) {
        return new RootMapper(nodeName, processClass(nodeName, rootClass));
    }


    /**
     * Processes @XMLnode, @XMLattribute and @XMLtext annotations in a given class.
     * If subnodes are found (@XMLnode), they are processed recursivelly.
     *
     * @param nodeName
     * @param currentClass
     */
    private NodeConverter processClass(String nodeName, Class currentClass) {

        NodeConverter nodeConverter = lookupMapper(currentClass);

        //todo change such that ACM is part of mappers hierarchy - list in setupMappers(), implement 
        // Was appropriate NodeConverter found for given class?
        if (nodeConverter != null) {

            return nodeConverter;

        } else {  // default NodeConverter is used -> AnnotatedClassMapper

            AnnotatedClassMapper annotatedClassMapper = new AnnotatedClassMapper(currentClass, nodeName);

            // find and process @XMLattribute annotations
            processAttributes(currentClass, annotatedClassMapper);
            // find and process @XMLvalue annotation
            processValue(currentClass, annotatedClassMapper);
            // find and process @XMLnode annotations
            processNodes(currentClass, annotatedClassMapper);

            return annotatedClassMapper;
        }
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
     * @return Map of XML node names to {@link si.ptb.xlite.converters.ValueMapper} objects.
     */
    private void processNodes(Class currentClass, AnnotatedClassMapper mapper) {
        XMLnode annotation = null;
        for (Field field : currentClass.getDeclaredFields()) {
            annotation = (XMLnode) field.getAnnotation(XMLnode.class);
            if (annotation != null) {

                String nodeName = annotation.value().length() == 0 ? field.getName() : annotation.value();

                // recursive call that builds a tree of Mappers
                NodeMapper submapper = new NodeMapper(field, processClass(nodeName, field.getType()));
                mapper.addNodeConverter(nodeName, submapper);

                String conv = submapper.nodeConverter.getClass().equals(ValueConverterWrapper.class) ?
                        ((ValueConverterWrapper) submapper.nodeConverter).choosenValueConverter.getClass().getSimpleName() :
                        submapper.nodeConverter.getClass().getSimpleName();

                System.out.println(currentClass.getSimpleName() + "." + field.getName() + " node:" + nodeName
                        + " converter:" + conv);
            }
        }
    }

    /**
     * Searches class for fields that have @XMLattribute annotation and creates a ValueMapper for that field
     *
     * @param mapper       AnnotatedClassMapper to which the ValueMapper is referenced
     * @param currentClass Class being inspected for @XMLattribute annotations
     * @return Map of XML attribute names to {@link si.ptb.xlite.converters.ValueMapper} objects.
     */
    private void processAttributes(Class currentClass, AnnotatedClassMapper mapper) {
        XMLattribute annotation = null;
        String attributeName;
        for (Field field : currentClass.getDeclaredFields()) {
            annotation = (XMLattribute) field.getAnnotation(XMLattribute.class);
            if (annotation != null) {
                attributeName = annotation.value().length() == 0 ? field.getName() : annotation.value();
                ValueConverter valueConverter = lookupConverter(field.getType());
                mapper.addAttributeConverter(attributeName, new ValueMapper(field, valueConverter));

                System.out.println(currentClass.getSimpleName() + "." + field.getName() + " attribute:" + attributeName
                        + " converter:" + valueConverter.getClass().getSimpleName());
            }
        }
    }

    /**
     * Searches class for a field that has @XMLtext annotation.
     *
     * @param currentClass
     * @return {@link si.ptb.xlite.converters.ValueMapper} for the found field.
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
            throw new XLiteException("Error: Multiple @XMLtext annotations in class "
                    + currentClass.getName() + ". Max one @XMLtext annotation can be present in a class.");
        }
        if (found == 1) {
            ValueConverter valueConverter = lookupConverter(targetField.getType());
            mapper.setValueConnector(new ValueMapper(targetField, valueConverter));

            System.out.println(currentClass.getSimpleName() + "." + targetField.getName() + " value "
                    + " converter:" + valueConverter.getClass().getSimpleName());
        }
    }

}
