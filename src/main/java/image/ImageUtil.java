package image;

import code.CodeWriter;
import common.BitMatrix;
import metadata.ErrorCorrectionLevel;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Coordinate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Random;

public class ImageUtil {
    public void buildImage(String destPath) throws Exception {
        BufferedImage image = createImage("123456",destPath, true, 1, 1);
        mkdirs(destPath);
//        String file = new Random().nextInt(999999999) + ".jpg";
        String file = "test2.jpg";
        ImageIO.write(image, "JPG", new File(destPath + "/" + file));
    }

    public void buildImage(String content, String destPath, String fileName) throws Exception {
        BufferedImage image = createImage(content,destPath, true, 1, 1);
        mkdirs(destPath);
        ImageIO.write(image, "bmp", new File(destPath + "/" + fileName));
    }

    public void buildImageOldCode(String content, String destPath, String fileName) throws Exception {
        BufferedImage image = createImage(content,destPath,false, 1, 1);
        mkdirs(destPath);
        ImageIO.write(image, "JPG", new File(destPath + "/" + fileName));
    }

    public void buildImageMulCode(String content, String destPath, String fileName, int numberV, int numberH) throws Exception {
        BufferedImage image = createImage(content,destPath,false, numberV, numberH);
        mkdirs(destPath);
        ImageIO.write(image, "bmp", new File(destPath + "/" + fileName));
    }

    public static void mkdirs(String destPath) {
        File file =new File(destPath);
        //当文件夹不存在时，mkdirs会自动创建多层目录，区别于mkdir．(mkdir如果父目录不存在则会抛出异常)
        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
        }
    }

    private int codeSize = 300;
    public BufferedImage createImage(String content, String imgPath, boolean isSpa, int numberV, int numberH) throws  Exception{
        BitMatrix bitMatrix = null;
        if(numberH > 1 && numberV > 1){
            System.out.println("build mul matrix");
            bitMatrix = new CodeWriter().encodeMatrix(content, codeSize, codeSize, ErrorCorrectionLevel.H, numberV, numberH);
        }else {
            if (isSpa) {
                bitMatrix = new CodeWriter().encode(content, codeSize, codeSize, ErrorCorrectionLevel.H);
            } else {
                bitMatrix = new CodeWriter().encodeOldCode(content, codeSize, codeSize, ErrorCorrectionLevel.H);
            }
        }

        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();

        BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(y, x) ? 0xFF000000
                        : 0xFFFFFFFF);
            }
        }
        if (imgPath == null || "".equals(imgPath)) {
            return image;
        }
        return image;
    }


//    public static void main(String[] args) throws IOException {
//        int width = 444;
//        int height = 444;
//        BufferedImage codeImage = Thumbnails.of("F://无边框有标识点单个图像.bmp").size(222, 222).asBufferedImage( );
//        Thumbnails.of("F://无边框有标识点单个图像.bmp").size(width, height)
//                .watermark(new Coordinate(0, 0),codeImage,1f)
//                .watermark(new Coordinate((int) (width * 0.5f), 0),codeImage,1f)
//                .watermark(new Coordinate(0, (int) (height * 0.5f)),codeImage,1f)
//                .watermark(new Coordinate((int) (width * 0.5f), (int) (height * 0.5f)),codeImage,1f)
//                .toFile("F://无边框有标识点组合图像.bmp");
//    }
}
