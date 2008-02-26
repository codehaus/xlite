package si.ptb.xfast;

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
     * Processes @XMLnode, @XMLattribute and @XMLtext annotations in a given class.
     * If subnodes are found (@XMLnode), they are processed recursivelly.
     * @param nodeName
     * @param currentClass
     */
    public static ClassMapper processClass(String nodeName, Class currentClass) {
        ClassMapper mapper = new ClassMapper(nodeName, currentClass);

        // find and process @XMLattribute annotations
        mapper.attributeMappers = processAttributes(currentClass);

        // find and process @XMLvalue annotation
        mapper.valueMapper = processValue(currentClass);

        // find and process @XMLnode annotations
        mapper.nodeMappers = processNodes(currentClass);

        // process subnodes recursivelly
//        for (Map.Entry<String, ClassMapper> entry : mapper.nodeMappers.entrySet()) {
//            ClassMapper submapper = new ClassMapper(nodeName, entry.getValue().);
//            processClass(submapper, entry.getValue().targetClass, entry.getValue().nodeName);
//        }

        return mapper;
    }

    /**
     * Searches class for fields that have @XMLnode annotation.
     *
     * @param currentClass
     * @return Map of XML node names to {@link FieldMapper} objects.
     */
    private static Map<String, ClassMapper> processNodes(Class currentClass) {
        Map<String, ClassMapper> map = new HashMap<String, ClassMapper>();
        int found = 0;
        XMLnode annotation = null;
        for (Field field : currentClass.getDeclaredFields()) {
            annotation = (XMLnode) field.getAnnotation(XMLnode.class);
            String nodeName = annotation.value();
            if (annotation != null) {
                found++;
                map.put(nodeName, processClass(nodeName, field.getType()));  //TODO Implement targetType=Class annotation!
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
        int found = 0;
        XMLattribute annotation = null;
        for (Field field : currentClass.getDeclaredFields()) {
            annotation = (XMLattribute) field.getAnnotation(XMLattribute.class);
            if (annotation != null) {
                found++;
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
            throw new FastConverterException("Error: Multiple @XMLtext annotations in class "
                    + currentClass.getName() + ". Max one @XMLtext annotation can be present in a class.");
        }
        if (found != 1) {
            return null;  // annotation was not found
        }
        return new FieldMapper("", targetField);
    }
}
