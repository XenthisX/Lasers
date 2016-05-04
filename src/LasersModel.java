import java.io.File;
import java.io.FileNotFoundException;
import java.util.Observable;
import java.util.Scanner;

public class LasersModel extends Observable {

    /**
     * an empty cell
     */
    public final static char EMPTY = '.';
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
    /**
     * The status of the program, running or not
     */
    private static boolean running = true;
    /**
     * The grid, stored as a 2D char array
     */
    private char[][] grid;
    /**
     * The width of the Laser Room
     */
    private int width;
    /**
     * The height of the Laser Room
     */
    private int height;

    /**
     * First constructor, creates a safe object when only given a safe, with no laser placements
     *
     * @param safeFile the safe file to parse in
     * @throws FileNotFoundException
     */
    public LasersModel(String safeFile) throws FileNotFoundException {

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

    /**
     * Function to return if simulation is running
     * @return is the simulation running?
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Verifies that the current location is valid and present in the grid
     *
     * @param r the row
     * @param c the column
     * @return true if position is valid, false if not
     */
    public boolean checkCoords(int r, int c) {
        return !(r < 0 || r >= height || c < 0 || c >= height);
    }

    /**
     * Adds a laser given a row and a column, and draws the beams from that coordinate
     *
     * @param r the row to add the laser to
     * @param c the column to add the laser to
     */
    public void add(int r, int c) {
        //If pillar or laser
        if (!checkCoords(r, c)) {
            System.out.println("Error adding laser at: (" + r + ", " + c + ")");
            setChanged();
            notifyObservers();
        } else if ("1234LX".indexOf(grid[r][c]) != -1) {
            System.out.println("Error adding laser at: (" + r + ", " + c + ")");
            setChanged();
            notifyObservers();
        } else {

            System.out.println("Laser added at: (" + r + ", " + c + ")");
            grid[r][c] = 'L';

            setChanged();
            notifyObservers();
        }
    }

    /**
     * Helper functon to run through the grid and run the beam drawing function when a laser is found.
     */
    public void updateBeams() {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (grid[row][col] == LASER) {

                    leftBeam(row, col, BEAM);
                    rightBeam(row, col, BEAM);
                    upBeam(row, col, BEAM);
                    downBeam(row, col, BEAM);
                }
            }
        }
    }

    /**
     * Removes a laser given a row and a column, and removes the beam from the laser
     *
     * @param r the row to remove the laser from
     * @param c the column to remove the laser from
     */
    public void remove(int r, int c) {
        //If pillar or laser
        if (!checkCoords(r, c)) {
            System.out.println("Error removing laser at: (" + r + ", " + c + ")");
            setChanged();
            notifyObservers();
        } else if (grid[r][c] != 'L') {
            System.out.println("Error removing laser at: (" + r + ", " + c + ")");
            setChanged();
            notifyObservers();
        } else {
            System.out.println("Laser removed at: (" + r + ", " + c + ")");
            char t = '.';
            grid[r][c] = t;
            leftBeam(r, c, t);
            rightBeam(r, c, t);
            upBeam(r, c, t);
            downBeam(r, c, t);
            setChanged();
            notifyObservers();
        }

    }

    /**
     * Function to check that lasers are not intersecting with one another, using the multifunctional directionBeam
     * function, checks assuming starting on a laser
     *
     * @param r the row to start verification from
     * @param c the column to start verification from
     * @return true if valid, false if invalid (lasers hitting other lasers)
     */
    public boolean checkBeams(int r, int c) {
        return (!leftBeam(r, c, 'v') && !rightBeam(r, c, 'v') && !upBeam(r, c, 'v') && !downBeam(r, c, 'v'));
    }

    /**
     * Multifunction function for either placing beams, removing beams, or verifying that lasers aren't pointing at
     * other lasers
     * @param r the row to place / remove / verify from
     * @param c the column to place / remove / verify from
     * @param type the desired function, either placing (BEAM), removing (EMPTY), or verifying ('V')
     * @return false unless there are 2 lasers touching, then returns true
     */
    public boolean leftBeam(int r, int c, char type) {
        if (c == 0) {
            return false;
        }
        for (int leftIterator = c - 1; leftIterator >= 0; leftIterator--) {
            if (type == 'v') {
                if (grid[r][leftIterator] == LASER) {
                    return true;
                } else if ("01234X".indexOf(grid[r][leftIterator]) != -1) {
                    return false;
                }
            } else {
                if ("01234LX".indexOf(grid[r][leftIterator]) != -1) {
                    break;
                } else {
                    grid[r][leftIterator] = type;
                }
            }

        }
        return false;
    }

    /**
     * Multifunction function for either placing beams, removing beams, or verifying that lasers aren't pointing at
     * other lasers
     * @param r the row to place / remove / verify from
     * @param c the column to place / remove / verify from
     * @param type the desired function, either placing (BEAM), removing (EMPTY), or verifying ('V')
     * @return false unless there are 2 lasers touching, then returns true
     */
    public boolean rightBeam(int r, int c, char type) {
        if (c == width - 1) {
            return false;
        }
        //right
        for (int rightIterator = c + 1; rightIterator < width; rightIterator++) {
            if (type == 'v') {
                if (grid[r][rightIterator] == LASER) {
                    return true;
                } else if ("01234X".indexOf(grid[r][rightIterator]) != -1) {
                    return false;
                }
            } else {
                if ("01234LX".indexOf(grid[r][rightIterator]) != -1) {
                    break;
                } else {
                    grid[r][rightIterator] = type;
                }
            }
        }
        return false;
    }

    /**
     * Multifunction function for either placing beams, removing beams, or verifying that lasers aren't pointing at
     * other lasers
     * @param r the row to place / remove / verify from
     * @param c the column to place / remove / verify from
     * @param type the desired function, either placing (BEAM), removing (EMPTY), or verifying ('V')
     * @return false unless there are 2 lasers touching, then returns true
     */
    public boolean upBeam(int r, int c, char type) {
        if (r == 0) {
            return false;
        }
        //up
        for (int upIterator = r - 1; upIterator >= 0; upIterator--) {
            if (type == 'v') {
                if (grid[upIterator][c] == LASER) {
                    return true;
                } else if ("01234X".indexOf(grid[upIterator][c]) != -1) {
                    return false;
                }
            } else {
                if ("01234LX".indexOf(grid[upIterator][c]) != -1) {
                    break;
                } else {
                    grid[upIterator][c] = type;
                }
            }
        }
        return false;
    }

    /**
     * Multifunction function for either placing beams, removing beams, or verifying that lasers aren't pointing at
     * other lasers
     * @param r the row to place / remove / verify from
     * @param c the column to place / remove / verify from
     * @param type the desired function, either placing (BEAM), removing (EMPTY), or verifying ('V')
     * @return false unless there are 2 lasers touching, then returns true
     */
    public boolean downBeam(int r, int c, char type) {
        if (r == height - 1) {
            return false;
        }
        //down
        for (int downIterator = r + 1; downIterator < height; downIterator++) {
            if (type == 'v') {
                if (grid[downIterator][c] == LASER) {
                    return true;
                } else if ("01234X".indexOf(grid[downIterator][c]) != -1) {
                    return false;
                }
            } else {
                if ("01234LX".indexOf(grid[downIterator][c]) != -1) {
                    break;
                } else {
                    grid[downIterator][c] = type;
                }
            }
        }
        return false;
    }

    /**
     * A function to check around a pillar and figure out if there are a valid number of lasers around it
     * @param r the row that the pillar is located at
     * @param c the column that the pillar is located at
     * @return the number of lasers around the pillar
     */
    public int checkNeighbors(int r, int c) {
        int laserCount = 0;
        // check left
        if (c > 0) {
            if (grid[r][c - 1] == 'L') laserCount++;
        }
        // check right
        if (c < width - 1) {
            if (grid[r][c + 1] == 'L') laserCount++;
        }
        // check up
        if (r > 0) {
            if (grid[r - 1][c] == 'L') laserCount++;
        }
        if (r < height - 1) {
            if (grid[r + 1][c] == 'L') laserCount++;
        }
        return laserCount;
    }


    /**
     * Quits the program, first sets the running value to false, then exits
     */
    public void quit() {
        running = false;
        System.exit(0);

    }

    /**
     * Verify function, runs other verification functions (checkBeams, checkNeighbors) and if any of them do not verify
     * returns that there was an error verifying
     */
    public void verify() {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                //Checks no aligned lasers
                if (grid[row][col] == LASER) {
                    if (!checkBeams(row, col)) {
                        System.out.println("Error verifying at: (" + row + ", " + col + ")");
                        setChanged();
                        notifyObservers();
                        return;
                    }
                    //Checks correct amount of emitters per pillar
                } else if ("01234X".indexOf(grid[row][col]) != -1) {
                    int neighbors = checkNeighbors(row, col);
                    if (!(grid[row][col] == 'X')) {

                        if (neighbors != Integer.parseInt(grid[row][col] + "")) {
                            System.out.println("Error verifying at: (" + row + ", " + col + ")");
                            setChanged();
                            notifyObservers();
                            return;
                        }
                    }
                    //checks no more empties
                } else if (grid[row][col] == EMPTY) {
                    System.out.println("Error verifying at: (" + row + ", " + col + ")");
                    setChanged();
                    notifyObservers();
                    return;
                }
            }

        }
        System.out.println("Safe is fully verified!");
        setChanged();
        notifyObservers();
    }

    @Override
    public String toString() {
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
            if (row != height - 1) {
                result += "\n";
            }
        }
        return result;
    }
}
