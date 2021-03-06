/*
 * This software is released under the BSD license. Full license available at http://www.xlite.org/license/
 *
 * Copyright (c) 2008, 2009, Peter Knego & Xlite contributors
 * All rights reserved.
 */
package org.xlite.converters;

import org.xlite.MappingContext;
import org.xlite.XMLSimpleReader;
import org.xlite.XMLSimpleWriter;
import org.xlite.XliteException;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author peter
 */
public class CollectionConverter implements ElementConverter, CollectionConverting {

    public boolean canConvert(Class type) {
        return Collection.class.isAssignableFrom(type);
    }

    public Object fromElement(XMLSimpleReader reader, MappingContext mappingContext, String defaultValue, String format, Class targetType, Object targetObject) {
//        ElementConverter converter = mappingContext.lookupElementConverter(targetType);
//        return converter.fromElement(reader, mappingContext);
        throw new XliteException("CollectionConverter.fromElement() method should not be called directly. " +
                "Rather for every object in a collection, a .fromElement() method should be called on it's assigned converter");
    }

    public void toElement(Object object, QName nodeName, XMLSimpleWriter writer, MappingContext mappingContext, String defaultValue, String format) {
        throw new XliteException("CollectionConverter.toElement() method should not be called directly. " +
                "Rather for every object in a collection, a .toElement() method should be called on it's assigned converter");
    }

    public Collection initializeCollection(Class targetType) {
        // is target class a Collection?
        if (!Collection.class.isAssignableFrom(targetType)) {
            throw new XliteException("Error: Target class " + targetType.getName() + " can not be cast to java.util.Collection!");
        }
        Class<? extends Collection> concreteType = getConcreteCollectionType(targetType);
        try {
            return concreteType.newInstance();
        } catch (Exception e) {
            throw new XliteException("Could not instantiate collection " + targetType.getName() + ". ", e);
        }
    }

    private static Class<? extends Collection> getConcreteCollectionType(Class<? extends Collection> targetType) {
        if (targetType == List.class || targetType == Collection.class) {
            return ArrayList.class;
        }
        return targetType;
    }

    public void addItem(Collection collection, Object object) {
        collection.add(object);
    }

}
