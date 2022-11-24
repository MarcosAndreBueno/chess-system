package GUI;

import static java.lang.Character.toLowerCase;

public class GUIPosition {

    private int row;
    private char columnChar;
    private int columnInt;

    public GUIPosition(int column, int row) {
        this.row = row;
        this.columnInt = column;
    }

    // convert chess position to matrix position
    public GUIPosition toMatrixPosition() {
        this.row = 8 - row;
        this.columnChar = (toLowerCase((char)(columnInt+65)));
        return this;
    }

    public int getRow() {
        return row;
    }

    public char getColumnChar() {
        return columnChar;
    }

    public int getColumnInt() {
        return columnInt;
    }
}
