package detector;

import detector.code.ResultPoint;

public final class FinderPattern extends ResultPoint {

    private final float estimateModuleSize;
    private final int count;

    FinderPattern(float posX, float posY, float estimateModuleSize){
        this(posX, posY, estimateModuleSize, 1);
    }

    private FinderPattern(float posX, float posY, float estimateModuleSize, int count){
        super(posX, posY);
        this.estimateModuleSize = estimateModuleSize;
        this.count = count;
    }


    public boolean aboutEquals(float moduleSize, float i, float j){
        if(Math.abs(i - getX()) <= moduleSize && Math.abs(j - getY()) <= moduleSize){
            float moduleSizeDiff = Math.abs(moduleSize - estimateModuleSize);
            return moduleSizeDiff <= 1.0f || moduleSizeDiff <= estimateModuleSize;
        }
        return false;
    }

//    FinderPattern combineEstimate(float i, float j, float newModuleSize){
//        int combinedCount = count +1;
//        float
//    }
}
