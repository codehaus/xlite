package si.ptb.fastconverter.docx;

import si.ptb.fastconverter.docx.Body;
import si.ptb.fastconverter.docx.Paragraph;
import si.ptb.fastconverter.docx.Run;
import si.ptb.fastconverter.XmlStorage;
import si.ptb.fastconverter.XMLnode;


import java.io.*;

import com.thoughtworks.xstream.XStream;

/**
 * User: peter
 * Date: Dec 27, 2007
 * Time: 2:52:22 PM
 */

public class DocxDocument extends XmlStorage {

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
