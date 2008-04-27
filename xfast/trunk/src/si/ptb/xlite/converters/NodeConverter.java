package si.ptb.xlite.converters;

import si.ptb.xlite.MappingContext;
import si.ptb.xlite.XMLSimpleReader;
import si.ptb.xlite.XMLSimpleWriter;

/**
 * Classes implementing NodeConverter interface are used for serializing/deserializing xml
 * nodes to Java objects. They are responsible for reading/writing the xml stream on their own,
 * such that they always read/write the full xml node including all subnodes.
 *
 * @author peter
 */
public interface NodeConverter {

    /**
     * Indicates whether an implementation of NodeConverter can convert xml node to given Class.
     * If it can then it returns an instance of NodeConverter. Otherwise it returns null.
     *
     * @param type
     * @return
     */
    public boolean canConvert(Class type);

    /**
     * Method responsible for reading a complete xml node from XMLSimpleReader and returning deserialized Object
     * that corresponds to this node. When XMLSimpleReader instance is passed to this method it is already
     * positioned on the xml node that is to be converted. Method can inspect all node's attributes, value and subnodes.
     * When method returns, the stream should be positioned on the same node that
     *
     * @param reader
     * @param targetType
     * @param mappingContext @return
     */
    public Object fromNode(XMLSimpleReader reader, Class targetType, MappingContext mappingContext);

    public void toNode(Object object, XMLSimpleWriter writer, MappingContext mappingContext);

}
