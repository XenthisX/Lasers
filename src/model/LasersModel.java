package model;

import java.io.FileNotFoundException;
import java.util.Observable;

public class LasersModel extends Observable {

    public LasersModel(String filename) throws FileNotFoundException {
        // TODO
    }

    /**
     * A utility method that indicates the model has changed and
     * notifies observers
     */
    private void announceChange() {
        setChanged();
        notifyObservers();
    }
}
