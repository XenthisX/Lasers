import java.io.File;
import java.io.FileNotFoundException;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

/**
 * Created by Elijah Bosley on 5/3/2016.
 */
public class LasersTextUI implements Observer {

    /**
     * The underlying model
     */
    private LasersModel model;

    /**
     * Construct a LasersTextUI object
     */
    public LasersTextUI() {
        this.model.addObserver(this);
    }

    /**
     * Main function, runsthe simulation with the command line arguments
     *
     * @param args the file / files to read in as either just the grid or the grid/ solution pair
     */
    public static void main(String[] args) {
        LasersTextUI game = new LasersTextUI();

        try {
            game.runSimulation(args);
        } catch (FileNotFoundException e) {
            System.out.println("File: \'" + args[0] + "\' not found.");
        }
    }

    /**
     * Starts the simulation by running an ongoing command loop
     */
    private void runSimulation(String[] args) throws FileNotFoundException {

        if (args.length == 0) {
            System.out.println("Usage: java LasersModel safe-file [input]");
            System.exit(0);
        } else {
            model = new LasersModel(args[0]);
            if (args.length == 2) {
                this.readInputFile(args[1]);
            }

            Scanner in = new Scanner(System.in);
            while (model.isRunning()) {
                System.out.print("> ");
                this.parseCommand(in.nextLine(), false);

            }
        }

    }

    /**
     * Read the input solution file and run parse command on every line of it
     *
     * @param input the name of the input file
     * @throws FileNotFoundException
     */
    public void readInputFile(String input) throws FileNotFoundException {
        Scanner in = new Scanner(new File(input));
        while (in.hasNextLine()) {
            String line = in.nextLine();
            this.parseCommand(line, true);

        }
    }

    /**
     * Reads in commands and executes them
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
            if (isFile) System.out.println("> r " + line[1] + " " + line[2]);
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
    public void displayHelp() {
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
    public void display() {
        this.model.updateBeams();
        System.out.println(this);
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

    }


}
