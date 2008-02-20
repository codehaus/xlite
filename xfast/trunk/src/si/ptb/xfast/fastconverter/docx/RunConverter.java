package si.ptb.fastconverter.docx;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * User: peter
 * Date: Feb 16, 2008
 * Time: 9:29:15 PM
 */
public class RunConverter implements Converter {


    public boolean canConvert(Class type) {
        return type.equals(Run.class);
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        Run run = new Run();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            if ("w:t".equals(reader.getNodeName())) {
                run.text = reader.getValue();
            }else {
                run.saveNodeTree(reader);
            }
            reader.moveUp();
        }
        return run;
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
