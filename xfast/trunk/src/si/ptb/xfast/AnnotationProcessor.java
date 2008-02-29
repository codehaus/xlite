package si.ptb.xfast;

import si.ptb.xfast.FieldMapper;

import java.lang.reflect.Field;
import java.util.List;

/**
 * User: peter
 * Date: Feb 17, 2008
 * Time: 4:47:34 PM
 */
public class AnnotationProcessor {

    List<Mapper> mappers;
    List<ValueConverter> converters;

    public AnnotationProcessor(List<ValueConverter> converters, List<Mapper> mappers) {
        this.converters = converters;
        this.mappers = mappers;
    }

    /**
     * Processes @XMLnode, @XMLattribute and @XMLtext annotations in a class tree,
     * starting from the given root class and working down through all referenced classes.
     *
     * @param nodeName  Name of the XMl node that this class maps to.
     * @param rootClass A root class in a tree of classes
     * @return
     */
    public Mapper processClassTree(String nodeName, Class rootClass) {
        return processClass(nodeName, rootClass, null);
    }


    /**
     * Processes @XMLnode, @XMLattribute and @XMLtext annotations in a given class.
     * If subnodes are found (@XMLnode), they are processed recursivelly.
     *
     * @param nodeName
     * @param currentClass
     */
    private Mapper processClass(String nodeName, Class currentClass, Field parentField) {

        // TODO introduce mapper/converter lookup
        Mapper mapper = lookupMapper(nodeName, currentClass);

        // is a subclass of AnnotatedMapper?
        if (mapper.getClass().isAssignableFrom(AnnotatedMapper.class)) {
            AnnotatedMapper annotatedMapper = (AnnotatedMapper) mapper;

            // find and process @XMLattribute annotations
            processAttributes(currentClass, annotatedMapper);

            // find and process @XMLvalue annotation
            processValue(currentClass, annotatedMapper);

            // find and process @XMLnode annotations
            processNodes(currentClass, annotatedMapper);
        }

        return mapper;
    }

    private Mapper lookupMapper(String nodeName, Class type) {
        for (Mapper mapper : mappers) {
            if (mapper.canMapNode(type)) {
               return mapper;
            }
        }
        return null;
    }

    /**
     * Searches class for fields that have @XMLnode annotation.
     *
     * @param currentClass
     * @return Map of XML node names to {@link FieldMapper} objects.
     */
    private void processNodes(Class currentClass, AnnotatedMapper mapper) {
        XMLnode annotation = null;
        for (Field field : currentClass.getDeclaredFields()) {
            annotation = (XMLnode) field.getAnnotation(XMLnode.class);
            if (annotation != null) {

                String nodeName = annotation.value();

                // recursive call that builds a tree of Mappers
                Mapper submapper = processClass(nodeName, field.getType(), field);
                mapper.addNodeMapper(nodeName, submapper);
            }
        }
    }

    /**
     * Searches class for fields that have @XMLattribute annotation and creates a ValueConverter for that field
     *
     * @param mapper       AnnotatedMapper to which the
     * @param currentClass
     * @return Map of XML attribute names to {@link FieldMapper} objects.
     */
    private void processAttributes( Class currentClass, AnnotatedMapper mapper) {
        XMLattribute annotation = null;
        for (Field field : currentClass.getDeclaredFields()) {
            annotation = (XMLattribute) field.getAnnotation(XMLattribute.class);
            if (annotation != null) {
                mapper.addAttributeConverter(annotation.value(), new FieldMapper(annotation.value(), field));
            }
        }

    }

    /**
     * Searches class for a field that has @XMLtext annotation.
     *
     * @param currentClass
     * @return {@link FieldMapper} for the found field.
     */
    private void processValue(Class currentClass, AnnotatedMapper mapper) {
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
            mapper.setValueConverter(new FieldMapper("", targetField));
        }
    }
}
