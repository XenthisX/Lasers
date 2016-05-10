package model;

import backtracking.Configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * A model for the Lasers Puzzle. Contains many helper functions to assist with Backtracking and other useful MVC
 * interactions
 *
 * @author Elijah Bosley
 * @author Stefan Marchhart
 */

public class LasersModel extends Observable implements Configuration {

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
    private char[][] initalGrid;
    private int width;
    /**
     * The height of the Laser Room
     */
    private int height;

    private int currentRow;
    private int currentCol;

    private ArrayList<Coordinate> lasers;
    private ArrayList<Coordinate> pillars;


    public LasersModel(String filename) throws FileNotFoundException {
        Scanner in = null;
        in = new Scanner(new File(filename));

        width = Integer.parseInt(in.next());
        height = Integer.parseInt(in.next());

        initalGrid = new char[height][width];
        grid = new char[height][width];

        lasers = new ArrayList<>();
        pillars = new ArrayList<>();
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                char temp = in.next().charAt(0);
                grid[row][col] = temp;
                initalGrid[row][col] = temp;
                if ("01234LX".indexOf(grid[row][col]) != -1) {
                    pillars.add(new Coordinate(row, col));
                }
            }
        }
        in.close();
        currentCol = -1;
        currentRow = 0;
    }

    /**
     * Copy constructor used by getSuccessors
     *
     * @param other the model to copy from
     */
    public LasersModel(LasersModel other) {
        this.width = other.width;
        this.height = other.height;

        this.grid = new char[width][width];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                this.grid[row][col] = other.grid[row][col];
            }
        }
        this.initalGrid = other.initalGrid;
        this.pillars = new ArrayList<>(other.pillars);
        this.lasers = new ArrayList<>(other.lasers);
        this.currentCol = other.currentCol;
        this.currentRow = other.currentRow;
    }

    /**
     * Function to return if simulation is running
     *
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
            setChanged();
            notifyObservers("Error adding laser at: (" + r + ", " + c + ")");
        } else if ("01234LX".indexOf(grid[r][c]) != -1) {
            setChanged();
            notifyObservers("Error adding laser at: (" + r + ", " + c + ")");
        } else {
            grid[r][c] = 'L';
            lasers.add(new Coordinate(r, c));
            setChanged();
            notifyObservers("Laser added at: (" + r + ", " + c + ")");
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
            setChanged();
            notifyObservers("Error removing laser at: (" + r + ", " + c + ")");
        } else if (grid[r][c] != 'L') {
            setChanged();
            notifyObservers("Error removing laser at: (" + r + ", " + c + ")");
        } else {
            char t = '.';
            grid[r][c] = t;
            leftBeam(r, c, t);
            rightBeam(r, c, t);
            upBeam(r, c, t);
            downBeam(r, c, t);
            Coordinate coord = new Coordinate(r, c);
            for (int iter = 0; iter < lasers.size(); iter++) {
                if (lasers.get(iter).equals(coord)) {
                    lasers.remove(iter);
                    break;
                }
            }
            setChanged();
            notifyObservers("Laser removed at: (" + r + ", " + c + ")");
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
     *
     * @param r    the row to place / remove / verify from
     * @param c    the column to place / remove / verify from
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
     *
     * @param r    the row to place / remove / verify from
     * @param c    the column to place / remove / verify from
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
     *
     * @param r    the row to place / remove / verify from
     * @param c    the column to place / remove / verify from
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
     *
     * @param r    the row to place / remove / verify from
     * @param c    the column to place / remove / verify from
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
     *
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
                        setChanged();
                        notifyObservers("Error verifying at: (" + row + ", " + col + ")");
                        return;
                    }
                    //Checks correct amount of emitters per pillar
                } else if ("01234X".indexOf(grid[row][col]) != -1) {
                    int neighbors = checkNeighbors(row, col);
                    if (!(grid[row][col] == 'X')) {

                        if (neighbors != Integer.parseInt(grid[row][col] + "")) {
                            setChanged();
                            notifyObservers("Error verifying at: (" + row + ", " + col + ")");
                            return;
                        }
                    }
                    //checks no more empties
                } else if (grid[row][col] == EMPTY) {
                    setChanged();
                    notifyObservers("Error verifying at: (" + row + ", " + col + ")");
                    return;
                }
            }

        }
        setChanged();
        notifyObservers("Safe is fully verified!");
    }

    /**
     * A utility method that indicates the model has changed and
     * notifies observers
     */
    public void announceChange() {
        setChanged();
        notifyObservers();
    }

    /**
     * Getter for width
     *
     * @return the width of the grid
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * Getter for height
     *
     * @return the height of the grid
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * Getter for the character contained at row,col
     *
     * @param row the row of the grid to check
     * @param col the column of the grid to check
     * @return char pertaining to the value at grid[row][col]
     */
    public char getGrid(int row, int col) {
        return this.grid[row][col];
    }

    /**
     * Returns an Arraylist of coordinates of the lasers in the grid
     *
     * @return Arraylist containing coordinate pairs for the location of the placed lasers
     */
    public ArrayList<Coordinate> getLasers() {
        return lasers;
    }

    /**
     * Helper function that resets all lasers and beams to their default empty state
     */
    public void reset() {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if ("01234X".indexOf(grid[row][col]) == -1) {
                    grid[row][col] = EMPTY;
                }
            }
        }
        lasers = new ArrayList<>();
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
            result += "-";
        }
        result += "\n";
        // nested for loops to generate the visible part of the grid
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                // this if creates the row numbers and left hand divider
                if (col == 0) {
                    result += row + "" + "|";
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

    @Override
    public Collection<Configuration> getSuccessors() {
        if (currentCol == width - 1) {
            currentRow++;
            currentCol = 0;
        } else {
            currentCol++;
        }
        if(currentRow > height) {
            return new ArrayList<Configuration>();
        }




        LasersModel model1 = new LasersModel(this);
        model1.add(currentRow, currentCol);
        LasersModel model2 = new LasersModel(this);

        ArrayList<Configuration> configList = new ArrayList<>();

        model1.updateBeams();
        model2.updateBeams();
        configList.add(model1);
        configList.add(model2);

        return configList;

    }

    @Override
    public boolean isValid() {


//No intersecting Beams
        for (Coordinate l : lasers) {
            if (!checkBeams(l.getRow(), l.getCol())) {
                return false;
            }
        }


//Not too many emitters on pillars
        for (Coordinate p : pillars) {
            if (grid[p.getRow()][p.getCol()] != 'X') {
                int temp = Character.getNumericValue(grid[p.getRow()][p.getCol()]);

                if (checkNeighbors(p.getRow(), p.getCol()) > temp) {
                    return false;
                }
            }
        }

        return true;


    }

    @Override
    public boolean isGoal() {

        if(!(currentCol==width-1&&currentRow==height-1)){
            return false;
        }
        if(!isValid()){
            return false;
        }
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {


                if (grid[row][col] == EMPTY) {
                    return false;
                }
                if ("01234X".indexOf(grid[row][col]) != -1) {
                    if (grid[row][col] != 'X') {
                        int temp = Character.getNumericValue(grid[row][col]);
                        if (checkNeighbors(row, col) != temp) {
                            return false;
                        }
                    }

                }
            }
        }
        return true;
    }

    /**
     * Updates the model using a new file, overwrites the configuration by reading in the new one
     *
     * @param filename the file to read in
     * @throws FileNotFoundException
     */
    public void updateModel(String filename) throws FileNotFoundException {
        Scanner in;
        in = new Scanner(new File(filename));

        width = Integer.parseInt(in.next());
        height = Integer.parseInt(in.next());
        pillars = new ArrayList<>();
        grid = new char[height][width];
        initalGrid = new char[height][width];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                char temp = in.next().charAt(0);
                grid[row][col] = temp;
                if ("01234LX".indexOf(grid[row][col]) != -1) {
                    pillars.add(new Coordinate(row, col));
                }
            }
        }
        in.close();
        lasers = new ArrayList<>();
        currentCol = 0;
        currentRow = 0;

    }

    /**
     * Another copy constructor, but instead of making a new instance simply updates the old one
     *
     * @param other the model to copy from
     */
    public void replaceModel(LasersModel other) {

        this.width = other.width;
        this.height = other.height;
        this.grid = new char[width][width];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                this.grid[row][col] = other.grid[row][col];
            }
        }
        this.initalGrid = new char[height][width];

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                this.initalGrid[row][col] = other.initalGrid[row][col];
            }
        }
        this.pillars = other.pillars;
        this.lasers = other.lasers;
        this.currentCol = other.currentCol;
        this.currentRow = other.currentRow;

    }
}
