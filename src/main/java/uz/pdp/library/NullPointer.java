package uz.pdp.library;

import static uz.pdp.library.SessionMessage.NOT_FOUND;
import static uz.pdp.library.Util.CYAN;
import static uz.pdp.library.Util.print;

public class NullPointer<T> {

    public T findObject(T object) {
        try {
            return object;
        } catch (NullPointerException e) {
            print(CYAN, NOT_FOUND);
        }
        return findObject(object);
    }

}
