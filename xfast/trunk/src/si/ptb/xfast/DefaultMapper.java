package si.ptb.xfast;

import deprecated.FieldMapper;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLStreamException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * User: peter
 * Date: Feb 17, 2008
 * Time: 5:20:50 PM
 */
public class DefaultMapper extends AbstractNodeMapper {

    public String nodeName;
    public Class targetClass;
    public Field parentReference;
    public Map<String, DefaultMapper> nodeMappers = new HashMap<String, DefaultMapper>();
    public Map<String, FieldMapper> attributeMappers = new HashMap<String, FieldMapper>();
    public FieldMapper valueMapper;

    private SubTreeStore unknownNodeStorage;

    public DefaultMapper(String nodeName, Class targetClass, Field parentField) {
        this.nodeName = nodeName;
        this.targetClass = targetClass;
        this.parentReference = parentField;
    }

    public boolean canMap(Class type) {
        return true;
    }

    public Object deserialize(Object parentObject, XMLStreamReader reader) {

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
        try {
            for (int event = reader.getEventType(); event != XMLStreamConstants.END_ELEMENT; event = reader.next()) {
                switch (event) {
                    case XMLStreamConstants.START_ELEMENT:
                        qname = reader.getName();
                        name = qname.getPrefix().isEmpty() ? qname.getLocalPart() : (qname.getPrefix() + ":" + qname.getLocalPart());
                        DefaultMapper submapper = nodeMappers.get(name);
                        if (submapper != null) {  // subnode is mapped to class
                            submapper.deserialize(currentObject, reader);
                        } else {  // unknown subnode
                            //TODO process unknown nodes
                        }
                        break;
                    case XMLStreamConstants.ATTRIBUTE:

                        break;
                    case XMLStreamConstants.CHARACTERS:
                        chars.append(reader.getText());
                        break;
                }
            }

            if (parentObject != null) {
            parentReference.set(parentObject, currentObject);
        }
        return currentObject;
        } catch (XMLStreamException e) {
           throw new XfastException("Error getting next xml element.",e);
        } catch (IllegalAccessException e) {
            throw new XfastException("Error setting parent-child reference.",e);
        }


    }

    public void serialize(Object object, XMLStreamWriter writer) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

}
