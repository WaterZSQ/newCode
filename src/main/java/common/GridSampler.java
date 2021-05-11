package common;

import exception.NotFoundException;

public abstract class GridSampler {
    private static GridSampler gridSampler = new DefaultGridSampler();

    public static void setGridSampler(GridSampler newGridSample){
        gridSampler = newGridSample;
    }

    public static GridSampler getInstance(){
        return gridSampler;
    }

    public abstract BitMatrix sampleGrid(BitMatrix image,
                                         int dimensionX,
                                         int dimensionY,
                                         float p1ToX, float p1ToY,
                                         float p2ToX, float p2ToY,
                                         float p3ToX, float p3ToY,
                                         float p4ToX, float p4ToY,
                                         float p1FromX, float p1FromY,
                                         float p2FromX, float p2FromY,
                                         float p3FromX, float p3FromY,
                                         float p4FromX, float p4FromY,
                                         boolean[][] matrix) throws NotFoundException;

    public abstract BitMatrix sampleGrid(BitMatrix image,
                                         int dimensionX,
                                         int dimensionY,
                                         PerspectiveTransform transform,
                                         boolean[][] matrix) throws NotFoundException;


    // 根据图像的尺寸检查一组已转换为采样点的点，查看该点是否在图像内
    protected static void checkAndNudgePoints(BitMatrix image, float[] points) throws NotFoundException {
        int width = image.getWidth();
        int height = image.getHeight();

        boolean nudged = true;
        int maxOffset = points.length - 1;
        for(int offset = 0;offset < maxOffset && nudged;offset += 2) {
            int x = (int) points[offset];
            int y = (int) points[offset + 1];
//            System.out.println("old :");
//            System.out.println(points[offset] + " - " + points[offset + 1]);
            if (x < -1 || x > width || y < -1 || y > height) {
//                System.out.println("check and Nudge point wrong 1 ");
                throw NotFoundException.getNotFoundInstance();
            }

            nudged = false;
            if (x == -1) {
                points[offset] = 0.0f;
                nudged = true;
            } else if (x == width) {
                points[offset] = width - 1;
                nudged = true;
            }

            if (y == -1) {
                points[offset + 1] = 0.0f;
                nudged = true;
            } else if (y == height) {
                points[offset + 1] = height - 1;
                nudged = true;
            }
//            System.out.println("new :");
//            System.out.println(points[offset] + " - " + points[offset + 1]);
        }
        nudged = true;

        for(int offset = points.length - 2;offset >= 0 && nudged; offset -= 2){
            int x = (int) points[offset];
            int y = (int) points[offset + 1];

            if (x < -1 || x > width || y < -1 || y > height) {
                throw NotFoundException.getNotFoundInstance();
            }

            nudged = false;
            if (x == -1) {
                points[offset] = 0.0f;
                nudged = true;
            } else if (x == width) {
                points[offset] = width - 1;
                nudged = true;
            }

            if (y == -1) {
                points[offset + 1] = 0.0f;
                nudged = true;
            } else if (y == height) {
                points[offset + 1] = height - 1;
                nudged = true;
            }
        }
    }

}
