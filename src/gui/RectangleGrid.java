package gui;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

/**
 * The RectangleGrid class extends rectangle and is responsible for holding a Row and Column where the rectangle is
 * located. It also holds an integer representing the color
 */
class RectangleGrid extends Rectangle {
    private int row;
    private int col;
    private int color = 0;

    RectangleGrid(double width, double height, Paint fill, int row, int col) {
        super(width, height, fill);
        this.row = row;
        this.col = col;
    }

    /**
     * Gets the row
     *
     * @return the row int
     */
    int getRow() {
        return this.row;
    }

    /**
     * Gets the col
     *
     * @return the col int
     */
    int getCol() {
        return this.col;
    }
}