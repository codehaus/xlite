package si.ptb.xfast;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.HashMap;

/**
 * User: peter
 * Date: Feb 17, 2008
 * Time: 5:20:50 PM
 */
public class ClassMapper {

    public String nodeName;
    public Class targetClass;
    public Map<String, FieldMapper> nodeMappers = new HashMap<String, FieldMapper>();
    public Map<String, FieldMapper> attributeMappers = new HashMap<String, FieldMapper>();
    public FieldMapper valueMapper;

    public ClassMapper(String nodeName, Class targetClass) {
        this.nodeName = nodeName;
        this.targetClass = targetClass;
    }

}
