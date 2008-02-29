package si.ptb.xfast;

import si.ptb.xfast.FieldMapper;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * User: peter
 * Date: Feb 28, 2008
 * Time: 10:19:19 PM
 */
public abstract class AnnotatedMapper implements Mapper {

    protected String nodeName;
    protected Class targetClass;
    protected Field parentReference;
    protected FieldMapper valueMapper;
    protected Map<String, Mapper> nodeMappers = new HashMap<String, Mapper>();
    protected Map<String, FieldMapper> attributeConverters = new HashMap<String, FieldMapper>();

    public AnnotatedMapper(Class targetClass, String nodeName) {
        this.targetClass = targetClass;
        this.nodeName = nodeName;
    }

    public FieldMapper getValueConverter() {
        return valueMapper;
    }

    public void setValueConverter(FieldMapper valueMapper) {
        this.valueMapper = valueMapper;
    }

    public void addNodeMapper(String nodeName, Mapper mapper) {
        nodeMappers.put(nodeName, mapper);
    }

    public void addAttributeConverter(String attributeName, FieldMapper fieldMapper) {
        attributeConverters.put(attributeName, fieldMapper);
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public Class getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class targetClass) {
        this.targetClass = targetClass;
    }


    public Field getParentReference() {
        return parentReference;
    }

    public void setParentReference(Field parentReference) {
        this.parentReference = parentReference;
    }
}
