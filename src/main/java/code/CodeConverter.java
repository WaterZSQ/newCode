package code;

import encoder.ByteMatrix;
import encoder.Code;

import java.util.Random;

public class CodeConverter {

    // 将原先的code放大，变得更为稀疏。可以选择不同的稀疏程度
    public static Code converter(Code code, int sparseNum){
        int newWidth = code.getMatrix().getWidth() * sparseNum;
        int newHeight = code.getMatrix().getHeight() * sparseNum;

        Code newCode = new Code();

        ByteMatrix newMatrix = new ByteMatrix(newHeight, newWidth);
        ByteMatrix matrix = code.getMatrix();
        newMatrix.set(0, 0, 1);
        newMatrix.set(newHeight - 1, 0, 1);
        newMatrix.set(0, newWidth - 1, 1);

        for(int i = 1;i < matrix.getHeight();i++){
            for(int j = 1;j < matrix.getWidth();j++){
                if(matrix.get(i, j) == (byte) 1){ // 说明有信息点
//                    int rand = (int) (Math.random() * (sparseNum * sparseNum + 1)); // 获取0 - 9的随机数
//                    int xPlus = rand / sparseNum;
//                    int yPlus = rand % sparseNum;
//                    if(yPlus == 0 && xPlus != 0){
//                        xPlus--;
//                    }
//                    System.out.println("old point : (" + i + "," + j + "); rand : " + rand);
//                    newMatrix.set(i * sparseNum + xPlus, j * sparseNum + yPlus, 1);
                    newMatrix.set(i * sparseNum + 1, j * sparseNum + 1, 1);
                }
            }
        }
        code.translate(newCode);

        newCode.setMatrix(newMatrix);
//        System.out.println(newMatrix.toString());
        return newCode;
    }


    public static Code converter(Code code, int sparseNum, int numberV, int numberH){
        int newWidth = code.getMatrix().getWidth() * sparseNum * numberH;
        int newHeight = code.getMatrix().getHeight() * sparseNum * numberV;

//        System.out.println("width : " + newWidth);
//        System.out.println("height : " + newHeight);

        Code temp = converter(code, 3);

        boolean[][] image = temp.getMatrix().getBooleanArr();
//        image[0][0] = true;
        image[0][image[0].length - 1] = false;
        image[image.length - 1][0] = false;
//        System.out.println(temp.toString());

        Code newCode = new Code();
        ByteMatrix newMatrix = new ByteMatrix(newHeight, newWidth);

//        for(int i = 0;i < ){
//
//        }
        newMatrix.set(0, 0, 1);
        newMatrix.set(newHeight - 1, 0, 1);
        newMatrix.set(0, newWidth - 1, 1);


        for(int i = 0;i < numberV;i++){
            for(int j = 0;j < numberH;j++){
                for(int m = 0;m < image.length;m++){
                    for(int n = 0;n < image[0].length;n++){
                        if(image[m][n]){
                            newMatrix.set(image.length * i + m, image[0].length * j + n, 1);
                        }
                    }
                }
            }
        }

        code.translate(newCode);
        newCode.setMatrix(newMatrix);
//        System.out.println(newMatrix.toString());
        return newCode;
    }

    public static boolean[][] converter(boolean[][] image){
        int height = image.length;
        int width = image[0].length;
        boolean[][] result = null;
        if(height == width && height == 222){
            result = decodeOneMatrix(image);
        }else{
            result = decodeMulMatrix(image);
        }

        return result;
    }

    private static boolean[][] decodeMulMatrix(boolean[][] image){
        int height = image.length;
        int width = image[0].length;

        int numberV = height / 222; // 垂直方向上有多少个矩阵
        int numberH = width / 222; // 水平方向上有多少个矩阵

        boolean[][] newImage = new boolean[222][222];

        for(int i = 0;i < numberV;i++){
            for(int j = 0;j < numberH;j++){
                for(int m = 0;m < 222;m++){
                    for(int n = 0;n < 222;n++){
                        if (image[i * 222 + m][j * 222 + n]) {
                            newImage[i][j] = true;
                        }
                    }
                }
            }
        }
        return newImage;
    }

    private static boolean[][] decodeOneMatrix(boolean[][] image){
        int width = image[0].length;
        int begin = (width - 74 * 3) / 2;
        int end = begin + 74 * 3;
        boolean[][] newImage = new boolean[74][74];

        for(int i = begin + 1;i < end - 1; i += 3){
            for(int j = begin + 1;j < end - 1;j += 3){
                if(haveBlackAround(image, i, j)){
                    newImage[(i - begin - 1) / 3][(j - begin - 1) / 3] = true;
                }
            }
        }
        newImage[0][0] = false;
        newImage[0][73] = false;
        newImage[73][0] = false;

        return newImage;
    }

    private static boolean haveBlackAround(boolean[][] image, int i, int j){
        return image[i][j] || image[i][j - 1] || image[i][j + 1]
                || image[i - 1][j - 1] || image[i - 1][j] || image[i - 1][j + 1]
                || image[i + 1][j - 1] || image[i + 1][j] || image[i + 1][j + 1];
    }

    public static Code converter(Code code){
        ByteMatrix matrix = code.getMatrix();

        int newWidth = matrix.getWidth();
        int newHeight = matrix.getHeight();

        ByteMatrix newMatrix = new ByteMatrix(newHeight / 3, newWidth / 3);

        for(int i = 4;i < matrix.getHeight() - 1;i += 3){
            for(int j = 4;j < matrix.getWidth() - 1;j += 3){
                if(haveBlackAround(matrix, i, j)){
                    newMatrix.set(i / 3, j / 3, true);
                }
            }
        }

        Code newCode = new Code();
        code.translate(newCode);
        newCode.setMatrix(newMatrix);

        return newCode;
    }


    private static boolean isBlack(ByteMatrix matrix, int i, int j){
        return matrix.get(i, j) == (byte) 1;
    }
    private static boolean haveBlackAround(ByteMatrix matrix, int i, int j){
//        System.out.println(i + " , " + j);
        return isBlack(matrix, i, j) || isBlack(matrix, i + 1, j) || isBlack(matrix, i - 1, j)
                || isBlack(matrix, i, j + 1) || isBlack(matrix, i + 1, j + 1) || isBlack(matrix, i - 1, j + 1)
                || isBlack(matrix, i, j - 1) || isBlack(matrix, i + 1, j - 1) || isBlack(matrix, i - 1, j - 1);
    }

}
