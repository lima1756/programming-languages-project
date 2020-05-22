package producerconsumer;

public class Product {
    private char contents;
    private int rowIndex;

    public Product() {
    }

    public Product(char contents, int rowIndex) {
        this.contents = contents;
        this.rowIndex = rowIndex;
    }

    public char getContents() {
        return contents;
    }

    public void setContents(char contents) {
        this.contents = contents;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }
    
    
}
