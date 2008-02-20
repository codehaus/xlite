package si.ptb.xfast.docx;



import si.ptb.xfast.XmlStorage;
import si.ptb.xfast.XMLnode;

import java.util.List;
import java.util.ArrayList;

/**
 * User: peter
 * Date: Dec 27, 2007
 * Time: 3:07:49 PM
 */

public class Paragraph extends XmlStorage {

    @XMLnode("w:r")
    public List<Run> runs = new ArrayList<Run>();

}
