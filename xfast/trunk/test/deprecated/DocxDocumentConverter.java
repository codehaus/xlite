package deprecated;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import si.ptb.xfast.docx.DocxDocument;
import si.ptb.xfast.docx.Body;

/**
 * User: peter
 * Date: Feb 16, 2008
 * Time: 9:41:00 PM
 */
public class DocxDocumentConverter implements Converter{

    public boolean canConvert(Class type) {
        return type.equals(DocxDocument.class);
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        DocxDocument docxDocument = new DocxDocument();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            if ("w:body".equals(reader.getNodeName())) {
                Body body = (Body) context.convertAnother(docxDocument, Body.class);
                docxDocument.body = body;
            } else {
//                docxDocument.saveNodeTree(reader);
            }
            reader.moveUp();
        }
        return docxDocument;
    }
}
