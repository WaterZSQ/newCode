import code.CodeConverter;
import encoder.ByteMatrix;
import encoder.Code;
import encoder.Encoder;
import encoder.MatrixUtil;
import exception.WriterException;
import image.ImageOptimizationUtil;
import image.ImageUtil;
import metadata.ErrorCorrectionLevel;
import org.junit.Test;

import java.io.IOException;

public class TestEncode {

    @Test
    public void testBuildMatrix() throws WriterException {
//        Encoder.encode("abc", ErrorCorrectionLevel.L);
        Code codeL = Encoder.encode("123456", ErrorCorrectionLevel.Q);
        System.out.println(codeL.toString());

//        Code codeM = Encoder.encode("123", ErrorCorrectionLevel.M);
//        Code codeQ = Encoder.encode("123", ErrorCorrectionLevel.Q);
//        Code codeH = Encoder.encode("123", ErrorCorrectionLevel.H);
    }

    @Test
    public void test1() throws IOException {
        boolean[][] image1 = ImageOptimizationUtil.binarization("F://test1.jpg");
        boolean[][] image2 = ImageOptimizationUtil.binarization("F://test2.jpg");
        for(int i = 0;i < image1.length;i++){
            for(int j = 0;j < image1[0].length;j++){
                if(image1[i][j] != image2[i][j]){
                    System.out.println(i + ", " + j);
                    System.out.println(image1[i][j]+ " - " + image2[i][j]);
                    return;
                }

                if(image1[i][j]){
                    System.out.print("1 ");
                }else{
                    System.out.print("0 ");
                }
            }
            System.out.println();
        }
        System.out.println("相同");
    }

    private boolean isValidBoard(boolean[][] image){
        int height = image.length;
        int width = image[0].length;
        if(!image[0][0] || !image[height - 1][0] || !image[0][width - 1]){
            return false;
        }

        for(int i = 3;i < width;i += 3){
            if((i % 2 == 1) != image[3][i]){
                System.out.println("false !!");
                return false;
            }
        }

        for(int i = 3;i < width;i += 3){
            if((i % 2 == 1) != image[i][3]){
                System.out.println("false !!");
                return false;
            }
        }

        return true;
    }

    private boolean isValidData(boolean[][] image){
        int height = image.length;
        int width = image[0].length;

        for(int i = 3;i < height;i += 3){
            if(i % 3 == 0){
                continue;
            }
            for(int j = 3;j < width;j += 2){
                if(j % 3 == 0){

                }
            }
        }
        return true;
    }


    @Test
    public void testValid() throws IOException {
        boolean[][] image = ImageOptimizationUtil.binarization("F://test2.jpg");
        System.out.println(isValidBoard(image));
    }


    @Test
    public void testBuildNewMatrix() throws WriterException, IOException {
        Code oldCode = Encoder.encode("123456", ErrorCorrectionLevel.Q);
        ByteMatrix matrix = oldCode.getMatrix();
        System.out.println(matrix.toString());
        System.out.println();
        System.out.println("-----------------");
        boolean[][] image = ImageOptimizationUtil.binarization("F://123456.jpg");
        boolean[][] newImage = CodeConverter.converter(image);
        for(int i = 0; i < newImage.length;i++){
            for(int j = 0; j < newImage[0].length;j++){
                if(newImage[i][j]){
                    System.out.print("1 ");
                }else{
                    System.out.print("0 ");
                }
            }
            System.out.println();
        }



        for(int i = 0;i < matrix.getHeight();i++){
            for(int j = 0;j < matrix.getWidth();j++){
                if((matrix.get(i, j) == (byte) 1 && image[i][j]) || (matrix.get(i, j) == (byte) 0 && !image[i][j])){
                    continue;
                }else{
                    System.out.println(i + ", " + j + " -- " + matrix.get(i, j) + " : " + image[i][j]);
                    return;
                }
            }
        }

//        Code newCode = CodeConverter.converter(oldCode, 3);
//        System.out.println(newCode);

//        Code code = CodeConverter.converter(newCode);
//        System.out.println(code);
//        System.out.println(code.equal(oldCode));
    }

