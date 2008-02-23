package old;

import si.ptb.xfast.ClassMapper;
import si.ptb.xfast.ArrayUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * This class represents a List of ClassMappers. It was created to be as fast as possible.
 * Internally it uses an array and a ClassMapper Comparer to keep this array sorted for fast binary search.
 * <p/>
 * This class is NOT THREAD-SAFE!
 * User: peter
 * Date: Feb 17, 2008
 * Time: 5:38:37 PM
 */
public class ClassMapperHolder {
    private int increment = 10;
    private int length = 20;
    private int lastIndex = 0;
    private ClassMapper[] mappers = new ClassMapper[length];
    private boolean sortNeeded = false;


    public void add(ClassMapper classMapper) {
        needsResize();
        mappers[lastIndex] = classMapper;
        lastIndex++;
        sortNeeded = true;
    }

    public void sortByNodeName() {
        if (sortNeeded) {
            Arrays.sort(mappers, new ClassMapperComparer());
            sortNeeded = false;
        }
    }

    public ClassMapper get(String nodeName) {
        sortByNodeName();
        ClassMapper fakeMapper = new ClassMapper(nodeName, null);
        int index = Arrays.binarySearch(mappers, fakeMapper, new ClassMapperComparer());
        return mappers[index];
    }

    public List<String> getNodeNames() {
        sortByNodeName();
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < lastIndex; i++) {
            list.add(mappers[i].nodeName);
        }
        return list;
    }

    private void needsResize() {
        if (length - 1 == lastIndex) {
            mappers = ArrayUtil.arrayCopy(mappers, length + increment);
            length += increment;
        }
    }

    public static class ClassMapperComparer implements Comparator<ClassMapper> {
        public int compare(ClassMapper o1, ClassMapper o2) {
            if (o1 == null && o2 != null) {
                return 1;
            } else if (o1 != null && o2 == null) {
                return -1;
            } else if (o1 == null) {
                return 0;
            } else {
                return o1.nodeName.compareTo(o2.nodeName);
            }
        }
    }
}
