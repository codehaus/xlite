package si.ptb.xlite.docx;


import si.ptb.xlite.XMLnode;

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