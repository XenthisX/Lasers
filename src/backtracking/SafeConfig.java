package backtracking;


import model.LasersModel;

import java.io.FileNotFoundException;
import java.util.Collection;

/**
 * The class represents a single configuration of a safe.  It is
 * used by the backtracker to generate successors, check for
 * validity, and eventually find the goal.
 * <p>
 * This class is given to you here, but it will undoubtedly need to
 * communicate with the model.  You are free to move it into the model
 * package and/or incorporate it into another class.
 *
 * @author Sean Strout @ RIT CS
 * @author YOUR NAME HERE
 */
public class SafeConfig extends LasersModel implements Configuration{


    public SafeConfig(String filename) throws FileNotFoundException {
        super(filename);
    }


    @Override
    public Collection<Configuration> getSuccessors() {




        return null;
    }

    @Override
    public boolean isValid() {
        // TODO
        return false;
    }

    @Override
    public boolean isGoal() {
        // TODO
        return false;
    }
}
