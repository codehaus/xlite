package si.ptb.xlite.converters;

import si.ptb.xlite.MappingContext;
import si.ptb.xlite.XMLSimpleReader;
import si.ptb.xlite.XMLSimpleWriter;
import si.ptb.xlite.XliteException;

import java.util.Collection;
import java.util.Iterator;

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
        //todo finish this
    }

    public Collection initializeCollection(Class targetType) {
        try {
            return (Collection) targetType.newInstance();
        } catch (InstantiationException e) {
            throw new XliteException("Could not instantiate collection " + targetType.getName() + ". ", e);
        } catch (IllegalAccessException e) {
            throw new XliteException("Could not instantiate collection " + targetType.getName() + ". ", e);
        }
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
