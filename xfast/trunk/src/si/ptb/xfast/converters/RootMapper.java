package si.ptb.xfast.converters;

import si.ptb.xfast.XfastException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * @author peter
 */
public class RootMapper extends NodeMapper {

    private String rootNodeName;

    public RootMapper(String rootNodeName, NodeConverter nodeConverter) {
        super(null, nodeConverter);
        this.rootNodeName = rootNodeName;
    }

    public Object getRootObject(XMLStreamReader reader) {

        // Traverses the XML data until first root node is found.
        // Root node is the node at the top of the mapping tree
        boolean continueLoop = true;
        QName qname;
        String name;
        try {
            for (int event = reader.getEventType(); continueLoop; event = reader.next()) {
                switch (event) {
                    case XMLStreamConstants.START_ELEMENT:
                        qname = reader.getName();
                        name = qname.getPrefix().length()==0 ? qname.getLocalPart() : (qname.getPrefix() + ":" + qname.getLocalPart());
                        if (name.equals(rootNodeName))
                            System.out.println("ROOT nodeName=" + rootNodeName + " nodeConverter=" + nodeConverter.toString());

//                        reader.next();
                        return nodeConverter.fromNode(reader);
                }
            }
        } catch (XMLStreamException e) {
            throw new XfastException("Error reading XML data from Reader.", e);
        }
        return null;
    }
}