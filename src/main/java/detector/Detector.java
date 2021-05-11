package detector;

import common.BitMatrix;
import common.GridSampler;
import detector.code.CodeDetector;
import detector.code.DetectorResult;
import detector.code.ResultPoint;
import exception.NotFoundException;

public class Detector {
    private final BitMatrix image;
    private final CodeDetector codeDetector;


    public Detector(BitMatrix image) throws NotFoundException {
        this.image = image;
        codeDetector = new CodeDetector(image);
    }

    public BitMatrix getImage() {
        return image;
    }


    public DetectorResult detect() throws NotFoundException {
        // 0 最高
        // 1 最左边
        // 2 最右边
        // 3 最低
        ResultPoint[] cornerPoints = codeDetector.detect();

        ResultPoint[] points = detectSolid1(cornerPoints);
//        System.out.println("detect solid 1");
        int ss = 0;
//        for(ResultPoint point : points){
//            System.out.println(ss + " : " + point.toString());
//            ss++;
//        }
//        System.out.println();
//        detectSolid2(points);
//
//        System.out.println("detect solid 2");
//
//        ss = 0;
//        for(ResultPoint point : points){
//            System.out.println(ss + " : " + point.toString());
//            ss++;
//        }

//        points[3] = correctTopRight(points);

        points = shiftToModuleCenter(points);

//        System.out.println("shift to module center");
//        ss = 0;
//        for(ResultPoint point : points){
//            System.out.println(ss + " : " + point.toString());
//            ss++;
//        }


        ResultPoint topLeft = points[0];
        ResultPoint bottomLeft = points[1];
        ResultPoint bottomRight = points[2];
        ResultPoint topRight = points[3];


        int dimensionLeft = transitionBetween(topLeft, bottomLeft) + 1;
        int dimensionBottom = transitionBetween(bottomRight, bottomLeft) + 1;
//        System.out.println("dimension left : " + dimensionLeft);
//        System.out.println("dimension bottom : " + dimensionBottom);
        boolean[][] matrix = new boolean[222][222];
        BitMatrix bits = sampleGrid(image, topLeft, bottomLeft, bottomRight, topRight, 222, 222, matrix);
        return new DetectorResult(bits, new ResultPoint[]{topLeft, bottomLeft, bottomRight, topRight}, matrix);
    }


    private static BitMatrix sampleGrid(BitMatrix image, ResultPoint topLeft, ResultPoint bottomLeft, ResultPoint bottomRight, ResultPoint topRight, int dimensionX, int dimensionY, boolean[][] matrix) throws NotFoundException {
        GridSampler sampler = GridSampler.getInstance();

        return sampler.sampleGrid(image,
                dimensionX,
                dimensionY,
                0.05f,
                0.05f,
                dimensionX - 0.05f,
                0.05f,
                dimensionX - 0.05f,
                dimensionY - 0.05f,
                0.05f,
                dimensionY - 0.05f,
                topLeft.getX(),
                topLeft.getY(),
                topRight.getX(),
                topRight.getY(),
                bottomRight.getX(),
                bottomRight.getY(),
                bottomLeft.getX(),
                bottomLeft.getY(),
                matrix);
    }


    private ResultPoint[] shiftToModuleCenter(ResultPoint[] points){
        ResultPoint pointA = points[0];
        ResultPoint pointB = points[1];
        ResultPoint pointC = points[2];
        ResultPoint pointD = points[3];


        // 计算伪尺寸
        int dimH = transitionBetween(pointA, pointD) + 1;
        int dimV = transitionBetween(pointC, pointD) + 1;

        ResultPoint pointAs = shiftPoint(pointA, pointB, dimV * 4);
        ResultPoint pointCs = shiftPoint(pointC, pointB, dimH * 4);

        dimH = transitionBetween(pointAs, pointD) + 1;
        dimV = transitionBetween(pointCs, pointD) + 1;
        if((dimH & 0x01) == 1){
            dimH++;
        }
        if((dimV & 0x01) == 1){
            dimV++;
        }

        float centerX = (pointA.getX() + pointB.getX() + pointC.getX() + pointD.getX()) / 4;
        float centerY = (pointA.getY() + pointB.getY() + pointC.getY() + pointD.getY()) / 4;

        pointA = moveAway(pointA, centerX, centerY);
        pointB = moveAway(pointB, centerX, centerY);
        pointC = moveAway(pointC, centerX, centerY);
        pointD = moveAway(pointD, centerX, centerY);


        ResultPoint pointBs;
        ResultPoint pointDs;
        pointAs = shiftPoint(pointA, pointB, dimV * 4);
        pointAs = shiftPoint(pointAs, pointD, dimH * 4);
        pointBs = shiftPoint(pointB, pointA, dimV * 4);
        pointBs = shiftPoint(pointBs, pointC, dimH * 4);
        pointCs = shiftPoint(pointC, pointD, dimV * 4);
        pointCs = shiftPoint(pointCs, pointB, dimH * 4);
        pointDs = shiftPoint(pointD, pointC, dimV * 4);
        pointDs = shiftPoint(pointDs, pointA, dimH * 4);
        return new ResultPoint[]{pointAs, pointBs, pointCs, pointDs};
    }

