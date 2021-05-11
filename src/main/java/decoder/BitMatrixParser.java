package decoder;

import common.BitMatrix;
import exception.FormatException;
import exception.NotFoundException;
import metadata.ErrorCorrectionLevel;
import metadata.Version;

public class BitMatrixParser {

    private final BitMatrix bitMatrix;
//    private FormatInformation formatInformation;

    BitMatrixParser(BitMatrix bitMatrix) throws FormatException {
        int dimension = bitMatrix.getHeight();
        if(dimension < 74){
            throw FormatException.getFormatInstance();
        }
        this.bitMatrix = bitMatrix;
    }

    public BitMatrix getBitMatrix() {
        return bitMatrix;
    }

    public byte[] readCodewords() throws FormatException{
        int dimension = bitMatrix.getHeight();

        byte[] result = new byte[Version.totalCodewords];

        int resultOffset = 0;
        int currentByte = 0;
        int bitsRead = 0;

        for(int i = 1;i < dimension - 2; i += 2){
            for(int j = 3;j < dimension;j += 3){
                bitsRead++;
                currentByte <<= 1;

                if(bitMatrix.get(i, j + 1)){
                    currentByte |= 1;
                }

                if(bitsRead == 8){
                    result[resultOffset++] = (byte) currentByte;
                    bitsRead = 0;
                    currentByte = 0;
                }
            }
        }

        if(resultOffset != Version.totalCodewords){
            throw  FormatException.getFormatInstance();
        }

        return result;
    }
}
