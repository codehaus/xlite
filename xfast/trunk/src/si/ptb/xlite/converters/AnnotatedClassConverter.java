package si.ptb.xlite.converters;

import si.ptb.xlite.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * User: peter
 * Date: Feb 28, 2008
 * Time: 10:19:19 PM
 */
public class AnnotatedClassConverter implements NodeConverter {

    private SubTreeStore unknownNodeStorage;
    private Class targetClass;
    private ValueMapper valueMapper;
    private Map<QName, NodeMapper> nodeMappers = new HashMap<QName, NodeMapper>();
    private Map<QName, ValueMapper> attributeMappers = new HashMap<QName, ValueMapper>();
    private NsContext classNamespaces;

    public AnnotatedClassConverter(Class targetClass) {
        this.targetClass = targetClass;
    }

    public NsContext getClassNamespaces() {
        return classNamespaces;
    }

    public void setClassNamespaces(NsContext classNamespaces) {
        this.classNamespaces = classNamespaces;
    }

    public void setValueMapper(ValueMapper valueMapper) {
        this.valueMapper = valueMapper;
    }

    public ValueMapper getValueMapper() {
        return valueMapper;
    }

    public void addNodeConverter(QName qName, NodeMapper nodeConverter) {
        nodeMappers.put(qName, nodeConverter);
    }

    public void addAttributeConverter(QName attributeQName, ValueMapper valueMapper) {
        attributeMappers.put(attributeQName, valueMapper);
    }

    public Class getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class targetClass) {
        this.targetClass = targetClass;
    }

    /**
     * This is a default NodeConverter that tries to convert all classes.
     *
     * @param type
     * @return
     */
    public boolean canConvert(Class type) {
        return targetClass.equals(type);
    }

    public Object fromNode(XMLSimpleReader reader, Class targetType, MappingContext mappingContext) {

        // instantiate object that maps to the current XML node
        Object currentObject = null;
        try {
            currentObject = targetClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        // XML node value
        if (valueMapper != null) {
            valueMapper.setValue(currentObject, reader.getText());
        }

        // XML node attributes
        Iterator<Map.Entry<QName, String>> attributeSet = reader.getAttributeIterator();
        while (attributeSet.hasNext()) {
            Map.Entry<QName, String> entry = attributeSet.next();
            QName attrQName = entry.getKey();
            String attrValue = entry.getValue();
             // find the attribute mapper
            ValueMapper attrMapper = attributeMappers.get(attrQName);
            // if mapper exists, use it to set field to attribute value
            if (attrMapper != null) {
                attrMapper.setValue(currentObject, attrValue);
            }
            System.out.println("ATTR: " + attrQName);
        }        

        // XML subnodes
        QName qname;
        String name;
        while (reader.moveDown()) {
            qname = reader.getName();
//            name = qname.getPrefix().length() == 0 ? qname.getLocalPart() : (qname.getPrefix() + ":" + qname.getLocalPart());

            // find NodeMapper for converting XML node with given name
            NodeMapper subMapper = nodeMappers.get(qname);
            if (subMapper != null) {  // converter is found
//                System.out.println("START:" + name + " thisConverter:" + this.toString() +
//                        " subConverter:" + subMapper.nodeConverter);
                subMapper.setValue(currentObject, reader);
            } else {  // unknown subMapper
//                System.out.println("UNKNOWN node: " + name);
            }
            reader.moveUp();
//            System.out.println("#### POSITION: "+reader.getName());
        }

        return currentObject;
    }

    public void toNode(Object object, XMLSimpleWriter writer, MappingContext mappingContext) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void printContents(String prefix) {
        prefix += " ";
        for (Map.Entry<QName, ValueMapper> attrEntry : attributeMappers.entrySet()) {
            System.out.println(prefix + "attribute:" + attrEntry.getKey()
                    + " field:" + attrEntry.getValue().targetField.getName() + "(" + attrEntry.getValue().targetField.getType() + ")");
        }

        for (Map.Entry<QName, NodeMapper> nodeEntry : nodeMappers.entrySet()) {
            System.out.println(prefix + "node:" + nodeEntry.getKey());
        }

    }
}
