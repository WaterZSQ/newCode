package decoder;

import metadata.ErrorCorrectionLevel;
import metadata.Version;

public final class DataBlock {
    private final int numDataCodewords; // 数据码字数量
    private final byte[] codewords; // 信息码字

    private DataBlock(int numDataCodewords, byte[] codewords){
        this.numDataCodewords = numDataCodewords;
        this.codewords = codewords;
    }

    public int getNumDataCodewords() {
        return numDataCodewords;
    }

    public byte[] getCodewords() {
        return codewords;
    }

    public static DataBlock getDataBlock(byte[] rawCodewords, Version version, ErrorCorrectionLevel ecLevel){
        if(rawCodewords.length != version.getTotalCodewords()){
            throw new IllegalArgumentException();
        }

        int numDataCodewords = version.getCodeword()[ecLevel.getBits()][0];

        int numBlockCodewords = version.getTotalCodewords();

        DataBlock result = new DataBlock(numDataCodewords, new byte[numBlockCodewords]);

        System.arraycopy(rawCodewords, 0, result.getCodewords(), 0, rawCodewords.length);
        return result;
    }

}
