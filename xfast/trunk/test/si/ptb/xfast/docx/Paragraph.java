package si.ptb.xfast.docx;

import si.ptb.xfast.XMLnode;

import java.util.ArrayList;
import java.util.List;

/**
 * User: peter
 * Date: Dec 27, 2007
 * Time: 3:07:49 PM
 */

public class Paragraph {

    @XMLnode("w:r")
    public List<Run> runs = new ArrayList<Run>();

}
