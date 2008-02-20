package si.ptb.fastconverter;

import java.util.ArrayList;
import java.util.List;

/**
 * User: peter
 * Date: Feb 14, 2008
 * Time: 3:11:03 PM
 */
public class ObjectReference {

    private Object referent;

    public List storedNodes = new ArrayList();
    public List storedAttributes = new ArrayList();

    /**
     * Creates a new weak reference that refers to the given object.  The new
     * reference is not registered with any queue.
     *
     * @param referent object the new weak reference will refer to
     */
    public ObjectReference(Object referent) {
        this.referent = referent;
    }

    public Object get(){
        return referent;
    }


}
