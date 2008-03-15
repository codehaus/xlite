package si.ptb.xfast;

import si.ptb.xfast.converters.NodeConverter;
import si.ptb.xfast.converters.NodeMapper;
import si.ptb.xfast.converters.ValueMapper;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
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

    public Object fromNode(XMLStreamReader reader) {

        // instantiate object that maps to the current XML node
        Object currentObject = null;
        try {
            currentObject = targetClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        StringBuilder chars = new StringBuilder();
        QName qname;
        String name;
//        int depth = 1;
        boolean continueLoop = true;
        try {
            for (int event = reader.getEventType(); continueLoop; event = reader.next()) {
                switch (event) {
                    case XMLStreamConstants.START_ELEMENT:
//                        depth++;
                        qname = reader.getName();
                        name = qname.getPrefix().length() == 0 ? qname.getLocalPart() : (qname.getPrefix() + ":" + qname.getLocalPart());

                        // find NodeMapper for converting XML node with given name
                        NodeMapper subMapper = nodeMappers.get(name);

                        if (subMapper != null) {  // converter is found
                            System.out.println("START:" + name + " thisConverter:" + this.toString() +
                                    " subConverter:" + subMapper.nodeConverter);
                            subMapper.setValue(currentObject, reader);
                        } else {  // unknown subMapper
                            System.out.println("unknown node: " + name);
                        }
                        break;
                    case XMLStreamConstants.END_ELEMENT:
//                        depth--;
//                        continueLoop = (depth != 0);
                        continueLoop = false;
                        System.out.println("END: " + reader.getName().getLocalPart() + " this:" + this.toString());
                        break;
                    case XMLStreamConstants.ATTRIBUTE:
                        int count = reader.getAttributeCount();
                        String attrName, attrValue;
                        for (int i = 0; i < count; i++) {
                            qname = reader.getAttributeName(i);
                            attrName = qname.getPrefix().length() == 0 ? qname.getLocalPart() : (qname.getPrefix() + ":" + qname.getLocalPart());
                            attrValue = reader.getAttributeValue(i);
                            ValueMapper attrMapper = attributeMappers.get(attrName); //todo finish this 
                            System.out.println("ATTR: " + attrName);

                        }
                        break;
                    case XMLStreamConstants.CHARACTERS:
                        String text = reader.getText();
                        System.out.println("TEXT: " + text);
                        chars.append(text);
                        break;
                    case XMLStreamConstants.END_DOCUMENT:
                        System.out.println("END DOCUMENT");
                        continueLoop = false;
                        break;
                }
            }

            // XML node value
            valueMapper.setValue(currentObject, chars.toString());

        } catch (XMLStreamException e) {
            throw new XfastException("Error getting next xml element.", e);
        }

        return currentObject;
    }

    public void toNode(Object object, XMLStreamWriter writer) {
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
