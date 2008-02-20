package si.ptb.xfast;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * User: peter
 * Date: Feb 15, 2008
 * Time: 1:30:18 AM
 */
public interface XmlStoring {

    public void saveNodeTree(HierarchicalStreamReader reader);

    public void restoreNodeTree(HierarchicalStreamWriter writer);

}
