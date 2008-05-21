package info.documan.xlite.converters;

import info.documan.xlite.*;

import javax.xml.namespace.QName;

/**
 * @author peter
 */
public class NodeHolderConverter implements NodeConverter {
    public boolean canConvert(Class type) {
        return NodeHolder.class.isAssignableFrom(type);
    }

    public Object fromNode(XMLSimpleReader reader, MappingContext mappingContext) {
        NodeHolder nodeHolder = new NodeHolder(500, 500);
        reader.saveSubTree(nodeHolder.getStore(), nodeHolder);
        return nodeHolder;
    }

    public void toNode(Object object, QName nodeName, XMLSimpleWriter writer, MappingContext mappingContext) {
        NodeHolder nodeHolder;
        if (NodeHolder.class.isAssignableFrom(object.getClass())) {
            nodeHolder = (NodeHolder) object;
        } else {
            throw new XliteException("NodeHolderConverter can only convert instances of NodeHolder!");
        }
        writer.restoreSubTrees(nodeHolder.getStore(), nodeHolder);
    }
}
