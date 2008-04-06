package si.ptb.xlite.converters;

import si.ptb.xlite.converters.NodeConverter;
import si.ptb.xlite.converters.NodeMapper;
import si.ptb.xlite.converters.ValueMapper;
import si.ptb.xlite.SubTreeStore;
import si.ptb.xlite.XMLSimpleReader;
import si.ptb.xlite.MappingContext;
import si.ptb.xlite.XMLSimpleWriter;

import javax.xml.namespace.QName;
import java.util.HashMap;
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
    private Map<String, NodeMapper> nodeMappers = new HashMap<String, NodeMapper>();
    private Map<String, ValueMapper> attributeMappers = new HashMap<String, ValueMapper>();

    public AnnotatedClassConverter(Class targetClass) {
        this.targetClass = targetClass;
    }

    public void setValueMapper(ValueMapper valueMapper) {
        this.valueMapper = valueMapper;
    }

    public ValueMapper getValueMapper() {
        return valueMapper;
    }

    public void addNodeConverter(String nodeName, NodeMapper nodeConverter) {
        nodeMappers.put(nodeName, nodeConverter);
    }

    public void addAttributeConverter(String attributeName, ValueMapper valueMapper) {
        attributeMappers.put(attributeName, valueMapper);
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
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            String attrName = reader.getAttributeName(i);     //todo refactor to QName
            String attrValue = reader.getAttributeValue(i);
            // find the attribute mapper
            ValueMapper attrMapper = attributeMappers.get(attrName);
            // if mapper exists, use it to set field to attribute value
            if (attrMapper != null) {
                attrMapper.setValue(currentObject, attrValue);
            }
//            System.out.println("ATTR: " + attrName);
        }

        // XML subnodes
        QName qname;
        String name;
        while (reader.moveDown()) {
            qname = reader.getName();  // todo refactor NameSpaces!!
            name = qname.getPrefix().length() == 0 ? qname.getLocalPart() : (qname.getPrefix() + ":" + qname.getLocalPart());

            // find NodeMapper for converting XML node with given name
            NodeMapper subMapper = nodeMappers.get(name);
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
        for (Map.Entry<String, ValueMapper> attrEntry : attributeMappers.entrySet()) {
            System.out.println(prefix + "attribute:" + attrEntry.getKey()
                    + " field:" + attrEntry.getValue().targetField.getName() + "(" + attrEntry.getValue().targetField.getType() + ")");
        }

        for (Map.Entry<String, NodeMapper> nodeEntry : nodeMappers.entrySet()) {
            System.out.println(prefix + "node:" + nodeEntry.getKey());
        }

    }
}
