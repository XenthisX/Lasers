package ptui;

import model.LasersModel;

import java.io.FileNotFoundException;
import java.util.Observable;
import java.util.Observer;

/**
 * This class represents the view portion of the plain text UI.  It
 * is initialized first, followed by the controller (ControllerPTUI).
 * You should create the model here, and then implement the update method.
 *
 * @author Sean Strout @ RIT CS
 * @author Elijah Bosley, Stefan Marchart
 */
public class LasersPTUI implements Observer {

    /**
     * A horizontal divider
     */
    public final static char HORI_DIVIDE = '-';
    /**
     * A vertical divider
     */
    public final static char VERT_DIVIDE = '|';

    /**
     * The UI's connection to the model
     */
    private LasersModel model;

    /**
     * Construct the PTUI.  Create the model and initialize the view.
     *
     * @param filename the safe file name
     * @throws FileNotFoundException if file not found
     */
    public LasersPTUI(String filename) throws FileNotFoundException {
        try {
            this.model = new LasersModel(filename);
        } catch (FileNotFoundException fnfe) {
            System.out.println(fnfe.getMessage());
            System.exit(-1);
        }
        this.model.addObserver(this);
    }

    public LasersModel getModel() {
        return this.model;
    }

    @Override
    public void update(Observable o, Object arg) {
        this.model.updateBeams();
        if (arg != null) {
            System.out.println(arg);
        }
        System.out.println(createOutput());
        
    }

    private String createOutput() {
        String result = "  ";
        // Creates labels for the top columns
        for (int i = 0; i < this.model.getWidth() + (this.model.getWidth() - 1); i++) {
            if (i % 2 == 0) result += i / 2 + " ";
        }
        result += "\n  ";
        // Creates dividers for the top part of the Laser puzzle
        for (int i = 0; i < this.model.getWidth() + (this.model.getWidth() - 1); i++) {
            result += HORI_DIVIDE;
        }
        result += "\n";
        // nested for loops to generate the visible part of the grid
        for (int row = 0; row < this.model.getHeight(); row++) {
            for (int col = 0; col < this.model.getWidth(); col++) {
                // this if creates the row numbers and left hand divider
                if (col == 0) {
                    result += row + "" + VERT_DIVIDE;
                }

                result += (this.model.getGrid(row, col));

                // this if adds spacing after every item gets placed in the puzzle
                if (col >= 0 && col < this.model.getWidth() - 1) {
                    result += " ";
                }

            }
            // new line after running through the whole X line
            if (row != this.model.getHeight() - 1) {
                result += "\n";
            }
        }
        return result;
    }
    public static void displayHelp() {
            System.out.println("a|add r c: Add laser to (r,c)");
            System.out.println("d|display: Display safe");
            System.out.println("h|help: Print this help message");
            System.out.println("q|quit: Exit program");
            System.out.println("r|remove r c: Remove laser from (r,c)");
            System.out.println("v|verify: Verify safe correctness");

    }
}
