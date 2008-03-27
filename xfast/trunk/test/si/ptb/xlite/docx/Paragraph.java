package si.ptb.xlite.docx;

import si.ptb.xlite.XMLnode;

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
