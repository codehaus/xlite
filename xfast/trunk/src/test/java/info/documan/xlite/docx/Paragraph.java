package info.documan.xlite.docx;

import info.documan.xlite.XMLnode;

import java.util.List;

/**
 * User: peter
 * Date: Dec 27, 2007
 * Time: 3:07:49 PM
 */
public class Paragraph {

    @XMLnode(value = "w:r", itemType = Run.class)
    public List<Run> runs;

}
