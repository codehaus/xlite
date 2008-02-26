package deprecated;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import deprecated.NodeTree;
import deprecated.XmlStoring;
import si.ptb.xfast.ObjectReference;

/**
 * User: peter
 * Date: Jan 31, 2008
 * Time: 6:36:26 PM
 */
public class NodeStorageService {

    // every object can have multiple unknown nodes
    private Map storedAttributes = new HashMap();
    private List storedRefs = new ArrayList();
    private int c = 0;

    public void saveNodeTree(Object object, HierarchicalStreamReader reader) {
        // retrieve this node and all subnodes
        NodeTree nodeTree = new NodeTree();
        NodeTree.saveNodeRecursive(nodeTree, reader);

        // Does object support saving NodeTree inside it? (= implements XmlStoring)
        if (XmlStoring.class.isAssignableFrom(object.getClass())) {
            XmlStoring ns = (XmlStoring) object;
//            ns.saveNodeTree(nodeTree);
        } else {
            // mutable objects can not be used as keys in Maps
            // so we wrap them with weak reference objects
            ObjectReference foundRef = findReference(object);

            if (foundRef == null) {
                foundRef = new ObjectReference(object);
                storedRefs.add(foundRef);
            }

            foundRef.storedNodes.add(nodeTree);
        }

        c++;
        if (c % 1000 == 0) {
            System.out.println("storedTrees #" + c);
        }
    }

    private ObjectReference findReference(Object object) {
        ObjectReference foundRef = null;
        for (int i = storedRefs.size() - 1; i >= 0; i--) {
            ObjectReference ref = (ObjectReference) storedRefs.get(i);
            if (ref.get() == object) {
                foundRef = ref;
                break;
            }
        }
        return foundRef;
    }


    public void restoreNodeTree(Object object, HierarchicalStreamWriter writer) {

        if (XmlStoring.class.isAssignableFrom(object.getClass())) {
            XmlStoring ns = (XmlStoring) object;
            ns.restoreNodeTree(writer);
        } else {
            // find NodeTree belonging to object parameter
            for (int i = 0; i < storedRefs.size(); i++) {
                ObjectReference ref = (ObjectReference) storedRefs.get(i);
                if (ref.get() == object) {
                    List trees = ref.storedNodes;
                    for (int c = 0; c < trees.size(); c++) {
                        NodeTree nodeTree = (NodeTree) trees.get(c);
                        NodeTree.restoreNodeRecursive(writer, nodeTree);
                    }
                }
            }
        }
    }


    /**
     * Saves an attribute for later use.
     *
     * @param object    An Object to which attribute is assciated.
     * @param attrName
     * @param attrValue
     */
    public void saveAttribute(Object object, String attrName, String attrValue) {

        if (!storedAttributes.containsKey(object)) {
            storedAttributes.put(object, new HashMap<String, String>());
            storedAttributes.put(object, new ArrayList());
        }
        List attrList = (List) storedAttributes.get(object);
        attrList.add(new Attribute(attrName, attrValue));
    }

    /**
     * Restores saved attributes to the writer.
     *
     * @param object Object with which attributes are associated.
     * @param writer HierarchicalStreamWriter where attributes will be written to.
     */
    public void restoreAttributes(Object object, HierarchicalStreamWriter writer) {
        if (storedAttributes.containsKey(object)) {
            List attrList = (List) storedAttributes.get(object);
            for (Object o : attrList) {
                Attribute attr = (Attribute) o;
                writer.addAttribute(attr.name, attr.value);
            }
        }
    }

    public String toString() {
        String prefix = " ";
        StringBuilder str = new StringBuilder();
        List<Object> alreadyPrinted = new ArrayList<Object>();
        for (Object o : storedRefs) {
            ObjectReference ref = (ObjectReference) o;
            ArrayList nodeList = (ArrayList) ref.storedNodes;

            str.append("\n").append(ref.get().getClass()).append(" : ").append(ref.get().hashCode());

            // attributes for the given object
            Map<String, String> attrMap = (Map<String, String>) storedAttributes.get(o);
            if (attrMap != null) {
                alreadyPrinted.add(o);
                for (String attrKey : attrMap.keySet()) {
                    str.append("\n" + prefix + "unknown attribute ").append(attrKey).append("='").append(attrMap.get(attrKey)).append("'");
                }
            }

            //subnodes for the given object
            if (nodeList == null) {
                System.out.println("ERROR: null list of unknown subnodes " + ref.get().getClass() + "  " + ref.get().toString());
            } else {
                for (Object obj : nodeList) {
                    NodeTree nodeTree = (NodeTree) obj;
                    str.append(nodeTree.toString(prefix, 1));
                }
            }
        }

        // unknown attributes not yet printed
        for (Object obj : storedAttributes.keySet()) {
            if (!alreadyPrinted.contains(obj)) {
                str.append("\n").append(obj.getClass()).append(" : ").append(obj.hashCode());
                List<Attribute> attrList = (List<Attribute>) storedAttributes.get(obj);
                for (Attribute attr : attrList) {
                    str.append("\n" + prefix + "unknown attribute ").append(attr.name).append("='").append(attr.value).append("'");
                }
            }
        }

        return str.toString();
    }

    public class Attribute {
        public Attribute(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String name;
        public String value;
    }

}
