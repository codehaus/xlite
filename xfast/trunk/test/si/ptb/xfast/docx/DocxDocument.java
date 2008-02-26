package si.ptb.xfast.docx;

import com.thoughtworks.xstream.XStream;
import si.ptb.xfast.XMLnode;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * User: peter
 * Date: Dec 27, 2007
 * Time: 2:52:22 PM
 */

public class DocxDocument {

    private static String mainPartName = "word/document.xml";
    private static String settingsPartName = "word/settings.xml";

    private XStream xstream;

    @XMLnode("w:body")
    public Body body;

    public static DocxDocument open(InputStream inStream) throws IllegalArgumentException, FileNotFoundException {

        XStream xstream = new XStream();
        xstream.alias("w:document", DocxDocument.class);
        xstream.aliasField("w:body", DocxDocument.class, "body");

//        xstream.addImplicitCollection(Body.class, "paragraphs");
        xstream.alias("w:p", Paragraph.class);

//        xstream.addImplicitCollection(Paragraph.class, "runs");
        xstream.alias("w:r", Run.class);
        xstream.aliasField("w:t", Run.class, "text");

        DocxDocument document = (DocxDocument) xstream.fromXML(inStream);
        return document;
    }


}
