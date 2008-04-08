package si.ptb.xlite.docx;

import si.ptb.xlite.XMLtext;
import si.ptb.xlite.XMLnode;
import si.ptb.xlite.XMLnamespaces;


/**
 * User: peter
 * Date: Jan 11, 2008
 * Time: 1:04:13 PM
 */
@XMLnamespaces("http://schemas.openxmlformats.org/wordprocessingml/2006/main")
public class Run {

    @XMLnode("t")
    public Text textnode;

}
