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

    }

    public LasersPTUI(String safeFile, String inital) throws FileNotFoundException {

        this(safeFile);


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
        System.out.println("My name is Stefan Marchhart");
        System.out.println("My name is Elijah Bosley");
        System.out.println("Our project account is p142-05l");
    }
}
