package code;

import decoder.Decoder;
import decoder.DecoderResult;
import detector.Detector;
import detector.code.DetectorResult;
import detector.code.ResultPoint;
import exception.ChecksumException;
import exception.FormatException;
import exception.NotFoundException;
import image.BinaryBitmap;

public class CodeReader implements Reader {
    private final Decoder decoder = new Decoder();

    public Result decode(BinaryBitmap image) throws NotFoundException, FormatException, ChecksumException {
        DecoderResult decoderResult;
        ResultPoint[] points;

        DetectorResult detectorResult = new Detector(image.getBlackMatrix()).detect();

//        for(int i = 0;i < ){
//
//        }
        boolean[][] matrix = detectorResult.getMatrix();
        for(int i = 0;i < matrix.length;i++){
            for(int j = 0;j < matrix[0].length;j++){
                if(matrix[i][j]){
                    System.out.print("1 ");
                }else{
                    System.out.print("0 ");
                }
            }
            System.out.println();
        }


//        decoderResult = decoder.decode(detectorResult.getBits());
//        points = detectorResult.getPoints();

//        Result result = new Result(decoderResult.getText(), decoderResult.getRawBytes(), points);
//        System.out.println(result.toString());
//        return result;
        return null;
    }

    @Override
    public void reset() {
        // do nothing
    }
}
