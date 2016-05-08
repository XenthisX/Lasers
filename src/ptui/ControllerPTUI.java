package ptui;

import model.LasersModel;

/**
 * This class represents the controller portion of the plain text UI.
 * It takes the model from the view (LasersPTUI) so that it can perform
 * the operations that are input in the run method.
 *
 * @author Sean Strout @ RIT CS
 * @author YOUR NAME HERE
 */
public class ControllerPTUI {
    /**
     * The UI's connection to the model
     */
    private LasersModel model;

    /**
     * Construct the PTUI.  Create the model and initialize the view.
     *
     * @param model The laser model
     */
    public ControllerPTUI(LasersModel model) {
        this.model = model;
    }

    /**
     * Run the main loop.  This is the entry point for the controller
     *
     * @param inputFile The name of the input command file, if specified
     */
    public void run(String inputFile) {
        // TODO
    }
}
