package si.ptb.xfast;

import org.testng.annotations.Test;
import si.ptb.xfast.docx.DocxDocument;

/**
 * User: peter
 * Date: Feb 26, 2008
 * Time: 11:50:33 PM
 */
public class XfastTest {

    @Test
    public void mainTest() {
        Xfast xf = new Xfast(DocxDocument.class, "rootNode");

        System.out.println("end!");
    }
}
