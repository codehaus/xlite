package info.documan.xlite.converters;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import info.documan.xlite.*;

/**
 * User: peter
 * Date: Feb 28, 2008
 * Time: 10:19:19 PM
 */
public class AnnotatedClassConverter implements NodeConverter {

    private SubTreeStore nodeStorage;

    private Class targetClass;

    private ValueMapper valueMapper;

    private Map<QName, NodeMapper> nodeMappers = new HashMap<QName, NodeMapper>();

    private Map<QName, ValueMapper> attributeMappers = new HashMap<QName, ValueMapper>();

    private NsContext classNamespaces;

    public AnnotatedClassConverter(Class targetClass) {
        this.targetClass = targetClass;
    }

    public SubTreeStore getNodeStorage() {
        return nodeStorage;
    }

    public void setNodeStorage(SubTreeStore nodeStorage) {
        this.nodeStorage = nodeStorage;
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
            throw new XliteException("Could not instantiate class " + targetClass.getName(), e);
        } catch (IllegalAccessException e) {
            throw new XliteException("Could not instantiate class " + targetClass.getName(), e);

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
//            System.out.println("ATTR: " + attrQName);
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
                if (nodeStorage != null) {
                    reader.saveSubTree(nodeStorage, currentObject);
                }
            }
//            String nm = "null";
//            nm = (reader.reader.getEventType() == 1 || reader.reader.getEventType() == 2) ? reader.reader.getName().getLocalPart() : "";
//            System.out.println("BEFORE moveUp: "+reader.reader.getEventType()+" "+nm);
            reader.moveUp();
        }

        return currentObject;
    }

    public void toNode(Object object, QName nodeName, XMLSimpleWriter writer, MappingContext mappingContext) {

        // write a start tag
        writer.startNode(nodeName);

        // write attributes
        for (QName attrName : attributeMappers.keySet()) {
            ValueMapper mapper = attributeMappers.get(attrName);
            String value = mapper.getValue(object);
            writer.addAttribute(attrName, value);
        }

        // write node's value
        if (valueMapper != null && object != null) {
            writer.addText(valueMapper.getValue(object));
        }

        // write subnodes
        for (QName subName : nodeMappers.keySet()) {
            NodeMapper nodeMapper = nodeMappers.get(subName);
            nodeMapper.writeNode(object, subName, writer);
        }

        // write  unknown (stored) subnodes
        if (nodeStorage != null) {
            writer.restoreSubTrees(nodeStorage, object);
        }

        // write end tag
        writer.endNode();

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
