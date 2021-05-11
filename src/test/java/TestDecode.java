import decoder.Decoder;
import decoder.DecoderResult;
import encoder.ByteMatrix;
import encoder.Code;
import encoder.Encoder;
import exception.ChecksumException;
import exception.FormatException;
import exception.WriterException;
import metadata.ErrorCorrectionLevel;
import org.junit.Test;

public class TestDecode {


    @Test
    public void testDecode() throws WriterException, FormatException, ChecksumException {
        Code code = Encoder.encode("ab sl", ErrorCorrectionLevel.Q);
        ByteMatrix matrix = code.getMatrix();
        boolean[][] image = new boolean[matrix.getHeight()][matrix.getWidth()];

        for(int i = 0;i < matrix.getHeight();i++){
            for(int j = 0;j < matrix.getWidth();j++){
                if(matrix.get(i, j) == (byte) 1){
                    image[i][j] = true;
                }
            }
        }
//        System.out.println(code.toString());

        Decoder decoder = new Decoder();
        DecoderResult result = decoder.decode(image);

        System.out.println(result.toString());
    }



}
