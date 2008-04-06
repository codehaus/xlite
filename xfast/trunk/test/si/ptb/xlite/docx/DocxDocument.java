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

    @XMLnode("wbody")
    public Body body;


    public static void main(String[] args) throws FileNotFoundException {
        long start = System.currentTimeMillis();
           Xlite xlite = new Xlite(DocxDocument.class, "wdocument");

        FileReader reader = new FileReader("/home/peter/vmware/shared/Office Open XML Part 3 - Primer/word/document2_pp.xml");
        FileReader reader4 = new FileReader("/home/peter/vmware/shared/Office Open XML Part 4 - Markup Language Reference/word/document.xml");
        FileReader reader2 = new FileReader("/home/peter/test2.xml");
        FileReader reader3 = new FileReader("/home/peter/test3.xml");
        DocxDocument document = (DocxDocument) xlite.fromXML(reader);

        System.out.println("duration: "+(System.currentTimeMillis() - start));
    }

}
