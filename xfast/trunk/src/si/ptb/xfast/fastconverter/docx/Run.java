package si.ptb.fastconverter.docx;

import si.ptb.fastconverter.XmlStorage;
import si.ptb.fastconverter.XMLtext;


/**
 * User: peter
 * Date: Jan 11, 2008
 * Time: 1:04:13 PM
 */

public class Run extends XmlStorage {

    @XMLtext("w:t")    
    public String text;

}
