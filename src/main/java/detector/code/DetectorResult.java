package detector.code;

import common.BitMatrix;

public class DetectorResult {
    private final BitMatrix bits;
    private final ResultPoint[] points;
    private final boolean[][] matrix;

    public DetectorResult(BitMatrix bits, ResultPoint[] points, boolean[][] matrix){
        this.bits = bits;
        this.points = points;
        this.matrix = matrix;
    }

    public BitMatrix getBits() {
        return bits;
    }

    public ResultPoint[] getPoints() {
        return points;
    }

    public boolean[][] getMatrix(){
        return matrix;
    }
}