    private static ResultPoint moveAway(ResultPoint point, float fromX, float fromY){
        float x = point.getX();
        float y = point.getY();

        if(x < fromX){
            x--;
        }else{
            x++;
        }

        if(y < fromY){
            y--;
        }else{
            y++;
        }

        return new ResultPoint(x, y);
    }


    private ResultPoint[] detectSolid1(ResultPoint[] cornerPoints){
        // 0  2
        // 1  3
        ResultPoint pointA = cornerPoints[0];// 最高
        ResultPoint pointB = cornerPoints[1];// 最左
        ResultPoint pointC = cornerPoints[2];// 最右
        ResultPoint pointD = cornerPoints[3];// 最低

        int trAB = transitionBetween(pointA, pointB);
        int trBC = transitionBetween(pointB, pointC);
        int trCD = transitionBetween(pointC, pointD);
        int trDA = transitionBetween(pointD, pointA);

        // 0..3
        // :  :
        // 1--2
//        int max = trAB;
        ResultPoint[] points = {pointA, pointB, pointD, pointC};
//        if(max < trBC){
//            max = trBC;
//            System.out.println("1");
//            points[0] = pointA;
//            points[1] = pointB;
//            points[2] = pointC;
//            points[3] = pointD;
//        }

//        if(max < trCD){
//            max = trCD;
//            System.out.println("2");
//
//            points[0] = pointB;
//            points[1] = pointC;
//            points[2] = pointD;
//            points[3] = pointA;
//        }

//        if(max < trDA){
//            System.out.println("3");
//
//            points[0] = pointC;
//            points[1] = pointD;
//            points[2] = pointA;
//            points[3] = pointB;
//        }
        return points;
    }

    private void detectSolid2(ResultPoint[] points){
        // A..D
        // :  :
        // B--C
        ResultPoint pointA = points[0];
        ResultPoint pointB = points[1];
        ResultPoint pointC = points[2];
        ResultPoint pointD = points[3];

        int tr = transitionBetween(pointA, pointD);

        ResultPoint pointBs = shiftPoint(pointB, pointC, (tr + 1) * 4);
        ResultPoint pointCs = shiftPoint(pointC, pointB, (tr + 1) * 4);

        int trBA = transitionBetween(pointBs, pointA);
        int trCD = transitionBetween(pointCs, pointD);

        System.out.println("BA transition : " + trBA);
        System.out.println("CD transition : " + trCD);


        // 0..3
        // |  :
        // 1--2
        if (trBA < trCD) {
            // solid sides: A-B-C
            points[0] = pointA;
            points[1] = pointB;
            points[2] = pointC;
            points[3] = pointD;
        }else{
            // solid sides: B-C-D
            points[0] = pointC;
            points[1] = pointD;
            points[2] = pointA;
            points[3] = pointB;
        }
    }

    // ???
    private static ResultPoint shiftPoint(ResultPoint point, ResultPoint to, int div){
        float x = (to.getX() - point.getX()) / (div + 1);
        float y = (to.getY() - point.getY()) / (div + 1);

        return new ResultPoint(point.getX() + x, point.getY() + y);
    }

    // 两点形成的直线上的黑白色块的转换次数
    private int transitionBetween(ResultPoint from, ResultPoint to){
//        System.out.println("from : " + from.toString());
//        System.out.println("to : " + to.toString());
        int fromX = (int) from.getX();
        int fromY = (int) from.getY();
        int toX = (int) to.getX();
        int toY = (int) to.getY();

        boolean steep = Math.abs(toY - fromY) > Math.abs(toX - fromX);

        if(steep){
            int temp = fromX;
            fromX = fromY;
            fromY = temp;

            temp = toX;
            toX = toY;
            toY = temp;
        }

        int dx = Math.abs(toX - fromX);
        int dy = Math.abs(toY - fromY);
        int error = -dx / 2;

        int yStep = fromY < toY ? 1 : -1;
        int xStep = fromX < toX ? 1 : -1;

        int transitions = 0;

        boolean inBlack = image.get(steep ? fromY : fromX, steep ? fromX : fromY);

        for(int x = fromX, y = fromY;x != toX;x += xStep){
            boolean isBlack = image.get(steep ? y : x, steep ? x : y);

            if(isBlack != inBlack){
                transitions++;
                inBlack = isBlack;
            }
            error += dy;
            if(error > 0){
                if(y == toY){
                    break;
                }
                y += yStep;
                error -= dx;
            }
        }
        return transitions;
    }

}
