package si.ptb.xlite;

import si.ptb.xlite.converters.*;

import java.lang.reflect.Field;

/**
 * User: peter
 * Date: Feb 17, 2008
 * Time: 4:47:34 PM
 */
public class AnnotationProcessor {

    private MappingContext mappingContext;

    public AnnotationProcessor(MappingContext mappingContext) {
        this.mappingContext = mappingContext;
    }

    /**
     * Processes @XMLnode, @XMLattribute and @XMLtext annotations in a given class.
     * If subnodes are found (@XMLnode), they are processed recursivelly.
     *
     * @param currentClass
     * @return
     */
    public NodeConverter processClass(Class currentClass) {

        NodeConverter nodeConverter = lookupNodeConverter(currentClass);

        // Was appropriate NodeConverter found for given class?
        if (nodeConverter != null) {

            return nodeConverter;

        } else {  // default NodeConverter is used -> AnnotatedClassMapper

            AnnotatedClassConverter annotatedClassConverter = new AnnotatedClassConverter(currentClass);

            // find and process @XMLattribute annotations
            processAttributes(currentClass, annotatedClassConverter);
            // find and process @XMLvalue annotation
            processValue(currentClass, annotatedClassConverter);
            // find and process @XMLnode annotations
            processNodes(currentClass, annotatedClassConverter);

            mappingContext.nodeConverters.add(annotatedClassConverter);

            return annotatedClassConverter;
        }
    }

    private NodeConverter lookupNodeConverter(Class type) {
        for (NodeConverter nodeConverter : mappingContext.nodeConverters) {
            if (nodeConverter.canConvert(type)) {
                return nodeConverter;
            }
        }
        return null;
    }

    private ValueConverter lookupValueConverter(Class type) {
        for (ValueConverter valueConverter : mappingContext.valueConverters) {
            if (valueConverter.canConvert(type)) {
                return valueConverter;
            }
        }
        return null;
    }

    /**
     * Searches given class for fields that have @XMLnode annotation.
     *
     * @param currentClass A class to be inspected for @XMLnode annotations.
     * @param converter    AnnotatedClassMapper that coresponds in
     */
    private void processNodes(Class currentClass, AnnotatedClassConverter converter) {
        XMLnode annotation = null;
        for (Field field : currentClass.getDeclaredFields()) {
            annotation = (XMLnode) field.getAnnotation(XMLnode.class);
            if (annotation != null) {

                String nodeName = annotation.value().length() == 0 ? field.getName() : annotation.value();

                NodeConverter subConverter;
                if (annotation.converter().equals(NodeConverter.class)) { // default converter
                    subConverter = processClass(field.getType());

                } else { // custom converter assigned via annotation
                    try {
                        subConverter = annotation.converter().newInstance();

                        // check that assigned converter can actually converto to the target field type
                        if (!subConverter.canConvert(field.getType())) {
                            throw new XliteException("Error: assigned converter type does not match field type.\n" +
                                    "Converter " + subConverter.getClass().getName() + " can not be used to convert " +
                                    "data of type " + field.getType() + ".\n" +
                                    "Please check XML annotations on field '" + field.getName() +
                                    "' in class " + field.getDeclaringClass().getName() + ".");
                        }
                    } catch (InstantiationException e) {
                        throw new XliteException("Could not instantiate converter " + annotation.converter().getName() + ". ", e);
                    } catch (IllegalAccessException e) {
                        throw new XliteException("Could not instantiate converter " + annotation.converter().getName() + ". ", e);
                    }
                }
                
                NodeMapper submapper = new NodeMapper(field, subConverter, mappingContext);
                converter.addNodeConverter(nodeName, submapper);

                String conv = submapper.nodeConverter.getClass().equals(ValueConverterWrapper.class) ?
                        ((ValueConverterWrapper) submapper.nodeConverter).valueConverter.getClass().getSimpleName() :
                        submapper.nodeConverter.getClass().getSimpleName();

                System.out.println(currentClass.getSimpleName() + "." + field.getName() + " node:" + nodeName
                        + " converter:" + conv);
            }
        }
    }

