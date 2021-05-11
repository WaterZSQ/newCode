package detector.code;

import common.BitMatrix;
import common.MathUtils;
import exception.NotFoundException;

public final class CodeDetector {
    private static final int INIT_SIZE = 10;
    // 调整精度
    private static final float CORR = 0.005f;

    private final BitMatrix image;
    private final int height;
    private final int width;
    private final int leftInit;
    private final int rightInit;
    private final int downInit;
    private final int upInit;

    public CodeDetector(BitMatrix image) throws NotFoundException {
        this(image, INIT_SIZE, image.getHeight() / 2, image.getWidth() / 2);
    }

    public CodeDetector(BitMatrix image, int initSize, int x, int y) throws NotFoundException {
        this.image = image;
//        System.out.println("image : ");
//        for(int i : image.getBits()){
//            System.out.print(i + " ");
//        }
//        System.out.println();

        height = image.getHeight();
        width = image.getWidth();


//        System.out.println(height);
//        System.out.println(width);


//        int halfSize = initSize / 2;
//
//        upInit = x - halfSize;
//        downInit = x + halfSize;
//        leftInit = y - halfSize;
//        rightInit = y + halfSize;

        upInit = 0;
        downInit = height - 1;
        leftInit = 0;
        rightInit = width - 1;

//        if(upInit < 0 || leftInit < 0 || downInit >= height || rightInit >= width){
//            throw NotFoundException.getNotFoundInstance();
//        }
    }

//    public ResultPoint[] detect1(){
//        int left = 0;
//        int up = 0;
//        int right = 0;
//
////        for(){
////
////        }
//    }

    public ResultPoint[] detect() throws NotFoundException {
        int left = leftInit;
        int right = rightInit;
        int up = upInit;
        int down = downInit;

        boolean sizeExceeded = false;
        boolean aBlackPointFoundOnBorder = false;


        boolean atLeastOneBlackPointFoundOnRight = true;
        boolean atLeastOneBlackPointFoundOnBottom = true;
        boolean atLeastOneBlackPointFoundOnLeft = true;
        boolean atLeastOneBlackPointFoundOnTop = true;

        while(!aBlackPointFoundOnBorder){
            aBlackPointFoundOnBorder = true;

            // 右边界
            boolean rightBorderContainsBlack = false;
            while((!rightBorderContainsBlack && atLeastOneBlackPointFoundOnRight) && right >= 0){
                rightBorderContainsBlack = containsBlackPoint(up, down, right, false);

                if(!rightBorderContainsBlack){// 边界没有找到黑色块
                    right--;
                    aBlackPointFoundOnBorder = false;
                    atLeastOneBlackPointFoundOnRight = false;
                }
                else if(atLeastOneBlackPointFoundOnRight){
                    right--;
                }
            }

            if(right < 0){
                sizeExceeded = true;
                break;
            }


            boolean bottomBorderContainsBlack = false;
            while((!bottomBorderContainsBlack && atLeastOneBlackPointFoundOnBottom) && down >= 0){
                bottomBorderContainsBlack = containsBlackPoint(left, right, down, true);

                if(!bottomBorderContainsBlack){
                    down--;
                    aBlackPointFoundOnBorder = false;
                    atLeastOneBlackPointFoundOnBottom = false;
                }
                else if(atLeastOneBlackPointFoundOnBottom){
                    down--;
                }
            }
            if(down < 0){
                sizeExceeded = true;
                break;
            }

            boolean leftBorderContainsBlack = false;
            while((!leftBorderContainsBlack && atLeastOneBlackPointFoundOnLeft) && left < width){
                leftBorderContainsBlack = containsBlackPoint(up, down, left, false);
                if(!leftBorderContainsBlack){
                    left++;
                    aBlackPointFoundOnBorder = false;
                    atLeastOneBlackPointFoundOnLeft = false;
                }
                else if(atLeastOneBlackPointFoundOnLeft){
                    left++;
                }
            }

            if(left >= width){
                sizeExceeded = true;
                break;
            }

            boolean topBorderContainsBlack = false;
            while((!topBorderContainsBlack && atLeastOneBlackPointFoundOnTop) && up < height){
                topBorderContainsBlack = containsBlackPoint(left, right, up, true);
                if(topBorderContainsBlack){
                    up++;
                    aBlackPointFoundOnBorder = false;
                    atLeastOneBlackPointFoundOnTop = false;
                }
                else if(atLeastOneBlackPointFoundOnTop){
                    up++;
                }
            }

            if(up >= height){
                sizeExceeded = true;
                break;
            }
        }

        if(!sizeExceeded){
            int maxSize = right - left;

            // z : left most point
            ResultPoint z = null;

            for(int i = 1;z == null && i < maxSize;i++){
                z = getBlackPointOnSegment(left, down - i, left + i, down);
            }

            if(z == null){
                throw NotFoundException.getNotFoundInstance();
            }

            // t : top most point
            ResultPoint t = null;
            for(int i = 1;t == null && i < maxSize;i++){
                t = getBlackPointOnSegment(left, up + i, left + i, up);
            }

            if(t == null){
                throw NotFoundException.getNotFoundInstance();
            }


            // x : right most point
            ResultPoint x = null;
            for(int i = 1;x == null && i < maxSize;i++){
                x = getBlackPointOnSegment(right, up + i, right - i, up);
            }

            if(x == null){
                throw NotFoundException.getNotFoundInstance();
            }

            // y : bottom most point
            ResultPoint y = null;
            for(int i = 1;y == null && i < maxSize;i++){
                y = getBlackPointOnSegment(right, down - i, right - i, down);
            }
            if(y == null){
                throw NotFoundException.getNotFoundInstance();
            }
            return centerEdges(y, z, x, t);
        }else{
            throw NotFoundException.getNotFoundInstance();
        }
    }


