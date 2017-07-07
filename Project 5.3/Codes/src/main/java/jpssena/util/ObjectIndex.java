package jpssena.util;

/**
 * Created by João Paulo on 15/06/2017.
 */
public class ObjectIndex <C extends Comparable<? super C>> implements Comparable<ObjectIndex> {
    private int index;
    private C object;

    public ObjectIndex(C object, int index) {
        this.object = object;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public C getObject() {
        return object;
    }

    @Override
    public int compareTo(ObjectIndex o) {
        //noinspection unchecked
        return o.getObject().compareTo(object) * -1;
    }

    public String toString() {
        return object.toString();
    }
}
