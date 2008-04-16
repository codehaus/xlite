package si.ptb.xlite.converters;

import si.ptb.xlite.*;

import java.lang.reflect.Field;
import java.util.Collection;

/**
 * @author peter
 */
public class NodeMapper {

    private Field targetField;
    public NodeConverter nodeConverter;    //todo make private
    private CollectionConverting collectionConverter;
    private MappingContext mappingContext;
    private boolean collectionNotInitialized = true;
    private Class itemType;

    public NodeMapper(Field targetField, NodeConverter nodeConverter, MappingContext mappingContext) {
        this.targetField = targetField;
        this.nodeConverter = nodeConverter;
        this.mappingContext = mappingContext;

        // is this a CollectionConverter?
        if (CollectionConverting.class.isAssignableFrom(nodeConverter.getClass())) {
            this.collectionConverter = (CollectionConverting) nodeConverter;
            XMLnode annotation = (XMLnode) targetField.getAnnotation(XMLnode.class);
            this.itemType = annotation.itemType();
            if (this.itemType == Object.class) {
                throw new XliteException("Error: collection in class " + targetField.getDeclaringClass().getSimpleName() +
                        " does not have a target type defined. " +
                        "When @XMLnode annotation is used on collection, a 'targetType' value must be defined.");
            }
        }
    }

    public void setValue(Object targetObject, XMLSimpleReader reader) {
        if (collectionConverter == null) {
            setFieldValue(targetObject, reader);
        } else {
            collectionAddItem(targetObject, reader);
        }
    }

    private void collectionAddItem(Object targetObject, XMLSimpleReader reader) {

        try {
            Collection collection = (Collection) targetField.get(targetObject);
            if (collection == null) {
                collection = collectionConverter.initializeCollection(targetField.getType());
                targetField.set(targetObject, collection);
            }
            Object value = nodeConverter.fromNode(reader, itemType, mappingContext);
            collectionConverter.addItem(collection, value);
        } catch (IllegalAccessException e) {
            throw new XliteException("Field could not be read! ", e);
        }
    }

    private void setFieldValue(Object targetObject, XMLSimpleReader reader) {
        try {
            Object value = nodeConverter.fromNode(reader, targetField.getType(), mappingContext);
            targetField.set(targetObject, value);
        } catch (IllegalAccessException e) {
            throw new XliteException("Field could not be set! ", e);
        }
    }

    public void writeNode(Object object, XMLSimpleWriter writer) {
        Object targetObject = null;
        try {
            targetObject = targetField.get(object);
            nodeConverter.toNode(targetObject, writer, mappingContext);
        } catch (IllegalAccessException e) {
            throw new XliteException("Field could not be read! ", e);
        }
    }
}
