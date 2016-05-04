import java.io.File;
import java.io.FileNotFoundException;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

/**
 * TextUI client for the Lasers Puzzle
 */
public class LasersTextUI implements Observer {

    /**
     * The underlying model
     */
    private LasersModel model;

    /**
     * Construct a LasersTextUI object
     */
    public LasersTextUI(String safefile) {
        this.model = new LasersModel(safefile);
        this.model.addObserver(this);
    }

    /**
     * Main function, runs the simulation with the command line arguments
     *
     * @param args the file / files to read in as either just the grid or the grid/ solution pair
     */
    public static void main(String[] args) {


        if (args.length == 0) {
            System.out.println("Usage: java LasersModel safe-file [input]");
            System.exit(0);
        } else {
            String safefile = args[0];
            LasersTextUI game = new LasersTextUI(safefile);
            if (args.length == 2) {
                game.readInputFile(args[1]);
            }

            game.runSimulation();
        }
    }


    /**
     * Starts the simulation by running an ongoing command loop
     */
    private void runSimulation() {

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
    private void readInputFile(String input) {

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
            display();
        } else if (command.equals("h") || command.equals("help")) {
            displayHelp();
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

    /**
     * Help function, prints out the help message if help or h is entered into the command line
     */
    private void displayHelp() {
        System.out.println("a|add r c: Add laser to (r,c)");
        System.out.println("d|display: Display safe");
        System.out.println("h|help: Print this help message");
        System.out.println("q|quit: Exit program");
        System.out.println("r|remove r c: Remove laser from (r,c)");
        System.out.println("v|verify: Verify safe correctness");
    }

    /**
     * Display function, responsible for first updating the display of the lasers, then printing out the current
     * solution
     */
    private void display() {
        this.model.updateBeams();
        System.out.println(model);
    }

    /**
     * This method is called whenever the observed object is changed. An
     * application calls an <tt>Observable</tt> object's
     * <code>notifyObservers</code> method to have all the object's
     * observers notified of the change.
     *
     * @param o   the observable object.
     * @param arg an argument passed to the <code>notifyObservers</code>
     */
    @Override
    public void update(Observable o, Object arg) {
        display();
    }


}
