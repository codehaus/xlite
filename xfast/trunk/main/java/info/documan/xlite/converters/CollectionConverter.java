package info.documan.xlite.converters;

import info.documan.xlite.XMLSimpleWriter;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import info.documan.xlite.MappingContext;
import info.documan.xlite.XliteException;
import info.documan.xlite.*;

/**
 * @author peter
 */
public class CollectionConverter implements NodeConverter, CollectionConverting {

    public boolean canConvert(Class type) {
        return Collection.class.isAssignableFrom(type);
    }

    public Object fromNode(XMLSimpleReader reader, Class targetType, MappingContext mappingContext) {
        return mappingContext.processNextNode(targetType, reader);
    }

    public void toNode(Object object, QName nodeName, XMLSimpleWriter writer, MappingContext mappingContext) {
        if (object == null) {
            return;
        }
        Collection collection = (Collection) object;
        for (Object obj : collection) {
            mappingContext.processNextObject(obj, nodeName, writer);
        }
    }

    public Collection initializeCollection(Class targetType) {
        if (!isCollectionType(targetType)) {
            throw new XliteException("Error: Target class " + targetType.getName() + " can not be cast to java.util.Collection!");
        }
        Class<? extends Collection> concreteType = getConcreteCollectionType(targetType);
        try {
            return concreteType.newInstance();
        } catch (InstantiationException e) {
            throw new XliteException("Could not instantiate collection " + targetType.getName() + ". ", e);
        } catch (IllegalAccessException e) {
            throw new XliteException("Could not instantiate collection " + targetType.getName() + ". ", e);
        }
    }

    private Class<? extends Collection> getConcreteCollectionType(Class<? extends Collection> targetType) {
        if (targetType == List.class || targetType == Collection.class) {
            return ArrayList.class;
        }
        return targetType;
    }

    private boolean isCollectionType(Class type) {
        return Collection.class.isAssignableFrom(type);
    }

    public void addItem(Collection collection, Object object) {
        collection.add(object);
    }

    public Iterator getIterator(Collection collection) {
        return collection.iterator();
    }

    public static void main(String[] args) {
        NodeConverter nc = new CollectionConverter();
        CollectionConverting cc = (CollectionConverting) nc;

        System.out.println(CollectionConverting.class.isAssignableFrom(nc.getClass()));
    }

}
