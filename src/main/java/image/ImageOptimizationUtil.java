package image;

import code.CodeConverter;
import common.Utils;
import decoder.Decoder;
import decoder.DecoderResult;
import encoder.Code;
import encoder.Encoder;
import exception.ChecksumException;
import exception.FormatException;
import exception.WriterException;
import metadata.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageOptimizationUtil {
    public final static int YZ = 150;

    public static boolean[][] binarization(String filePath) throws IOException {
        File file = new File(filePath);
        BufferedImage bi = ImageIO.read(file);

        int h = bi.getHeight();
        int w = bi.getWidth();
        if(h == 222 && w == 222){
            System.out.println("222 * 222");
        }else if(h == 444 && w == 444){
            System.out.println("444 * 444");
        }else if(h == 444 && w == 222){
            System.out.println("444 * 222");
        }else if(h == 222 && w == 444){
            System.out.println("222 * 444");
        }
        int[][] arr = new int[h][w];

        for(int i = 0;i < h;i++){
            for(int j = 0;j < w;j++){
                arr[j][i] = getImageGray(bi.getRGB(i, j));
            }
        }

//        BufferedImage bufferedImage = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_BINARY);

        boolean[][] image = new boolean[h][w];

        for(int i = 0;i < h;i++){
            for(int j = 0;j < w;j++){
//                int gray = getGray(arr, i, j, w, h);
                int gray = arr[i][j];
//                System.out.println(i + ", " + j + " : " + gray);
                if(gray > YZ){
                    int white = new Color(255,255,255).getRGB();
//                    bufferedImage.setRGB(i, j, white);
                    image[i][j] = false;
                }else{
                    int black = new Color(0,0,0).getRGB();
//                    bufferedImage.setRGB(i, j, black);
                    image[i][j] = true;
                }
            }
        }

//        ImageIO.write(bufferedImage, "jpg", new File(outputPath));
//        Utils.printBooleanArr(image);
        System.out.println("--------------------------------------------");
        return image;
    }


    // 解析单个码
    public static DecoderResult decodeOneCode(boolean[][] image) throws FormatException, ChecksumException{
        boolean[][] code = CodeConverter.converter(image);
        Decoder decoder = new Decoder();
        return decoder.decode(code);
    }

    // 解析码
    public static DecoderResult decodeCode(String path){
        try {
            DecoderResult result = null;
            boolean[][] image = ImageOptimizationUtil.binarization(path);
            int countBlockPoint = 0;
            for (boolean[] booleans : image) {// 判断第一列有多少个黑点
                if (booleans[0]) {
                    countBlockPoint++;
                }
            }
            if(countBlockPoint == 2){ // 单个code
                result = decodeOneCode(image);
            }else{
//                System.out.println("mul code");
                for(int i = 0;i < 2;i++){
                    for(int j = 0;j < 2;j++){
                        boolean[][] smallImage = new boolean[222][222];

                        for(int m = 0;m < 222;m++){
                            for(int n = 0;n < 222;n++){
                                if(image[i * 222 + m][j * 222 + n]){
                                    smallImage[m][n] = true;
                                }
                            }
                        }

//                        Utils.printBooleanArr(smallImage);

                        try {
                            result = decodeOneCode(smallImage);// 失败的话，继续下一个矩阵的读取
                        }catch (FormatException | ChecksumException e){
                            if(i == 1 && j == 1){
                                throw FormatException.getFormatInstance();
                            }
                        }
                    }
                }
            }
            return result;
        } catch (IOException | FormatException | ChecksumException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
//        DecoderResult result = decodeCode("F://12132131.bmp");
        DecoderResult result = decodeCode("F://无边框有标识点单个图像.bmp");
        System.out.println(result.toString());
    }

    // 自己的灰度值加上周围8个点的灰度值再除以9，得到相对灰度值
    private static int getGray(int[][] gray, int x, int y, int w, int h){
        int rs = gray[x][y] + (x == 0 ? 255 : gray[x - 1][y]) + (x == 0 || y == 0 ? 255 : gray[x - 1][y - 1])
                + (x == 0 || y == h - 1 ? 255 : gray[x - 1][y + 1]) + (y == 0 ? 255 : gray[x][y - 1])
                + (y == h - 1 ? 255 : gray[x][y + 1]) + (x == w - 1 ? 255 : gray[x + 1][y])
                + (x == w - 1 || y == 0 ? 255 : gray[x + 1][y - 1])
                + (x == w - 1 || y == h - 1 ? 255 : gray[x + 1][y + 1]);
        return rs / 9;
    }

    // 灰度计算
    // 利用浮点算法：Gray = R*0.3 + G*0.59 + B*0.11
    private static int getImageGray(int rgb){
        String argb = Integer.toHexString(rgb);

        int r = Integer.parseInt(argb.substring(2, 4), 16);
        int g = Integer.parseInt(argb.substring(4, 6), 16);
        int b = Integer.parseInt(argb.substring(6, 8), 16);
        int gray = (int) (r * 0.28 + g * 0.59 + b * 0.11);
        return gray;
    }
}
