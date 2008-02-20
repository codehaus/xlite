package si.ptb.fastconverter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.util.Map;

/**
 * User: peter
 * Date: Feb 15, 2008
 * Time: 9:38:52 AM
 */
public class FastClassConverter implements Converter {

    private Map<String, ClassMapper> classMappers;

    public FastClassConverter(Class rootClass, String nodeName) {
        classMappers = AnnotationProcessor.processClassTree(rootClass, nodeName);
    }

    /**
     * Determines whether the converter can marshall a particular type.
     *
     * @param type the Class representing the object type to be converted
     */
    public boolean canConvert(Class type) {
        for (ClassMapper classMapper : classMappers.values()) {
            if (classMapper.targetClass.equals(type)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Convert an object to textual data.
     *
     * @param source  The object to be marshalled.
     * @param writer  A stream to write to.
     * @param context A context that allows nested objects to be processed by XStream.
     */
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {

    }

    /**
     * Convert textual data back into an object.
     *
     * @param reader  The stream to read the text from.
     * @param context
     * @return The resulting object.
     */
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        String nodeName = reader.getNodeName();
        String nodeValue = reader.getValue().trim();

        ClassMapper mapper = classMappers.get(nodeName);
        Object object = null;
        // this should not happen - canConvert() method takes care of that
        if (mapper == null) {
            throw new FastConverterException("Can not find class mapper for the node " + nodeName);
        }
        try {
            object = mapper.targetClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        // process node's attributes
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            String attrName = reader.getAttributeName(i);
            String attrValue = reader.getAttribute(i);
            FieldMapper fm = mapper.attributeMappers.get(attrName);
            if (fm != null) {
                fm.set(object, attrValue, context);
            } else {
                System.out.println("node " + nodeName + ": unknown attribute "+attrName);
            }
        }

        // process node value
        if (nodeValue.length() != 0) {
            if (mapper.valueMapper != null) {
                mapper.valueMapper.set(object, nodeValue, context);
            } else {
                System.out.println("node " + nodeName + ": unknown value");
            }
        }

        while (reader.hasMoreChildren()) {
            reader.moveDown();
            String subNodeName = reader.getNodeName();
            FieldMapper fm = mapper.nodeMappers.get(subNodeName);
            if (fm != null) {
                fm.set(object, reader, context);
            } else {
                System.out.println("node " + nodeName + ": unknown subnode "+subNodeName);
            }
            reader.moveUp();
        }

        return object;
    }
}
