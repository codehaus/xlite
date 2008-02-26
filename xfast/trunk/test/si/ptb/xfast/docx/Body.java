package si.ptb.xfast.docx;


import si.ptb.xfast.XMLnode;

import java.util.ArrayList;
import java.util.List;

/**
 * User: peter
 * Date: Dec 30, 2007
 * Time: 11:08:01 PM
 */

public class Body {

    @XMLnode("w:p")
    public List<Paragraph> paragraphs = new ArrayList<Paragraph>();


}
