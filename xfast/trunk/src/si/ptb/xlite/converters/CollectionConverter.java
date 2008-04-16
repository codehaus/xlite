package si.ptb.xlite.converters;

import si.ptb.xlite.MappingContext;
import si.ptb.xlite.XMLSimpleReader;
import si.ptb.xlite.XMLSimpleWriter;
import si.ptb.xlite.XliteException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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

    public void toNode(Object object, XMLSimpleWriter writer, MappingContext mappingContext) {
        throw new UnsupportedOperationException("method not implemented yet!!");
    }

    public Collection initializeCollection(Class targetType) {
        if (!isCollectionType(targetType)) {
            throw new XliteException("Error: Target class " + targetType.getName() + " is can not be cast to java.util.Collection!");
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
