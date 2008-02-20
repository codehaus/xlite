package old;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import si.ptb.xfast.docx.Paragraph;
import si.ptb.xfast.docx.Run;

/**
 * User: peter
 * Date: Feb 16, 2008
 * Time: 9:27:21 PM
 */

public class ParagraphConverter implements Converter {

    static int count;

    public boolean canConvert(Class type) {
        return type.equals(Paragraph.class);
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {

    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        Paragraph paragraph = new Paragraph();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            if ("w:r".equals(reader.getNodeName())) {
                Run run = (Run) context.convertAnother(paragraph, Run.class);
                paragraph.runs.add(run);
            } else {
                paragraph.saveNodeTree(reader);
            }
            reader.moveUp();
        }

        count++;
        if (count % 1000 == 0) {
            System.out.println("paragraph: " + count);
        }

        return paragraph;
    }

}
