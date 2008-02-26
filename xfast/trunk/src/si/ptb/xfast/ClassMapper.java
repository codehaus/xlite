package si.ptb.xfast;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * User: peter
 * Date: Feb 17, 2008
 * Time: 5:20:50 PM
 */
public class ClassMapper {

    public String nodeName;
    public Class targetClass;
    public Map<String, ClassMapper> nodeMappers = new HashMap<String, ClassMapper>();
    public Map<String, FieldMapper> attributeMappers = new HashMap<String, FieldMapper>();
    public FieldMapper valueMapper;

    private SubTreeStore unknownNodeStorage;

    public ClassMapper(String nodeName, Class targetClass) {
        this.nodeName = nodeName;
        this.targetClass = targetClass;
    }

    public Object processNode(XMLStreamReader reader) throws XMLStreamException {

        // instantiate object that maps to the current XML node
        Object object = null;
        try {
            object = targetClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        StringBuilder chars = new StringBuilder();
        QName qname;
        String name;
        for (int event = reader.getEventType(); event != XMLStreamConstants.END_ELEMENT; event = reader.next()) {
            switch (event) {
                case XMLStreamConstants.START_ELEMENT:
                    qname = reader.getName();
                    name = qname.getPrefix().isEmpty() ? qname.getLocalPart() : (qname.getPrefix() + ":" + qname.getLocalPart());
                    ClassMapper submapper = nodeMappers.get(name);
                    if(submapper != null){  // subnode is mapped to class
                        submapper.processNode(reader);
                    }   else {  // unknown subnode
                        //todo process unknown nodes
                    }
                    break;
                case XMLStreamConstants.ATTRIBUTE:

                    break;
                case XMLStreamConstants.CHARACTERS:
                    chars.append(reader.getText());
                    break;

            }
        }
        


        return object;
    }

}
