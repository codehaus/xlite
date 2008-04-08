package si.ptb.xlite.docx;

import si.ptb.xlite.XMLtext;
import si.ptb.xlite.XMLnamespaces;

/**
 * @author peter
 */
@XMLnamespaces("http://schemas.openxmlformats.org/wordprocessingml/2006/main")
public class Text {

    @XMLtext()
    public String text;
}
