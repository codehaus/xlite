package si.ptb.xfast;

import si.ptb.xfast.converters.NodeConverter;
import si.ptb.xfast.converters.NodeMapper;
import si.ptb.xfast.converters.ValueMapper;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;

/**
 * User: peter
 * Date: Feb 28, 2008
 * Time: 10:19:19 PM
 */
public class AnnotatedClassMapper implements NodeConverter {

    public String nodeName;
    public Class targetClass;
    public ValueMapper valueMapper;
    private Map<String, NodeMapper> nodeMappers = new HashMap<String, NodeMapper>();
    private Map<String, ValueMapper> attributeMappers = new HashMap<String, ValueMapper>();
    private SubTreeStore unknownNodeStorage;

    public AnnotatedClassMapper(Class targetClass, String nodeName) {
        this.targetClass = targetClass;
        this.nodeName = nodeName;
    }

    public void setValueConnector(ValueMapper valueMapper) {
        this.valueMapper = valueMapper;
    }

    public void addNodeConverter(String nodeName, NodeMapper nodeConverter) {
        nodeMappers.put(nodeName, nodeConverter);
    }

    public void addAttributeConverter(String attributeName, ValueMapper valueMapper) {
        attributeMappers.put(attributeName, valueMapper);
    }

    /**
     * This is a default NodeConverter that tries to convert all classes.
     *
     * @param type
     * @return
     */
    public NodeConverter getConverter(Class type) {
        return null;  //todo implement this - How?
    }

    public Object fromNode(XMLSimpleReader reader) {

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
            System.out.println("ATTR: " + attrName);
        }

        QName qname;
        String name;
        while (reader.moveDown()) {
            qname = reader.getName();  // todo refactor NameSpaces!!
            name = qname.getPrefix().length() == 0 ? qname.getLocalPart() : (qname.getPrefix() + ":" + qname.getLocalPart());

            // find NodeMapper for converting XML node with given name
            NodeMapper subMapper = nodeMappers.get(name);
            if (subMapper != null) {  // converter is found
                System.out.println("START:" + name + " thisConverter:" + this.toString() +
                        " subConverter:" + subMapper.nodeConverter);
                subMapper.setValue(currentObject, reader);
            } else {  // unknown subMapper
                System.out.println("UNKNOWN node: " + name);
            }
            reader.moveUp();
        }

        return currentObject;
    }

    public void toNode(Object object, XMLSimpleWriter writer) {
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
