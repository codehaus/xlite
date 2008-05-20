package info.documan.xlite;

/**
 * @author peter
 */
public class NodeHolder {
    private SubTreeStore store;

    public NodeHolder(int size, int increment) {
        this.store = new SubTreeStore(size,increment);
    }

    public SubTreeStore getStore() {
        return store;
    }
}
