package common;


import exception.NotFoundException;

public class DefaultGridSampler extends GridSampler {

    public BitMatrix sampleGrid(BitMatrix image, int dimensionX, int dimensionY,
                                float p1ToX, float p1ToY,
                                float p2ToX, float p2ToY,
                                float p3ToX, float p3ToY,
                                float p4ToX, float p4ToY,
                                float p1FromX, float p1FromY,
                                float p2FromX, float p2FromY,
                                float p3FromX, float p3FromY,
                                float p4FromX, float p4FromY,
                                boolean[][] matrix) throws NotFoundException {
        PerspectiveTransform transform = PerspectiveTransform.quadrilateralToQuadrilateral(
                p1ToX, p1ToY, p2ToX, p2ToY, p3ToX, p3ToY, p4ToX, p4ToY,
                p1FromX, p1FromY, p2FromX, p2FromY, p3FromX, p3FromY, p4FromX, p4FromY);

        return sampleGrid(image, dimensionX, dimensionY, transform, matrix);
    }

    public BitMatrix sampleGrid(BitMatrix image, int width, int height, PerspectiveTransform transform, boolean[][] matrix) throws NotFoundException {
        if(width <= 0 || height <= 0){
            throw NotFoundException.getNotFoundInstance();
        }

        BitMatrix bits = new BitMatrix(222, 222);
        float[] points = new float[2 * 222]; //
        int max = points.length;

        // 一行一行地读
        for(int y = 0;y < height;y++){
            float iValue = y + 0.05f;

            for(int x = 0;x < max;x += 2){
                points[x] = (float) (x / 2) + 0.1f;
                points[x + 1] = iValue;
            }
            transform.transformPoints(points);
            checkAndNudgePoints(image, points);
//            System.out.println("points : ");
//            for(float aa : points){
//                System.out.print(aa + " ");
//            }
//            System.out.println();
//            System.out.println((int)points[0] + " - " + (int)points[1]);
//            System.out.println(image.get((int)points[0], (int)points[1]));
//            System.out.println(image.get(8, 15));
//            System.out.println(image.get(9, 15));
//            System.out.println(image.get(10, 15));
            try {
                for(int x = 0;x < max;x += 2){
                    if(image.get((int) points[x], (int) points[x + 1])){
                        bits.set(x / 2, y);
                        matrix[y][x / 2] = true;
//                        System.out.println(bits.get(x / 2, y));
                    }
                }
            }catch (ArrayIndexOutOfBoundsException aioobe){
                System.out.println("Default grid sampler array index out of bounds");
                throw NotFoundException.getNotFoundInstance();
            }
        }
        return bits;
    }

}
