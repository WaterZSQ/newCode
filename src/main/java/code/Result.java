package code;

import detector.code.ResultPoint;

public final class Result {
    private final String text;
    private final byte[] rawBytes;
    private final int numBits;
    private ResultPoint[] resultPoints;

    private final long timestamp;

    public Result(String text, byte[] rawBytes, ResultPoint[] resultPoints){
        this(text, rawBytes, resultPoints, System.currentTimeMillis());
    }
    public Result(String text, byte[] rawBytes, ResultPoint[] resultPoints, long timestamp){
        this(text, rawBytes, rawBytes == null ? 0 : 8 * rawBytes.length, resultPoints, System.currentTimeMillis());
    }

    public Result(String text, byte[] rawBytes, int numBits, ResultPoint[] resultPoints, long timestamp){
        this.text = text;
        this.rawBytes = rawBytes;
        this.numBits = numBits;
        this.resultPoints = resultPoints;
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public byte[] getRawBytes() {
        return rawBytes;
    }

    public int getNumBits() {
        return numBits;
    }

    public ResultPoint[] getResultPoints() {
        return resultPoints;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void addResultPoints(ResultPoint[] newPoints){
        ResultPoint[] oldPoints = resultPoints;
        if(oldPoints == null){
            resultPoints = newPoints;
        }else if(newPoints != null && newPoints.length > 0){
            ResultPoint[] allPoints = new ResultPoint[oldPoints.length + newPoints.length];
            System.arraycopy(oldPoints, 0, allPoints, 0, oldPoints.length);
            System.arraycopy(newPoints, 0, allPoints, oldPoints.length, newPoints.length);
            resultPoints = allPoints;
        }
    }

    public String toString(){
        return text;
    }
}
