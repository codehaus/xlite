package si.ptb.xfast.docx;


import si.ptb.xfast.docx.Paragraph;
import si.ptb.xfast.XmlStorage;
import si.ptb.xfast.*;


import java.util.List;
import java.util.ArrayList;

/**
 * User: peter
 * Date: Dec 30, 2007
 * Time: 11:08:01 PM
 */

public class Body extends XmlStorage {

    @XMLnode("w:p")     
    public List<Paragraph> paragraphs = new ArrayList<Paragraph>();


}
