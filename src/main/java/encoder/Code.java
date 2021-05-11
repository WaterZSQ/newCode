package encoder;

import metadata.ErrorCorrectionLevel;
import metadata.Mode;
import metadata.Version;

public class Code {
    private Mode mode;
    private ErrorCorrectionLevel ecLevel;
    private Version version;
    private ByteMatrix matrix;
    private int sparseNum;

    public int getSparseNum() {
        return sparseNum;
    }

    public void setSparseNum(int sparseNum) {
        this.sparseNum = sparseNum;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public ErrorCorrectionLevel getEcLevel() {
        return ecLevel;
    }

    public void setEcLevel(ErrorCorrectionLevel ecLevel) {
        this.ecLevel = ecLevel;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public ByteMatrix getMatrix() {
        return matrix;
    }

    public void setMatrix(ByteMatrix matrix) {
        this.matrix = matrix;
    }

    public void translate(Code code){
        code.setVersion(this.version);
        code.setEcLevel(this.ecLevel);
        code.setMode(this.mode);
        code.setMatrix(this.matrix);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(200);
        result.append("<<\n");
        result.append(" mode: ");
        result.append(mode);
        result.append("\n ecLevel: ");
        result.append(ecLevel);
        result.append("\n version: ");
        result.append(version.toString());
        if (matrix == null) {
            result.append("\n matrix: null\n");
        } else {
            result.append("\n matrix:\n");
            result.append(matrix);
        }
        result.append(">>\n");
        return result.toString();
    }

    public boolean equal(Code code){
        ByteMatrix matrix1 = this.matrix;
        ByteMatrix matrix2 = code.getMatrix();
        if(matrix1.getWidth() != matrix2.getWidth() || matrix1.getHeight() != matrix2.getHeight()){
            return false;
        }
        for(int i = 0;i < matrix1.getWidth();i++){
            for(int j = 0;j < matrix1.getWidth();j++){
                if(matrix1.get(i, j) != matrix2.get(i, j)){
                    System.out.println(matrix1.get(i, j));
                    System.out.println(matrix2.get(i, j));
                    System.out.println(i + ", " + j);
                    return false;
                }
            }
        }
        return true;
    }
}
