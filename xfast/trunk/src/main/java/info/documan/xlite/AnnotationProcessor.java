package info.documan.xlite;

import info.documan.xlite.converters.*;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import java.lang.reflect.Field;
import java.util.Arrays;

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

        AnnotatedClassConverter annotatedClassConverter = new AnnotatedClassConverter(currentClass);
        annotatedClassConverter.setNodeStorage(mappingContext.getNodeStore());

        // find and process @XMLnamespaces annotation
        processClassNamespaces(currentClass, annotatedClassConverter);
        // find and process @XMLattribute annotations
        processAttributes(currentClass, annotatedClassConverter);
        // find and process @XMLvalue annotation
        processValue(currentClass, annotatedClassConverter);
        // find and process @XMLnode annotations
        processNodes(currentClass, annotatedClassConverter);

        mappingContext.nodeConverters.add(annotatedClassConverter);

        return annotatedClassConverter;
    }

    private void processClassNamespaces(Class currentClass, AnnotatedClassConverter annotatedClassConverter) {
        NsContext classNS = new NsContext();
        XMLnamespaces nsAnnotation = (XMLnamespaces) currentClass.getAnnotation(XMLnamespaces.class);
        if (nsAnnotation != null && nsAnnotation.value().length != 0) {
            boolean defaultFound = false;
            for (int i = 0; i < nsAnnotation.value().length; i++) {
                classNS.addNamespace(nsAnnotation.value()[i]);
            }
        }
        annotatedClassConverter.setClassNamespaces(classNS);
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
        return mappingContext.lookupValueConverter(type);
    }

    /**
     * Searches given class for fields that have @XMLnode annotation.
     *
     * @param currentClass A class to be inspected for @XMLnode annotations.
     * @param converter    AnnotatedClassMapper that coresponds in
     */
    private void processNodes(Class currentClass, AnnotatedClassConverter converter) {

        for (Field field : currentClass.getDeclaredFields()) {

            // find the converter by the field type
            NodeConverter converterByType = mappingContext.lookupNodeConverter(field.getType());

            // get converter for the class that the field references
            NodeConverter fieldConverter = null;

            // if target field is a collection, then a collection converter must be defined
            CollectionConverting collectionConverter = null;
            if (CollectionConverting.class.isAssignableFrom(converterByType.getClass())) {
                collectionConverter = (CollectionConverting) converterByType;
            }

            // init a mapper
            NodeMapper fieldMapper = new NodeMapper(field, collectionConverter, mappingContext);

            // collect all @XMLnode annotations in a single array for easier processing
            XMLnode[] annotations = new XMLnode[0];
            XMLnodes multiAnno = (XMLnodes) field.getAnnotation(XMLnodes.class);
            if (multiAnno != null && multiAnno.value().length != 0) {
                annotations = multiAnno.value();
            }
            XMLnode singleAnno = (XMLnode) field.getAnnotation(XMLnode.class);
            if (singleAnno != null) {
                annotations = Arrays.copyOf(annotations, annotations.length + 1);
                annotations[annotations.length - 1] = singleAnno;
            }

            // process @XMLnode annotations
            for (XMLnode annotation : annotations) {
                Class itemType = annotation.itemType();
                Class<? extends NodeConverter> annotatedConverter = annotation.converter();

                // set to default values according to annotations
                if (itemType.equals(Object.class)) {
                    itemType = null;
                }
                if (annotatedConverter.equals(NodeConverter.class)) {
                    annotatedConverter = null;
                }

                // target field is a collection, so a target converter is a collection converter
                if (collectionConverter != null) {

                    if (annotatedConverter != null) {
                        throw new XliteException("Error: Can  not assign converter for collection " + field.getDeclaringClass().getName() +
                                " in class " + field.getDeclaringClass().getSimpleName() +
                                "When @XMLnode annotation is used on a collection, 'converter' value can not be used. " +
                                "Use 'itemType' instead.");
                    }

                    // if it's a collection, then @XMLnode must have "itemType" value defined
                    if (itemType == null) {
                        throw new XliteException("Error: Can  not assign converter for collection " + field.getDeclaringClass().getName() +
                                " in class " + field.getDeclaringClass().getSimpleName() +
                                "When @XMLnode annotation is used on a collection, 'itemType' value must be defined.");
                    }
                    fieldConverter = null;

                } else { // target field is a normal field (not a collection)

                    if (itemType != null) {
                        throw new XliteException("Error: Wrong @XMLnode annotation value on field " + fieldConverter.getClass().getName() +
                                "in class " + field.getDeclaringClass().getName() + ". @XMLnode 'itemType' can only be used on " +
                                "field types that implement Collection.");
                    }

                    // was custom converter assigned via annotation?
                    if (annotatedConverter != null) {
                        try {
                            fieldConverter = annotatedConverter.newInstance();

                            // check that assigned converter can actually convert to the target field type
                            if (collectionConverter == null && !fieldConverter.canConvert(field.getType())) {
                                throw new XliteException("Error: assigned converter type does not match field type.\n" +
                                        "Converter " + fieldConverter.getClass().getName() + " can not be used to convert " +
                                        "data of type " + field.getType() + ".\n" +
                                        "Please check XML annotations on field '" + field.getName() +
                                        "' in class " + field.getDeclaringClass().getName() + ".");
                            }

                        } catch (InstantiationException e) {
                            throw new XliteException("Could not instantiate converter " + annotation.converter().getName() + ". ", e);
                        } catch (IllegalAccessException e) {
                            throw new XliteException("Could not instantiate converter " + annotation.converter().getName() + ". ", e);
                        }

                    } else {
                        // converter was not declared via annotation, so we just use a converter derived from field type
                        fieldConverter = converterByType;
                    }
                }

                // get QName that field maps to
                String nodename = annotation.value().length() != 0 ? annotation.value() :
                        (annotation.name().length() != 0 ? annotation.name() : field.getName());
                NsContext fieldNS = getFieldNamespaces(field);
                NsContext classNS = converter.getClassNamespaces( );
                QName qname = getQName(nodename, fieldNS, classNS);

                if (fieldConverter != null) {
                    fieldMapper.setConverter(fieldConverter);
                }
                if (itemType != null) {
                    fieldMapper.addMapping(qname, itemType);
                }
                converter.addNodeMapper(qname, fieldMapper);

//                String conv = fieldMapper.nodeConverter.getClass().equals(ValueConverterWrapper.class) ?
//                        ((ValueConverterWrapper) fieldMapper.nodeConverter).valueConverter.getClass().getSimpleName() :
//                        fieldMapper.nodeConverter.getClass().getSimpleName();
//
//                System.out.println(currentClass.getSimpleName() + "." + field.getName() + " node:" + nodename + " converter:" + conv);

            }
        }
    }

    private QName getQName(String nodeName, NsContext fieldNS, NsContext classNS) {

        // split xml node name into prefix and local part
        int index = nodeName.indexOf(':');
        String prefix, localPart;
        if (index > 0) {  // with prefix ("prefix:localpart")
            prefix = nodeName.substring(0, index);
            localPart = nodeName.substring(index + 1, nodeName.length());

        } else if (index == 0) { // empty prefix (no prefix defined - e.g ":nodeName")
            prefix = XMLConstants.DEFAULT_NS_PREFIX;
            localPart = nodeName.substring(1, nodeName.length());

        } else { // no prefix given
            prefix = XMLConstants.DEFAULT_NS_PREFIX;
            localPart = nodeName;
        }

        String fieldNsURI = fieldNS.getNamespaceURI(prefix);
        String classNsURI = classNS.getNamespaceURI(prefix);
        String predefinedNsURI = mappingContext.getPredefinedNamespaces().getNamespaceURI(prefix);

        // choose the namespaceURI that is not null from field, class, predefined or
        // finally DEFAULT_NS_PREFIX (in that order)
        String theURI = fieldNsURI != null ? fieldNsURI :
                (classNsURI != null ? classNsURI :
                        (predefinedNsURI != null ? predefinedNsURI : XMLConstants.DEFAULT_NS_PREFIX));
//        System.out.println("namespace URI=" + theURI + " local=" + localPart + " prefix=" + prefix);
        return new QName(theURI, localPart, prefix);
    }


    private NsContext getFieldNamespaces(Field field) {

        NsContext fieldNS = new NsContext();
        XMLnamespaces nsAnnotation = (XMLnamespaces) field.getAnnotation(XMLnamespaces.class);
        if (nsAnnotation != null && nsAnnotation.value().length != 0) {
            boolean defaultFound = false;
            for (int i = 0; i < nsAnnotation.value().length; i++) {
                fieldNS.addNamespace(nsAnnotation.value()[i]);
            }
        }
        return fieldNS;
    }

    /**
     * Searches class for fields that have @XMLattribute annotation and creates a ValueMapper for that field
     *
     * @param converter    AnnotatedClassMapper to which the ValueMapper is referenced
     * @param currentClass Class being inspected for @XMLattribute annotations
     * @return Map of XML attribute names to {@link info.documan.xlite.converters.ValueMapper} objects.
     */
    private void processAttributes(Class currentClass, AnnotatedClassConverter converter) {
        XMLattribute annotation = null;
        String attributeName;
        for (Field field : currentClass.getDeclaredFields()) {
            annotation = (XMLattribute) field.getAnnotation(XMLattribute.class);
            if (annotation != null) {

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

                // get QName that field maps to
                String nodename = annotation.value().length() != 0 ? annotation.value() :
                        (annotation.name().length() != 0 ? annotation.name() : field.getName());
                QName qname = getQName(nodename, getFieldNamespaces(field), converter.getClassNamespaces());

                // SPECIAL CASE!!!
                // XML attributes with empty prefix DO NOT belong to default namespace
                if (qname.getPrefix().equals(XMLConstants.DEFAULT_NS_PREFIX)) {
                    String localPart = qname.getLocalPart();
                    qname = new QName(localPart);
                }

                converter.addAttributeConverter(qname, new ValueMapper(field, valueConverter));

                System.out.println(currentClass.getSimpleName() + "." + field.getName() + " attribute:" + qname
                        + " converter:" + valueConverter.getClass().getSimpleName());
            }
        }
    }

    /**
     * Searches class for a field that has @XMLtext annotation.
     *
     * @param currentClass
     * @return {@link info.documan.xlite.converters.ValueMapper} for the found field.
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
