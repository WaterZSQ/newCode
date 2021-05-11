package encoder;

import common.BitArray;
import exception.WriterException;
import metadata.ErrorCorrectionLevel;
import metadata.Version;

public final class MatrixUtil {

    private static void clearMatrix(ByteMatrix matrix){
        matrix.clear((byte) 0);
    }

    public static boolean isValid(){
        return false;
    }

    public static void buildMatrix(BitArray dataBits, ErrorCorrectionLevel ecLevel, Version version, ByteMatrix matrix) throws WriterException {
        clearMatrix(matrix);

        embedPositionDetectionPattern(matrix);

        embedDataBits(dataBits, matrix);
    }

    public static char[] int2charArray(int num){
        char[] arr = new char[4];
        int i = 3;
        while(i >= 0){
            arr[i] = (char) (num % 2 + '0');
            num >>>= 1;
            i--;
        }

        return arr;
    }

    private static void embedPositionDetectionPattern(ByteMatrix matrix){
        int beginRow = 1;
        int beginCol = 1;
        int length = matrix.getHeight();
        int endRow = matrix.getHeight() - 1;

        for(int i = 0;i + beginRow < length;i += 2){
            matrix.set(beginRow + i, beginCol, 1);
            matrix.set(endRow, beginCol + i, 1);
        }
    }

    private static void embedDataBits(BitArray dataBits, ByteMatrix matrix) throws WriterException {
        int bitIndex = 0;
        for(int row = 1;row < matrix.getHeight() - 2;row += 2){
            for(int col = 2;col < matrix.getWidth();col += 3){
                boolean bit;
                if (bitIndex < dataBits.getSize()) {
                    bit = dataBits.get(bitIndex);
                    ++bitIndex;
                } else {
                    bit = false;
                }

                if(bit){
                    matrix.set(row, col + 2, 1);
                }else{
                    matrix.set(row, col + 1, 1);
                }
            }
        }

        if (bitIndex != dataBits.getSize()) {
            throw new WriterException("Not all bits consumed: " + bitIndex + '/' + dataBits.getSize());
        }
    }
}
