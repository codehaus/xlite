package si.ptb.xlite.docx;

import si.ptb.xlite.XMLnode;
import si.ptb.xlite.Xlite;

import java.io.FileReader;
import java.io.IOException;

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

    public static void main(String[] args) throws IOException {

        DocxDocument document = null;
        Xlite xlite = new Xlite(DocxDocument.class, "w:document");
        xlite.isStoringUnknownNodes = true;
        xlite.addNamespace("w=http://schemas.openxmlformats.org/wordprocessingml/2006/main");

        FileReader reader4 = new FileReader("/home/peter/vmware/shared/Office Open XML Part 4 - Markup Language Reference/word/document.xml");
        FileReader reader3 = new FileReader("/home/peter/vmware/shared/Office Open XML Part 3 - Primer/word/document_pp.xml");
        for (int i = 0; i < 1; i++) {
            long start = System.currentTimeMillis();
            document = (DocxDocument) xlite.fromXML(reader4);

            System.out.println("duration: " + (System.currentTimeMillis() - start));

        }

//        FileWriter writer = new FileWriter("/home/peter/doc.html", false);
//        for (Paragraph paragraph : document.body.paragraphs) {
//            if (paragraph.runs != null) {
//                writer.append("\n");
//                for (Run run : paragraph.runs) {
//                    if (run.textnode != null) {
//                        writer.append(run.textnode.text);
//                    }
//                }
//            }
//        }
    }

}