    @Test
    public void testBuildImage() throws Exception {
        ImageUtil imageUtil = new ImageUtil();
        imageUtil.buildImage("abcs12345", "F:\\", "test9.jpg");
//        imageUtil.buildImage("123456", "F:\\", "test2.jpg");
    }

    @Test
    public void testBuildImage1() throws Exception {
        ImageUtil imageUtil = new ImageUtil();
//        imageUtil.buildImageOldCode("abcs12345", "F:\\", "try1.jpg");
        imageUtil.buildImage("abcs12345", "F:\\", "无边框有标识点单个图像.bmp");
    }



    @Test
    public void testBuildImage2() throws Exception {
        ImageUtil imageUtil = new ImageUtil();
//        imageUtil.buildImageOldCode("abcs12345", "F:\\", "try1.jpg");
        imageUtil.buildImageMulCode("abcs12345", "F:\\", "12132131.bmp", 2, 2);
    }

    @Test
    public void testBuildImage3() throws Exception {
        ImageUtil imageUtil = new ImageUtil();
//        imageUtil.buildImageOldCode("abcs12345", "F:\\", "try1.jpg");
        imageUtil.buildImageMulCode("abcs12345", "F:\\", "300.bmp", 2, 2);
    }

    @Test
    public void test(){
        char[] chars = MatrixUtil.int2charArray(1);
        for(char c : chars){
            System.out.print(c + "  ");
        }
        System.out.println();
        char[] chars1 = MatrixUtil.int2charArray(2);
        for(char c : chars1){
            System.out.print(c + "  ");
        }        System.out.println();

        char[] chars2 = MatrixUtil.int2charArray(3);
        for(char c : chars2){
            System.out.print(c + "  ");
        }
        System.out.println();
        char[] chars3 = MatrixUtil.int2charArray(4);
        for(char c : chars3){
            System.out.print(c + "  ");
        }
    }


    // 版本1，纠错级别为L，最多可以编码41个数字
    @Test
    public void testNumLengthIs41ECLevelL() throws WriterException {
        StringBuilder stringBuilder = new StringBuilder(41);
        for(int i = 0;i < 41;i++){
            stringBuilder.append("0");
        }
        Code code = Encoder.encode(stringBuilder.toString(), ErrorCorrectionLevel.L);
        System.out.println(code.toString());
    }

    // 版本1，纠错级别为M，最多可以编码34个数字
    @Test
    public void testNumLengthIs34ECLevelM() throws WriterException {
        StringBuilder stringBuilder = new StringBuilder(34);
        for(int i = 0;i < 34;i++){
            stringBuilder.append("0");
        }
        Code code = Encoder.encode(stringBuilder.toString(), ErrorCorrectionLevel.M);
        System.out.println(code.toString());
    }

    // 版本1，纠错级别为L，最多可以编码25个字母
    @Test
    public void testAlphaNumLengthIs25() throws WriterException {
        StringBuilder stringBuilder = new StringBuilder(25);
        for(int i = 0;i < 15;i++){
            stringBuilder.append("a");
        }
        stringBuilder.append("0123456789");
        Code code = Encoder.encode(stringBuilder.toString(), ErrorCorrectionLevel.L);
        System.out.println(code.toString());
    }

    // 版本1，纠错级别为M，最多可以编码20个字母
    @Test
    public void testAlphaNumLengthIs20ECLevelM() throws WriterException {
        StringBuilder stringBuilder = new StringBuilder(20);
        for(int i = 0;i < 20;i++){
            stringBuilder.append("a");
        }
        Code code = Encoder.encode(stringBuilder.toString(), ErrorCorrectionLevel.M);
        System.out.println(code.toString());
    }
}
