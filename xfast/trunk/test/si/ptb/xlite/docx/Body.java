package si.ptb.xlite.docx;


import si.ptb.xlite.XMLnode;
import si.ptb.xlite.XMLnamespaces;

import java.util.ArrayList;
import java.util.List;

/**
 * User: peter
 * Date: Dec 30, 2007
 * Time: 11:08:01 PM
 */

@XMLnamespaces("http://schemas.openxmlformats.org/wordprocessingml/2006/main")
public class Body {

    @XMLnode(value = "p", itemType = Paragraph.class)
    public List<Paragraph> paragraphs;


}
