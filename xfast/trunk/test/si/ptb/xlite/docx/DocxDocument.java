package si.ptb.xlite.docx;

import si.ptb.xlite.SampleXml;
import si.ptb.xlite.XMLnode;
import si.ptb.xlite.Xlite;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.FileReader;

/**
 * User: peter
 * Date: Dec 27, 2007
 * Time: 2:52:22 PM
 */

public class DocxDocument {

    private static String mainPartName = "word/document.xml";
    private static String settingsPartName = "word/settings.xml";

    @XMLnode("w:body")
    public Body body;


    public static void main(String[] args) throws FileNotFoundException {
           Xlite xlite = new Xlite(DocxDocument.class, "document");

        FileReader reader = new FileReader("/home/peter/vmware/shared/Office Open XML Part 3 - Primer/word/document.xml");
        DocxDocument document = (DocxDocument) xlite.fromXML(reader);

        System.out.println("end!");
    }

}
