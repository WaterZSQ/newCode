package common;

public class BitMatrix implements Cloneable{

    private int height;
    private int width;
    private int rowSize;
    private int[] bits;

    public BitMatrix(int dimension){
        this(dimension, dimension);
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getRowSize() {
        return rowSize;
    }

    public int[] getBits() {
        return bits;
    }

    public BitMatrix(int height, int width){
        if(height < 1 || width < 1){
            throw new IllegalArgumentException();
        }

        this.width = width;
        this.height = height;
        this.rowSize = (width + 31) / 32;

        bits = new int[rowSize * height];
    }

    private BitMatrix(int width, int height, int rowSize, int[] bits) {
        this.width = width;
        this.height = height;
        this.rowSize = rowSize;
        this.bits = bits;
    }

    public static BitMatrix parse(boolean[][] image){
        int height = image.length;
        int width = image[0].length;
        BitMatrix bits = new BitMatrix(height, width);
        for(int i = 0;i < height;i++){
            for(int j = 0;j < width;j++){
                if(image[i][j]){
                    bits.set(i, j);
                }
            }
        }
        return bits;
    }

    public void set(int x, int y){
        int offset = y * rowSize + (x / 32);
        if(offset < bits.length){
            bits[offset] |= 1 << (x & 0x1f);
        }
    }

    public boolean get(int x, int y){
        int offset = y * rowSize + (x / 32);
        return offset < bits.length && ((bits[offset] >>> (x & 0x1f)) & 1) != 0;
    }


    public void setRegion(int left, int top, int width, int height){
        if (top < 0 || left < 0) {
            throw new IllegalArgumentException("Left and top must be nonnegative");
        }
        if (height < 1 || width < 1) {
            throw new IllegalArgumentException("Height and width must be at least 1");
        }
        int right = left + width;
        int bottom = top + height;
        if (bottom > this.height || right > this.width) {
            throw new IllegalArgumentException("The region must fit inside the matrix");
        }
        for (int y = top; y < bottom; y++) {
            int offset = y * rowSize;
            for (int x = left; x < right; x++) {
                bits[offset + (x / 32)] |= 1 << (x & 0x1f);
            }
        }
    }
}
