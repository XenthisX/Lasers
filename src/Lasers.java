/**
 * Created by Elijah Bosley on 5/5/2016.
 */
public class Lasers {

    /**
     * Main reads in the initial
     *
     * @param args
     */
    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("Usage: java Lasers (gui | ptui) safe-file [input]");
            System.exit(0);
        } else {
            String safefile = args[0];
            LasersTextVC game = new LasersTextVC(safefile);
            if (args.length == 2) {
                game.readInputFile(args[1]);
            }

            game.runSimulation();
        }
    }
}
