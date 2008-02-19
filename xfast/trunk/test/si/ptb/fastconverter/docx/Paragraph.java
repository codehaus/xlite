package si.ptb.fastconverter.docx;



import si.ptb.fastconverter.XmlStorage;
import si.ptb.fastconverter.XMLnode;

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
