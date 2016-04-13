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


    public void readInputFile(String input) throws Exception {
        Scanner in = new Scanner(new File(input));
        while (in.hasNextLine()) {
            String line = in.nextLine();

            parseCommand(line);
        }
    }

    public void parseCommand(String str) throws Exception {
        String[] line = str.split(" ");
        String command = line[0];
        if (command.equals("a") || command.equals("add")) {
            if (line.length != 3) {
                throw new Exception("Incorrect coordinates");
            }
            add(Integer.parseInt(line[1]), Integer.parseInt(line[2]));
        } else if (command.equals("d") || command.equals("display")) {
            display();
        } else if (command.equals("h") || command.equals("help")) {
            help();
        } else if (command.equals("q") || command.equals("quit")) {
            quit();
        } else if (command.equals("r") || command.equals("remove")) {
            if (line.length != 3) {
                throw new Exception("Incorrect coordinates");
            }
            remove(Integer.parseInt(line[1]), Integer.parseInt(line[2]));
        } else if (command.equals("v") || command.equals("verify")) {
            verify();
        } else {
            throw new Exception("Unknown Command: ", new Throwable(line[0]));
        }
    }

    public boolean checkCoords(int r, int c) {
        if (r < 0 || r >= height || c < 0 || c >= height) {
            return false;
        } else {
            return true;
        }
    }

    public void add(int r, int c) {
        //If pillar or laser
        if ("1234LX".indexOf(grid[r][c]) != -1 || !checkCoords(r, c)) {
            System.out.println("Error adding laser at: (" + r + ", " + c + ")");
            display();
        } else {
            System.out.println("Laser added at: (" + r + ", " + c + ")");
            grid[r][c] = 'L';
            char t='*';
            leftBeam(r,c,t);
            rightBeam(r,c,t);
            upBeam(r,c,t);
            downBeam(r,c,t);
            display();
        }
    }


    public void leftBeam(int r, int c,char type) {
        if (c == 0) {
            return;
        }
        for (int leftIterator = c - 1; leftIterator >= 0; leftIterator--) {
            if ("01234LX".indexOf(grid[r][leftIterator]) != -1) {
                break;
            } else {
                grid[r][leftIterator] = type;
            }
        }
    }

    public void rightBeam(int r, int c,char type) {
        if (c == width - 1) {
            return;
        }
        //right
        for (int rightIterator = c + 1; rightIterator < width; rightIterator++) {
            if ("01234LX".indexOf(grid[r][rightIterator]) != -1) {
                break;
            } else {
                grid[r][rightIterator] = type;
            }
        }
    }

    public void upBeam(int r, int c,char type) {
        if (r == 0) {
            return;
        }
        //up
        for (int upIterator = r-1; upIterator >= 0; upIterator--) {
            if ("01234LX".indexOf(grid[upIterator][c]) != -1) {
                break;
            } else {
                grid[upIterator][c] = type;
            }
        }
    }

    public void downBeam(int r, int c,char type) {
        if (r == height - 1) {
            return;
        }
        //down
        for (int downIterator = r+1; downIterator < height; downIterator++) {
            if ("01234LX".indexOf(grid[downIterator][c]) != -1) {
                break;
            } else {
                grid[downIterator][c] = type;
            }
        }
    }


    public void display() {
        System.out.println(this);
    }

    public void help() {
        System.out.println("a|add r c: Add laser to (r,c)");
        System.out.println("d|display: Display safe");
        System.out.println("h|help: Print this help message");
        System.out.println("q|quit: Exit program");
        System.out.println("r|remove r c: Remove laser from (r,c)");
        System.out.println("v|verify: Verify safe correctness");
    }

    public void quit() {
        System.exit(0);
    }

    public void remove(int r, int c) {
        //If pillar or laser
        if (grid[r][c] != 'L' || !checkCoords(r, c)) {
            System.out.println("Error removing laser at: (" + r + ", " + c + ")");
            display();
        } else {
            System.out.println("Laser removed at: (" + r + ", " + c + ")");
            char t='.';
            grid[r][c] = t;
            leftBeam(r,c,t);
            rightBeam(r,c,t);
            upBeam(r,c,t);
            downBeam(r,c,t);
            display();
        }
        display();
    }

    public void verify() {

    }

    @Override
    public String toString() { //TODO for some reason this method is outputting a mirrored top and bottom row
        String result = "  ";
        // Creates labels for the top columns
        for (int i = 0; i < width + (width - 1); i++) {
            if (i % 2 == 0) result += i / 2 + " ";
        }
        result += "\n  ";
        // Creates dividers for the top part of the Laser puzzle
        for (int i = 0; i < width + (width - 1); i++) {
            result += HORI_DIVIDE;
        }
        result += "\n";
        // nested for loops to generate the visible part of the grid
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                // this if creates the row numbers and left hand divider
                if (col == 0) {
                    result += row + "" + VERT_DIVIDE;
                }

                result += (grid[row][col]);

                // this if adds spacing after every item gets placed in the puzzle
                if (col >= 0 && col < width - 1) {
                    result += " ";
                }

            }
            // new line after running through the whole X line
            result += "\n";
        }
        return result;
    }


    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Usage: java LasersPTUI safe-file [input]");
            System.exit(0);
        }
        LasersPTUI lasers = new LasersPTUI(args[0]);
        if (args.length == 2) {
            lasers.readInputFile(args[1]);
        }

        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            lasers.parseCommand(in.nextLine());
        }

    }
}
