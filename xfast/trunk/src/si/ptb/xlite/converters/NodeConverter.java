package si.ptb.xlite.converters;

import si.ptb.xlite.XMLSimpleReader;
import si.ptb.xlite.XMLSimpleWriter;
import si.ptb.xlite.MappingContext;

/**
 * Classes implementing NodeConverter interface are used for serializing/deserializing xml
 * nodes to Java objects. They are responsible for reading/writing the xml stream on their own,
 * such that they always read/write the full xml node including all subnodes.
 * @author peter
 */
public interface NodeConverter {

    /**
     * Indicates whether an implementation of NodeConverter can convert xml node to given Class.
     * If it can then it returns an instance of NodeConverter. Otherwise it returns null.
     * @param type
     * @return
     */
    public boolean canConvert(Class type);

    /**
     * Method responsible for reading a complete xml node from XMLStreamReader  and returning deserialized Object
     * that corresponds to this node. When XMLStreamReader instance is passed to this method it is already
     * positioned on the xml node that is to be converted. Method should use reader.next() to traverse through
     * all node's attributes, value and subnodes. It should stop reading the stream when it encounters an END_ELEMENT
     * event that corresponds to first node.
//     * @param parentObject
     * @param reader
     * @return
     */
    public Object fromNode(XMLSimpleReader reader, MappingContext mappingContext);

    public void toNode(Object object, XMLSimpleWriter writer, MappingContext mappingContext);

}
