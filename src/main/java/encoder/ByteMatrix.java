package encoder;

import java.util.Arrays;

public class ByteMatrix {
    private final byte[][] bytes;

    private final int height;
    private final int width;

    public ByteMatrix(int height, int width){
        this.height = height;
        this.width = width;
        bytes = new byte[height][width];
    }


    public byte get(int x, int y){
        return bytes[x][y];
    }

    public boolean[][] getBooleanArr(){
        boolean[][] result = new boolean[height][width];

        for(int i = 0;i < height;i++){
            for(int j = 0;j < width;j++){
                if(bytes[i][j] == (byte) 1){
                    result[i][j] = true;
                }
            }
        }
        return result;
    }

    public byte[][] getBytes() {
        return bytes;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public byte[][] getArray(){
        return bytes;
    }

    public void set(int x, int y, byte value){
        bytes[x][y] = value;
    }

    public void set(int x, int y, int value) {
        bytes[x][y] = (byte) value;
    }

    public void set(int x, int y, boolean value) {
        bytes[x][y] = (byte) (value ? 1 : 0);
    }

    public void clear(byte value){
        for(byte[] row : bytes){
            Arrays.fill(row, value);
        }
    }

    public String toString(){
        System.out.println("width : " + width + "， height : " + height);
        StringBuilder result = new StringBuilder(2 * width * height + 2);
        for(int x = 0; x < height;x++){
            for(int y = 0;y < width;y++){
                switch (bytes[x][y]){
                    case 0:
                        result.append(" 0");
                        break;
                    case 1:
                        result.append(" 1");
                        break;
                    default:
                        result.append(" 0");
                }
            }
            result.append("\n");
        }
        return result.toString();
    }
}
