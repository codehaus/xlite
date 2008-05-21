package info.documan.xlite.converters;

import info.documan.xlite.MappingContext;
import info.documan.xlite.XMLSimpleReader;
import info.documan.xlite.XMLSimpleWriter;
import info.documan.xlite.XliteException;

import javax.xml.namespace.QName;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author peter
 */
public class NodeMapper {

    private Field targetField;
    private CollectionConverting collectionConverter;
    private MappingContext mappingContext;
    public NodeConverter nodeConverter;
    private Map<Class, QName> itemTypes = new HashMap<Class, QName>();
    private Map<QName, NodeConverter> converterCache = new HashMap<QName, NodeConverter>();

    public NodeMapper(Field targetField, CollectionConverting collectionConverter, MappingContext mappingContext) {
        this.targetField = targetField;
        this.mappingContext = mappingContext;
        this.collectionConverter = collectionConverter;
    }

    public void setConverter(NodeConverter fieldConverter) {
        this.nodeConverter = fieldConverter;
    }

    public void addMapping(QName nodeName, Class itemType) {
        NodeConverter converter = mappingContext.lookupNodeConverter(itemType);
        this.itemTypes.put(itemType, nodeName);
        this.converterCache.put(nodeName, converter);
    }

    public void setValue(QName nodeName, Object targetObject, XMLSimpleReader reader) {
        if (collectionConverter == null) {
            setFieldValue(nodeName, targetObject, reader);
        } else {
            collectionAddItem(nodeName, targetObject, reader);
        }
    }

    private void collectionAddItem(QName nodeName, Object targetObject, XMLSimpleReader reader) {
        try {
            Collection collection = (Collection) targetField.get(targetObject);

            // initialize collection if needed
            if (collection == null) {
                collection = collectionConverter.initializeCollection(targetField.getType());
                targetField.set(targetObject, collection);
            }

            // find the converter for given node name
            NodeConverter converter = converterCache.get(nodeName);
            if (converter == null) {
                throw new XliteException("Error: could not find converter for node: "+ nodeName+
                        " in collection " + collection.getClass().getName() +
                        " in class " + collection.getClass().getEnclosingClass() +
                        ". Collection contains element types that are not defined in @XMLnode annotation.");
            }

            Object value = converter.fromNode(reader, mappingContext);
            collectionConverter.addItem(collection, value);
        } catch (IllegalAccessException e) {
            throw new XliteException("Field value could not be set! ", e);
        }
    }

    private void setFieldValue(QName nodeName, Object targetObject, XMLSimpleReader reader) {
        try {
//            Object value = nodeConverter.fromNode(reader, targetField.getType(), mappingContext);
            Object value = nodeConverter.fromNode(reader, mappingContext);
            targetField.set(targetObject, value);
        } catch (IllegalAccessException e) {
            throw new XliteException("Field value could not be set! ", e);
        }
    }

    public void writeNode(Object object, QName nodeName, XMLSimpleWriter writer) {
        try {

            // it's a collection
            if (collectionConverter != null) {
                Collection collection = (Collection) targetField.get(object);
                if (collection == null) {
                    return;
                }
                for (Object obj : collection) {
                    QName name = itemTypes.get(obj.getClass());
                    NodeConverter converter = converterCache.get(name);
                    converter.toNode(obj, name, writer, mappingContext);
                }

            // normal field
            } else {
                nodeConverter.toNode(targetField.get(object), nodeName, writer, mappingContext);
            }
        } catch (IllegalAccessException e) {
            throw new XliteException("Field value could not be read! ", e);
        }
    }

//    private NodeConverter findConverter(Class type) {
//        NodeConverter converter = null;
//
//        // search the mapper cache
//        for (Map.Entry<Class, NodeConverter> entry : converterCache.entrySet()) {
//            if (entry.getKey().equals(type)) {
//                converter = entry.getValue();
//                return converter;
//            }
//        }
//
//        // if not in cache, lookup globally
//        converter = mappingContext.lookupNodeConverter(type);
//        // store in cache for future use
//        converterCache.put(type, converter);
//        return converter;
//    }
}
