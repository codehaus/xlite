package si.ptb.xlite.docx;

import org.xml.sax.SAXException;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import si.ptb.xlite.XMLnode;
import si.ptb.xlite.Xlite;

import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;
import java.io.StringWriter;

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

    public static void main(String[] args) throws IOException, SAXException {

        for (int i = 0; i < 1; i++) {

            DocxDocument document = null;
            Xlite xlite = new Xlite(DocxDocument.class, "document", "http://schemas.openxmlformats.org/wordprocessingml/2006/main");
            xlite.isStoringUnknownNodes = true;
            xlite.addNamespace("w=http://schemas.openxmlformats.org/wordprocessingml/2006/main");

            FileReader reader4 = new FileReader("/home/peter/vmware/shared/Office Open XML Part 4 - Markup Language Reference/word/document.xml");
            FileReader reader3 = new FileReader("/home/peter/vmware/shared/Office Open XML Part 3 - Primer/word/document_pp.xml");
            FileReader reader2 = new FileReader("/home/peter/vmware/shared/test2/word/document_pp.xml");
            FileReader reader1 = new FileReader("/home/peter/vmware/shared/test1/word/document-pp.xml");

            FileReader reader = reader3;

            long start = System.currentTimeMillis();
            document = (DocxDocument) xlite.fromXML(reader);

            System.out.println("duration read: " + (System.currentTimeMillis() - start));
            System.out.println("store size: "+xlite.getNodeStore().getStoreSize());
            start = System.currentTimeMillis();

            String tmpfile = "/home/peter/tmp/out.xml";
            StringWriter fw = new StringWriter();
            xlite.toXML(document, fw);

            System.out.println("duration write: " + (System.currentTimeMillis() - start));

//            XMLUnit.setIgnoreWhitespace(true);
//            XMLAssert.assertXMLEqual(reader, fr);

        }

////        FileWriter writer = new FileWriter("/home/peter/doc.html", false);
//        for (Paragraph paragraph : document.body.paragraphs) {
//            if (paragraph.runs != null) {
////                writer.append("\n");
//                System.out.println("\n");
//                for (Run run : paragraph.runs) {
//                    if (run.textnode != null) {
////                        writer.append(run.textnode.text);
//                        System.out.print(run.textnode.text);
//                    } else {
//                        System.out.print("|");
//                    }
//                }
//            }
//        }

    }

}
