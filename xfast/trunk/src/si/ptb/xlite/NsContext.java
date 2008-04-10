package si.ptb.xlite;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author peter
 */
public class NsContext implements NamespaceContext {

    private Map<String, String> namespaces = new HashMap<String, String>();

    public void addNamespace(String namespace) {

        int index = namespace.indexOf('=');
        String prefix, nsURI;
        if (index > 0) {  // with prefix
            prefix = namespace.substring(0, index);
            nsURI = namespace.substring(index + 1, namespace.length());

        } else if (index == 0) { // empty prefix (no prefix defined - e.g ":namespaceURL")
            prefix = XMLConstants.DEFAULT_NS_PREFIX;
            nsURI = namespace.substring(1, namespace.length());

        } else { // no prefix given
            prefix = XMLConstants.DEFAULT_NS_PREFIX;
            nsURI = namespace;
        }
//        System.out.println("namespace nsURi=" + nsURI + " prefix=" + prefix);
        namespaces.put(prefix, nsURI);
    }

    public String getNamespaceURI(String prefix) {
        return namespaces.get(prefix);
    }

    public String getPrefix(String namespaceURI) {
        throw new UnsupportedOperationException("Method getPrefix(String namespaceURI) in class NsContext is not implemented");
    }

    public Iterator getPrefixes(String namespaceURI) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
