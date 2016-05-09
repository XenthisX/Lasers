package model;
/**
 * Created by stefan on 5/9/16.
 */
public class Coordinate {
    private int Row;
    private int Col;


    public Coordinate(int row, int col){
        Row=row;
        Col=col;
    }

    public int getRow() {
        return Row;
    }

    public int getCol() {
        return Col;
    }

    public void setRow(int row) {
        Row = row;
    }

    public void setCol(int col) {
        Col = col;
    }

    @Override
    public boolean equals(Object obj) {

        if(obj == null) return false;
        else if (!(obj instanceof Coordinate)) return false;
        else {
            return (Row == ((Coordinate)obj).getRow()) && (Col == ((Coordinate)obj).getCol());
        }
    }
}

