package si.ptb.xfast;

import deprecated.FieldMapper;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * User: peter
 * Date: Feb 17, 2008
 * Time: 4:47:34 PM
 */
public class AnnotationProcessor {

    /**
     * Processes @XMLnode, @XMLattribute and @XMLtext annotations in a class tree,
     * starting from the given root class and working down through all referenced classes.
     * @param nodeName  Name of the XMl node that this class maps to.
     * @param rootClass A root class in a tree of classes
     * @return
     */
    public static DefaultMapper processClassTree(String nodeName, Class rootClass){
        return processClass(nodeName, rootClass, null);
    }


    /**
     * Processes @XMLnode, @XMLattribute and @XMLtext annotations in a given class.
     * If subnodes are found (@XMLnode), they are processed recursivelly.
     * @param nodeName
     * @param currentClass
     */
    public static DefaultMapper processClass(String nodeName, Class currentClass, Field parentField) {
        DefaultMapper mapper = new DefaultMapper(nodeName, currentClass, parentField);

        // find and process @XMLattribute annotations
        mapper.attributeMappers = processAttributes(currentClass);

        // find and process @XMLvalue annotation
        mapper.valueMapper = processValue(currentClass);

        // find and process @XMLnode annotations
        mapper.nodeMappers = processNodes(currentClass);

        return mapper;
    }

    /**
     * Searches class for fields that have @XMLnode annotation.
     *
     * @param currentClass
     * @return Map of XML node names to {@link deprecated.FieldMapper} objects.
     */
    private static Map<String, DefaultMapper> processNodes(Class currentClass) {
        Map<String, DefaultMapper> map = new HashMap<String, DefaultMapper>();
        XMLnode annotation = null;
        for (Field field : currentClass.getDeclaredFields()) {
            annotation = (XMLnode) field.getAnnotation(XMLnode.class);
            if (annotation != null) {

                // TODO Implement CollectionMapper? Is this the right way?  Yes, probably!
                // CollectionMapper and DefaultMapper should have common interface: NodeMapper?
                // Should ValueConverters also implement NodeMapper interface?

                // TODO if CollectionMapper.isCollection(field.getType()) -> processCollection(name, Class)
                // TODO process primitive types -> isPrimitive(field.getType())

                String nodeName = annotation.value();
                map.put(nodeName, processClass(nodeName, field.getType(), field));  //TODO Implement targetType=Class annotation!
            }
        }
        return map;
    }

    /**
     * Searches class for fields that have @XMLattribute annotation.
     *
     * @param currentClass
     * @return Map of XML attribute names to {@link FieldMapper} objects.
     */
    private static Map<String, FieldMapper> processAttributes(Class currentClass) {
        Map<String, FieldMapper> map = new HashMap<String, FieldMapper>();
        XMLattribute annotation = null;
        for (Field field : currentClass.getDeclaredFields()) {
            annotation = (XMLattribute) field.getAnnotation(XMLattribute.class);
            if (annotation != null) {
                map.put(annotation.value(), new FieldMapper(annotation.value(), field));
            }
        }
        return map;
    }

    /**
     * Searches class for a field that has @XMLtext annotation.
     *
     * @param currentClass
     * @return {@link FieldMapper} for the found field.
     */
    private static FieldMapper processValue(Class currentClass) {
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
        if (found != 1) {
            return null;  // annotation was not found
        }
        return new FieldMapper("", targetField);
    }
}
