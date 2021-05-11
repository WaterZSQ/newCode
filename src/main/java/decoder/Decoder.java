package decoder;

import common.BitMatrix;
import common.reedsolomon.GenericGF;
import common.reedsolomon.ReedSolomonDecoder;
import exception.ChecksumException;
import exception.FormatException;
import exception.ReedSolomonException;
import metadata.ErrorCorrectionLevel;
import metadata.Version;

public class Decoder {
    private final ReedSolomonDecoder rsDecoder;

    public Decoder(){
        rsDecoder = new ReedSolomonDecoder(GenericGF.CODE_FIELD_256);
    }

    public DecoderResult decode(boolean[][] image) throws FormatException, ChecksumException  {
        return decode(BitMatrix.parse(image));
    }

    public DecoderResult decode(BitMatrix bits) throws FormatException, ChecksumException {
        BitMatrixParser parser = new BitMatrixParser(bits);
        return decode(parser);
    }

    public DecoderResult decode(BitMatrixParser parser) throws FormatException, ChecksumException {
        Version version = Version.getVersionForNumber(1);

        byte[] codewords = parser.readCodewords();
        DataBlock dataBlock = DataBlock.getDataBlock(codewords, version, ErrorCorrectionLevel.Q);
        int numDataCodewords = dataBlock.getNumDataCodewords();
        byte[] resultBytes = new byte[numDataCodewords];

        int resultOffset = 0;

        byte[] codewordBytes = dataBlock.getCodewords();
        correctErrors(codewordBytes, numDataCodewords);

        for(int i = 0;i < numDataCodewords;i++){
            resultBytes[resultOffset++] = codewordBytes[i];
        }

        return DecodedBitStreamParser.decode(resultBytes);
    }

    private void correctErrors(byte[] codewordBytes, int numDataCodewords) throws ChecksumException{
        int numCodewords = codewordBytes.length;

        int[] codewordsInts = new int[numCodewords];
        for(int i = 0;i < numCodewords;i++){
            codewordsInts[i] = codewordBytes[i] & 0xff;
        }
        try{
            rsDecoder.decode(codewordsInts, codewordBytes.length - numDataCodewords);
        }catch (ReedSolomonException e){
            throw  ChecksumException.getChecksumInstance();
        }

        for(int i = 0;i < numDataCodewords;i++){
            codewordBytes[i] = (byte) codewordsInts[i];
        }
    }
}