    /**
     * Searches class for fields that have @XMLattribute annotation and creates a ValueMapper for that field
     *
     * @param converter    AnnotatedClassMapper to which the ValueMapper is referenced
     * @param currentClass Class being inspected for @XMLattribute annotations
     * @return Map of XML attribute names to {@link si.ptb.xlite.converters.ValueMapper} objects.
     */
    private void processAttributes(Class currentClass, AnnotatedClassConverter converter) {
        XMLattribute annotation = null;
        String attributeName;
        for (Field field : currentClass.getDeclaredFields()) {
            annotation = (XMLattribute) field.getAnnotation(XMLattribute.class);
            if (annotation != null) {
                attributeName = annotation.value().length() == 0 ? field.getName() : annotation.value();

                // find the appropriate converter
                ValueConverter valueConverter;
                if (annotation.converter().equals(ValueConverter.class)) {  // default converter
                    valueConverter = lookupValueConverter(field.getType());

                } else {  // custom converter assigned via annotation
                    try {
                        valueConverter = annotation.converter().newInstance();

                        // check that assigned converter can actually converto to the target field type
                        if (!valueConverter.canConvert(field.getType())) {
                            throw new XliteException("Error: assigned converter type does not match field type.\n" +
                                    "Converter " + valueConverter.getClass().getName() + " can not be used to convert " +
                                    "data of type " + field.getType() + ".\n" +
                                    "Please check XML annotations on field '" + field.getName() +
                                    "' in class " + field.getDeclaringClass().getName() + ".");
                        }
                    } catch (InstantiationException e) {
                        throw new XliteException("Could not instantiate converter " + annotation.converter().getName() + ". ", e);
                    } catch (IllegalAccessException e) {
                        throw new XliteException("Could not instantiate converter " + annotation.converter().getName() + ". ", e);
                    }
                }

                converter.addAttributeConverter(attributeName, new ValueMapper(field, valueConverter));

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
    private void processValue(Class currentClass, AnnotatedClassConverter converter) {
        Field targetField = null;
        int found = 0;
        XMLtext annotation = null, targetAnnotation = null;
        for (Field field : currentClass.getDeclaredFields()) {
            annotation = (XMLtext) field.getAnnotation(XMLtext.class);
            if (annotation != null) {
                found++;
                targetField = field;
                targetAnnotation = annotation;
            }
        }
        if (found > 1) {
            throw new XliteException("Error: Multiple @XMLtext annotations in class "
                    + currentClass.getName() + ". Max one @XMLtext annotation can be present in a class.");
        }
        if (found == 1) {

            // find the appropriate converter
            ValueConverter valueConverter;
            if (targetAnnotation.converter().equals(ValueConverter.class)) {  // default converter
                valueConverter = lookupValueConverter(targetField.getType());

                // check that assigned converter can actually converto to the target field type
                if (!valueConverter.canConvert(targetField.getType())) {
                    throw new XliteException("Error: assigned converter type does not match field type.\n" +
                            "Converter " + valueConverter.getClass().getName() + " can not be used to convert " +
                            "data of type " + targetField.getType() + ".\n" +
                            "Please check XML annotations on field '" + targetField.getName() +
                            "' in class " + targetField.getDeclaringClass().getName() + ".");
                }
            } else {  // custom converter assigned via annotation
                try {
                    valueConverter = targetAnnotation.converter().newInstance();
                } catch (InstantiationException e) {
                    throw new XliteException("Could not instantiate converter " + targetAnnotation.converter().getName() + ". ", e);
                } catch (IllegalAccessException e) {
                    throw new XliteException("Could not instantiate converter " + targetAnnotation.converter().getName() + ". ", e);
                }
            }

            converter.setValueMapper(new ValueMapper(targetField, valueConverter));

            System.out.println(currentClass.getSimpleName() + "." + targetField.getName() + " value "
                    + " converter:" + valueConverter.getClass().getSimpleName());
        }
    }

}