    private ResultPoint[] centerEdges(ResultPoint y, ResultPoint z, ResultPoint x, ResultPoint t){
        //
        //       t            t
        //  z                      x
        //        x    OR    z
        //   y                    y
        //


        float yi = y.getX();
        float yj = y.getY();

        float zi = z.getX();
        float zj = z.getY();
        float xi = x.getX();
        float xj = x.getY();
        float ti = t.getX();
        float tj = t.getY();

        if(yi - width < 2.0f){
//            System.out.println("!!!");
            return new ResultPoint[]{
                    new ResultPoint(ti - CORR, tj + CORR),
                    new ResultPoint(zi + CORR, zj + CORR),
                    new ResultPoint(xi - CORR, xj - CORR),
                    new ResultPoint(yi + CORR, yj - CORR),
            };
        }else{
//            System.out.println("??");
            return new ResultPoint[]{
                    new ResultPoint(ti + CORR, tj + CORR),
                    new ResultPoint(zi + CORR, zj - CORR),
                    new ResultPoint(xi - CORR, xj + CORR),
                    new ResultPoint(yi - CORR, yj - CORR),
            };
        }
    }

    private ResultPoint getBlackPointOnSegment(float aX, float aY, float bX, float bY){
        int dist = MathUtils.round(MathUtils.distance(aX, aY, bX, bY)); // 两点之间的距离

        float xStep = (bX - aX) / dist;
        float yStep = (bY - aY) / dist;

        for(int i = 0;i < dist;i++){
            int x = MathUtils.round(aX + i * xStep);
            int y = MathUtils.round(aY + i * yStep);

            if(image.get(x, y)){
                return new ResultPoint(x, y);
            }
        }
        return null;
    }


    // a
    //
    private boolean containsBlackPoint(int a, int b, int fixed, boolean horizontal){
        if(horizontal){ // 水平
            for(int x = a; x <= b; x++){
                if(image.get(x, fixed)){
                    return true;
                }
            }
        }else{ // 垂直
            for(int y = a;y <= b;y++){
                if(image.get(fixed, y)){
                    return true;
                }
            }
        }

        return false;
    }
}
