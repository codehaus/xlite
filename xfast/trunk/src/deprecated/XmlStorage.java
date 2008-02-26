package deprecated;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.util.ArrayList;
import java.util.List;

import deprecated.NodeTree;

/**
 * Enables storing unknown XML data inside unmarshaled objects.
 *
 * User: peter
 * Date: Feb 15, 2008
 */
public class XmlStorage implements XmlStoring {

    public List storedNodes = new ArrayList();
    public List storedAttributes = new ArrayList();

    public void saveNodeTree(HierarchicalStreamReader reader) {
        NodeTree nodeTree = new NodeTree();
        NodeTree.saveNodeRecursive(nodeTree, reader);
        storedNodes.add(nodeTree);
    }

    public void restoreNodeTree(HierarchicalStreamWriter writer) {

        for (int i = 0; i < storedNodes.size(); i++) {
            NodeTree nodeTree = (NodeTree) storedNodes.get(i);
            NodeTree.restoreNodeRecursive(writer, nodeTree);
        }
    }

    public void saveAttributes(HierarchicalStreamReader reader){

    }

    public void restoreAttributes(HierarchicalStreamWriter writer) {

    }
}
