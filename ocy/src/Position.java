public class Position {
    private int idx;
    private int ln;
    private int col;

    public int getIdx() {
        return idx;
    }

    public void setIdx(int num) {
        this.idx = num;
    }

    public int getLn() {
        return ln;
    }

    public void setLn(int num) {
        this.ln = num;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int num) {
        this.col = num;
    }

    public void advance(String currentChar) {
        this.idx++;
        this.col++;

        if(currentChar != null && currentChar.equals("\n")) {
            this.ln++;
            this.col = 0;
        }
    }

    public Position copy() {
        Position pos =  new Position(this.idx, this.ln, this.col);
        return pos;
    }

    public Position(int idx, int ln, int col) {
        this.idx = idx;
        this.ln = ln;
        this.col = col;
    }
}
