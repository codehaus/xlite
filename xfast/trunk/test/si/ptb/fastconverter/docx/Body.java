package si.ptb.fastconverter.docx;


import si.ptb.fastconverter.docx.Paragraph;
import si.ptb.fastconverter.XmlStorage;
import si.ptb.fastconverter.*;


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
