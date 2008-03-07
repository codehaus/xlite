package si.ptb.xfast;

import si.ptb.xfast.converters.ValueMapper;
import si.ptb.xfast.converters.NodeConverter;
import si.ptb.xfast.converters.NodeMapper;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * User: peter
 * Date: Feb 28, 2008
 * Time: 10:19:19 PM
 */
public class AnnotatedClassMapper implements NodeConverter {

    private String nodeName;
    private Class targetClass;
    private Field parentField;
    private ValueMapper valueMapper;
    private Map<String, NodeMapper> nodeMappers = new HashMap<String, NodeMapper>();
    private Map<String, ValueMapper> attributeMappers = new HashMap<String, ValueMapper>();
    private SubTreeStore unknownNodeStorage;

    public AnnotatedClassMapper(Class targetClass, String nodeName, Field parentField) {
        this.targetClass = targetClass;
        this.nodeName = nodeName;
        this.parentField = parentField;
    }

    public void setValueConnector(ValueMapper valueMapper) {
        this.valueMapper = valueMapper;
    }

    public void addNodeConnector(String nodeName, NodeMapper nodeConverter) {
        nodeMappers.put(nodeName, nodeConverter);
    }

    public void addAttributeConverter(String attributeName, ValueMapper valueMapper) {
        attributeMappers.put(attributeName, valueMapper);
    }

    public void setParentField(Field parentField) {
        this.parentField = parentField;
    }

    public Field getParentField() {
        return parentField;
    }

    /**
     * This is a default NodeConverter that tries to convert all classes.
     *
     * @param type
     * @return Always returns true.
     */
    public boolean canConvert(Class type) {
        return true;
    }

    public NodeConverter getConverter(Class type) {
        return null;  //ToDo Finish this ASAP!!!
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
        int depth = 1;
        boolean continueLoop = true;
        try {
            for (int event = reader.getEventType(); continueLoop; event = reader.next()) {
                switch (event) {
                    case XMLStreamConstants.START_ELEMENT:
                        depth++;
                        qname = reader.getName();
                        name = qname.getPrefix().isEmpty() ? qname.getLocalPart() : (qname.getPrefix() + ":" + qname.getLocalPart());

                        // find NodeMapper for converting XML node with given name
                        NodeMapper subNode = nodeMappers.get(name);
                        if (subNode != null) {  // converter is found
                            subNode.setValue(currentObject, reader);
                        } else {  // unknown subNode
                            //TODO process unknown nodes
                        }
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        depth--;
                        continueLoop = (depth != 0);
                        break;
                    case XMLStreamConstants.ATTRIBUTE:

                        break;
                    case XMLStreamConstants.CHARACTERS:
                        chars.append(reader.getText());
                        break;
                }
            }
            //todo assign value

        } catch (XMLStreamException e) {
            throw new XfastException("Error getting next xml element.", e);
        }

        return currentObject;
    }

    public void toNode(Object object, XMLStreamWriter writer) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
