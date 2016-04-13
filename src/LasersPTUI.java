import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class LasersPTUI {

    /**
     * an empty cell
     */
    public final static char EMPTY = '.';
    /**
     * a cell occupied with a pillar that accepts any # of lasers
     */
    public final static char ANYPILLAR = 'X';
    /**
     * a cell occupied with a laser emitter
     */
    public final static char LASER = 'L';
    /**
     * a cell occupied with a laser beam
     */
    public final static char BEAM = '*';

    // OUTPUT CONSTANTS
    /**
     * A horizontal divider
     */
    public final static char HORI_DIVIDE = '-';
    /**
     * A vertical divider
     */
    public final static char VERT_DIVIDE = '|';

    private char[][] grid;
    private int width;
    private int height;
    private int curRow;
    private int curCol;

    /**
     * First constructor, creates a safe object when only given a safe, with no laser placements
     *
     * @param safeFile the safe file to parse in
     * @throws FileNotFoundException
     */
    public LasersPTUI(String safeFile) throws FileNotFoundException {

        Scanner in = new Scanner(new File(safeFile));

        width = Integer.parseInt(in.next());
        height = Integer.parseInt(in.next());

        grid = new char[height][width];

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                grid[row][col] = in.next().charAt(0);
            }
        }
        in.close();
        System.out.println(this);
    }

    public LasersPTUI(String safeFile, String inital) throws FileNotFoundException {

        this(safeFile);


    }

    @Override
    
    public String toString() { //TODO for some reason this method is outputting a mirrored top and bottom row
        String result = "  ";
        // Creates labels for the top columns
        for (int i = 0; i < width + (width - 1); i++) {
            if (i%2 == 0) result += i/2 + " ";
        }
        result += "\n  ";
        // Creates dividers for the top part of the Laser puzzle
        for (int i = 0; i < width + (width - 1); i++) {
            result += HORI_DIVIDE;
        }
        result += "\n";
        // nested for loops to generate the visible part of the grid
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // this if creates the row numbers and left hand divider
                if (x == 0) {
                    result += y + "" + VERT_DIVIDE;
                }

                result += (grid[x][y]);

                // this if adds spacing after every item gets placed in the puzzle
                if (x >= 0 && x < width - 1) {
                    result += " ";
                }

            }
            // new line after running through the whole X line
            result += "\n";
        }
        return result;
    }




    public static void main(String[] args) throws FileNotFoundException {

        switch (args.length) {
            default:
                System.out.println("Usage: java LasersPTUI safe-file [input]");
                System.exit(0);
                break;
            case 1:

                LasersPTUI lasers = new LasersPTUI(args[0]);
                break;
            case 2:
                LasersPTUI lazers = new LasersPTUI(args[0], args[1]);
                break;
        }

    }
}
