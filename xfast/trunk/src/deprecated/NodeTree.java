package deprecated;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.util.*;

/**
 * This class is used for storing unknown nodes in XML.
 * All data in nodes is stored: name, value, attributes and subnodes.
 * Subnodes are stored recursivelly, forming a tree.
 * User: peter
 * Date: Jan 31, 2008
 * Time: 6:24:48 PM
 */
public class NodeTree {

    public String name;
    public String value;
    //    public Map<String, String> attributes = new HashMap<String, String>();
    //    public List<NodeTree> subnodes = new ArrayList<NodeTree>();
    public Map attributes = new HashMap();
    public List subnodes = new ArrayList();

    public static void saveNodeRecursive(NodeTree nodeTree, HierarchicalStreamReader reader) {
        nodeTree.name = reader.getNodeName();

        // save attributes
        Iterator it = reader.getAttributeNames();
        while (it.hasNext()) {
            String attrName = (String) it.next();
            nodeTree.attributes.put(attrName, reader.getAttribute(attrName));
        }

        // save node value
        nodeTree.value = reader.getValue().trim();

        // save subnodes recursivelly
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            NodeTree newNode = new NodeTree();
            saveNodeRecursive(newNode, reader);
            nodeTree.subnodes.add(newNode);
            reader.moveUp();
        }
    }


    public static void restoreNodeRecursive(HierarchicalStreamWriter writer, NodeTree nodeTree) {
//todo        ExtendedHierarchicalStreamWriterHelper.startNode(writer, nodeTree.name, UnknownNode.class);
        for (Iterator it = nodeTree.attributes.keySet().iterator(); it.hasNext();) {
            Object o = it.next();
            String attrKey = (String) o;
            writer.addAttribute(attrKey, (String) nodeTree.attributes.get(attrKey));

        }
        writer.setValue(nodeTree.value);
        for (int i = 0; i < nodeTree.subnodes.size(); i++) {
            Object o = nodeTree.subnodes.get(i);
            NodeTree subnode = (NodeTree) o;
            restoreNodeRecursive(writer, subnode);
        }
        writer.endNode();

    }

    public String toString(String prefix, int depth) {
        StringBuilder str = new StringBuilder();
        StringBuilder of = new StringBuilder();
        for (int c = 0; c < depth; c++) {
            of.append(prefix);
        }
        String offset = of.append(prefix).toString();

        //node name
        str.append("\n").append(offset).append("<").append(name);

        // attributes
        Iterator it = attributes.keySet().iterator();
        while (it.hasNext()) {
            Object o = it.next();
            String attrkey = (String) o;
            str.append(" ").append(attrkey).append("=\"").append(attributes.get(attrkey)).append("\"");
        }
        str.append(">");

        // number of subnodes
        int subs = subnodes.size();

        // node value (text)
        if (subs > 0) {
//            str.append("\n").append(offset);
        }
        str.append(value);

        // subnodes (recursivelly)
        int subdepth = ++depth;
        for (int i = 0; i < subs; i++) {
            Object o = subnodes.get(i);
            NodeTree subnode = (NodeTree) o;
            str.append(subnode.toString(prefix, subdepth));
        }
        if (subs > 0) {
            str.append("\n").append(offset);
        }
        str.append("</").append(name).append(">");

        return str.toString();
    }

}
