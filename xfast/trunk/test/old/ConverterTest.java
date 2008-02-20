package old;

import com.thoughtworks.xstream.XStream;
import org.testng.annotations.Test;
import si.ptb.xfast.docx.*;

import java.io.*;

/**
 * User: peter
 * Date: Feb 16, 2008
 * Time: 9:34:14 PM
 */
public class ConverterTest {

    @Test
    public void testParse() throws FileNotFoundException {

//        File docFile = new File("/home/peter/vmware/shared/Office Open XML Part 3 - Primer/word/document.xml");
        File docFile = new File("/home/peter/vmware/shared/Office Open XML Part 4 - Markup Language Reference/word/document.xml");
        FileInputStream stream = new FileInputStream(docFile);

        XStream xstream = new XStream();
        xstream.alias("w:document", DocxDocument.class);
//        xstream.alias("w:body", Body.class);
//
//        xstream.alias("w:p", Paragraph.class);
//
//        xstream.alias("w:r", Run.class);
//        xstream.aliasField("w:t", Run.class, "text");

        xstream.registerConverter(new DocxDocumentConverter());
        xstream.registerConverter(new BodyConverter());
        xstream.registerConverter(new ParagraphConverter());
        xstream.registerConverter(new RunConverter());

        long start = System.currentTimeMillis();
        DocxDocument document = (DocxDocument) xstream.fromXML(stream);

        System.out.println("duration: " + (System.currentTimeMillis() - start));

        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter("doc.html"));
            out.write("<html><body>");

            out.write("<p>");
            out.write("/<just a node/>");
            out.write("</p>");

            for (Paragraph paragraph : document.body.paragraphs) {
                StringBuilder text = new StringBuilder();
                if (paragraph.runs != null) {
                    for (Run run : paragraph.runs) {
                        if (run.text != null) {
                            text.append(stringToHTMLString(run.text));
                        }
                    }
                    out.write("<p>");
                    out.write(text.toString());
                    out.write("</p>");
//                    System.out.println(text.toString());
                }
            }

            out.write("</body></html>");
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static String stringToHTMLString(String string) {
        StringBuffer sb = new StringBuffer(string.length());
        // true if last char was blank
        boolean lastWasBlankChar = false;
        int len = string.length();
        char c;

        for (int i = 0; i < len; i++) {
            c = string.charAt(i);
            if (c == ' ') {
                // blank gets extra work,
                // this solves the problem you get if you replace all
                // blanks with &nbsp;, if you do that you loss
                // word breaking
                if (lastWasBlankChar) {
                    lastWasBlankChar = false;
                    sb.append("&nbsp;");
                } else {
                    lastWasBlankChar = true;
                    sb.append(' ');
                }
            } else {
                lastWasBlankChar = false;
                //
                // HTML Special Chars
                if (c == '"')
                    sb.append("&quot;");
                else if (c == '&')
                    sb.append("&amp;");
                else if (c == '<')
                    sb.append("&lt;");
                else if (c == '>')
                    sb.append("&gt;");
                else if (c == '\n')
                    // Handle Newline
                    sb.append("&lt;br/&gt;");
                else {
                    int ci = 0xffff & c;
                    if (ci < 160)
                        // nothing special only 7 Bit
                        sb.append(c);
                    else {
                        // Not 7 Bit use the unicode system
                        sb.append("&#");
                        sb.append(new Integer(ci).toString());
                        sb.append(';');
                    }
                }
            }
        }
        return sb.toString();
    }
}
