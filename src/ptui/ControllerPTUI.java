package ptui;

import model.LasersModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * This class represents the controller portion of the plain text UI.
 * It takes the model from the view (LasersPTUI) so that it can perform
 * the operations that are input in the run method.
 *
 * @author Sean Strout @ RIT CS
 * @author Elijah Bosley, Stefan Marchart
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
        if (inputFile != null) {
            readInputFile(inputFile);
        }
        model.announceChange();
        Scanner in = new Scanner(System.in);
        while (model.isRunning()) {
            System.out.print("> ");
            this.parseCommand(in.nextLine(), false);

        }
    }

    /**
     * Read the input solution file and run parse command on every line
     *
     * @param input the name of the input file
     */
    public void readInputFile(String input) {
        Scanner in = null;
        try {
            in = new Scanner(new File(input));
        } catch (FileNotFoundException e) {
            System.out.println("Safe setup file: \'" + input + "\' not found.");
        }
        while (in.hasNextLine()) {
            String line = in.nextLine();
            this.parseCommand(line, true);

        }
    }

    /**
     * Reads in commands and executes them
     *
     * @param str    the string to parse
     * @param isFile a boolean that if true will echo the inputted command, otherwise will not
     */
    private void parseCommand(String str, boolean isFile) {
        String[] line = str.split(" ");
        if (str.equals("")) {
            return;
        }
        String command = line[0];
        if (command.equals("a") || command.equals("add")) {
            if (line.length != 3) {
                System.out.println("Incorrect coordinates");
                return;
            }
            if (isFile) System.out.println("> a " + line[1] + " " + line[2]);
            this.model.add(Integer.parseInt(line[1]), Integer.parseInt(line[2]));
            //if (isFile) System.out.println("> a " + line[1] + " " + line[2]);
        } else if (command.equals("d") || command.equals("display")) {
            model.announceChange();
        } else if (command.equals("h") || command.equals("help")) {
            //displayHelp();
        } else if (command.equals("q") || command.equals("quit")) {
            this.model.quit();
        } else if (command.equals("r") || command.equals("remove")) {
            if (line.length != 3) {
                System.out.println("Incorrect coordinates");
                return;
            }
            if (isFile) {
                System.out.println("> r " + line[1] + " " + line[2]);
            }
            this.model.remove(Integer.parseInt(line[1]), Integer.parseInt(line[2]));
        } else if (command.equals("v") || command.equals("verify")) {
            this.model.verify();
        } else {
            System.out.println("Unrecognized command: " + str);
        }
    }
}
