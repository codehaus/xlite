package si.ptb.xfast;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.lang.reflect.Type;

/**
 * NodeMapper implementations are responsible for serializing/deserializing a xml node's textual data to Java objects
 * Each node mapper
 * User: peter
 * Date: Feb 28, 2008
 * Time: 11:31:27 AM
 */
public interface NodeMapper {

    public boolean canMap(Class type);

    public Object deserialize(Object parentObject, XMLStreamReader reader);

    public void serialize(Object object, XMLStreamWriter writer);

}
