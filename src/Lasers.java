import javafx.application.Application;

/**
 * Created by Elijah Bosley on 5/5/2016.
 */
public class Lasers {

    /**
     * Main reads in the initial file and decides whether to run the text-based simulation or the GUI
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("Usage: java Lasers (gui | ptui) safe-file [input]");
            System.exit(0);
        } else if (args.length <= 3) {
            String uiChoice = args[0];
            String safefile = args[1];
            switch (uiChoice) {
                case "ptui":
                    LasersTextVC textGame = new LasersTextVC(safefile);
                    if (args.length == 3) {
                        textGame.model.readInputFile(args[1]);
                    }
                    textGame.runSimulation();
                case "gui":
                    LasersGraphicalVC guiGame = new LasersGraphicalVC(safefile);
                    if (args.length == 3) {
                        guiGame.model.readInputFile(args[1]);
                    }
                    Application.launch(args);
            }

        }
        }
    }
