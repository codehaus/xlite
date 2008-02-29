package si.ptb.xfast;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

/**
 * Classes implementing Mapper interface are used for serializing/deserializing xml
 * node's textual data to Java objects. They are responsible for reading/writing the xml stream on their own,
 * such that they always read/write the full xml node including all subnodes.
 * User: peter
 * Date: Feb 28, 2008
 * Time: 11:31:27 AM
 */
public interface Mapper {

    /**
     * Indicates if an implementation of Mapper interface can map a XML node to a given type.
     *
     * @param type
     * @return
     */
    public boolean canMapNode(Class type);

    /**
     * Method responsible for reading a complete xml node from XMLStreamReader  and returning deserialized Object
     * that corresponds to this node. When XMLStreamReader instance is passed to this method it is already
     * positioned on the xml node that is to be converted. Method should use reader.next() to traverse through
     * all node's attributes, value and subnodes. It shuld stop reading the stream when it encounters an END_ELEMENT
     * event that corresponds to 
     * @param parentObject
     * @param reader
     * @return
     */
    public Object fromNode(Object parentObject, XMLStreamReader reader);

    public void toNode(Object object, XMLStreamWriter writer);
    

}
