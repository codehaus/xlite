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

        // default namespace is already by definition empty - so no need to redefine it
        if (namespace.length() == 0) {
            return;
        }

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
        namespaces.put(prefix, nsURI);
    }

    public String getNamespaceURI(String prefix) {

        // default values need to be handled
        if (XMLConstants.DEFAULT_NS_PREFIX.equals(prefix)) {  // "" (empty prefix)
            return XMLConstants.NULL_NS_URI; // "" (default namespace)

        } else if (XMLConstants.XML_NS_PREFIX.equals(prefix)) {  // "xml"
            return XMLConstants.XML_NS_URI; // "http://www.w3.org/XML/1998/namespace"

        } else if (XMLConstants.XMLNS_ATTRIBUTE.equals(prefix)) {  // "xmlns"
            return XMLConstants.XMLNS_ATTRIBUTE_NS_URI; // "http://www.w3.org/2000/xmlns/"
        }

        String nsURI = namespaces.get(prefix);
//        // sanity check
//        if(nsURI == null){
//            throw new RuntimeException("ERROR: namespaceURI shuld not be null!!!");
//        }
        return nsURI;
    }

    public String getPrefix(String namespaceURI) {
        throw new UnsupportedOperationException("Method getPrefix(String namespaceURI) in class NsContext is not implemented");
    }

    public Iterator getPrefixes(String namespaceURI) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
