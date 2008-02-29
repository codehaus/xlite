package si.ptb.xfast;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

/**
 * User: peter
 * Date: Feb 17, 2008
 * Time: 5:20:50 PM
 */
public class DefaultMapper extends AnnotatedMapper {

    private SubTreeStore unknownNodeStorage;

    public DefaultMapper(String nodeName, Class targetClass) {
        super(targetClass, nodeName);
    }

    /**
     * This is a default Mapper that tries to convert all classes.
     * @param type
     * @return  Always returns true.
     */
    public boolean canMapNode(Class type) {
        return true;
    }

    /**
     * Default mapper does not convert any simple values.
     * @return Always returns false.
     */
    public boolean canConvertValue(Class type) {
        return false;
    }

    public Object fromValue(String value) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String toValue(Object object) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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
        try {
            for (int event = reader.getEventType(); event != XMLStreamConstants.END_ELEMENT; event = reader.next()) {
                switch (event) {
                    case XMLStreamConstants.START_ELEMENT:
                        qname = reader.getName();
                        name = qname.getPrefix().isEmpty() ? qname.getLocalPart() : (qname.getPrefix() + ":" + qname.getLocalPart());
                        Mapper submapper = nodeMappers.get(name);
                        if (submapper != null) {  // subnode is mapped to class
                            submapper.fromNode(currentObject, reader);
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
            throw new XfastException("Error getting next xml element.", e);
        } catch (IllegalAccessException e) {
            throw new XfastException("Error setting parent-child reference.", e);
        }

    }

    public void toNode(Object object, XMLStreamWriter writer) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

}
