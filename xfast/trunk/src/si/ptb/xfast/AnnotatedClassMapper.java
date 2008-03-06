package si.ptb.xfast;

import si.ptb.xfast.converters.ValueMapper;
import si.ptb.xfast.converters.NodeConverter;

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
    private Field parentReference;
    private ValueMapper valueMapper;
    private Map<String, NodeConverter> nodeMappers = new HashMap<String, NodeConverter>();
    private Map<String, ValueMapper> attributeConverters = new HashMap<String, ValueMapper>();
    private SubTreeStore unknownNodeStorage;

    public AnnotatedClassMapper(Class targetClass, String nodeName) {
        this.targetClass = targetClass;
        this.nodeName = nodeName;
    }

    public void setValueConverter(ValueMapper valueMapper) {
        this.valueMapper = valueMapper;
    }

    public void addNodeMapper(String nodeName, NodeConverter nodeConverter) {
        nodeMappers.put(nodeName, nodeConverter);
    }

    public void addAttributeConverter(String attributeName, ValueMapper valueMapper) {
        attributeConverters.put(attributeName, valueMapper);
    }

    public void setParentReference(Field parentReference) {
        this.parentReference = parentReference;
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

    public Object fromNode(Object parentObject, XMLStreamReader reader) {

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
                        NodeConverter submapper = nodeMappers.get(name);
                        if (submapper != null) {  // subnode is mapped to class
                            submapper.fromNode(currentObject, reader);
                        } else {  // unknown subnode
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

            if (parentObject != null) {
                parentReference.set(parentObject, currentObject);
            }
            return currentObject;
        } catch (XMLStreamException e) {
            throw new XfastException("Error getting next xml element.", e);
        } catch (IllegalAccessException e) {
            throw new XfastException("Error setting parent-child reference.", e);
        }

    }

    public void toNode(Object object, XMLStreamWriter writer) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
