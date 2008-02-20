package si.ptb.xfast.docx;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import si.ptb.xfast.docx.Paragraph;
import si.ptb.xfast.docx.Body;

/**
 * User: peter
 * Date: Feb 15, 2008
 * Time: 10:52:13 PM
 */
public class BodyConverter implements Converter {
    public BodyConverter() {
        System.out.println("BodyConverter!");
    }

    public boolean canConvert(Class type) {
        return type.equals(Body.class);
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {

    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        Body body = new Body();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            if ("w:p".equals(reader.getNodeName())) {
                Paragraph paragraph = (Paragraph) context.convertAnother(body, Paragraph.class);
                body.paragraphs.add(paragraph);
            } else {
                body.saveNodeTree(reader);
            }
            reader.moveUp();
        }
        return body;
    }
}
