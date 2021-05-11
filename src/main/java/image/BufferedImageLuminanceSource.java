package image;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class BufferedImageLuminanceSource extends LuminanceSource{
    private static final double MINUS_45_IN_RADIANS = -0.7853981633974483; // Math.toRadians(-45.0)

    private final BufferedImage image;
    private final int left;
    private final int top;


    public BufferedImageLuminanceSource(BufferedImage image){
        this(image, 0, 0, image.getWidth(), image.getHeight());
    }

    public BufferedImageLuminanceSource(BufferedImage image, int left, int top, int width, int height){
        super(width, height);

        if(image.getType() == BufferedImage.TYPE_BYTE_GRAY){
            this.image = image;
        }else{
            int sourceWidth = image.getWidth();
            int sourceHeight = image.getHeight();

            if(left + width > sourceWidth || top + height > sourceHeight){
                throw new IllegalArgumentException();
            }

            this.image = new BufferedImage(sourceWidth, sourceHeight, BufferedImage.TYPE_BYTE_GRAY);
            WritableRaster raster = this.image.getRaster();
            int[] buffer = new int[width];

            for(int y = top;y < top + height;y++){
                image.getRGB(left, y, width, 1, buffer, 0, sourceWidth);
                for(int x = 0;x < width;x++){
                    int pixel = buffer[x];
                    if ((pixel & 0xFF000000) == 0) { // 白色块
                        buffer[x] = 0xFF;
                    } else {
                        // .299R + 0.587G + 0.114B (YUV/YIQ for PAL and NTSC),
                        // (306*R) >> 10 is approximately equal to R*0.299, and so on.
                        // 0x200 >> 10 is 0.5, it implements rounding.
                        buffer[x] =
                                (306 * ((pixel >> 16) & 0xFF) +
                                        601 * ((pixel >> 8) & 0xFF) +
                                        117 * (pixel & 0xFF) +
                                        0x200) >> 10;
                    }
                }
                raster.setPixels(left, y, width, 1, buffer);
            }
        }

        this.left = left;
        this.top = top;
    }

    @Override
    public byte[] getRow(int y, byte[] row) {
        if(y < 0 || y >= getHeight()){
            throw new IllegalArgumentException();
        }
        int width = getWidth();
        if(row == null || row.length < width){
            row = new byte[width];
        }

        image.getRaster().getDataElements(left, top + y, width, 1, row);
        return row;
    }

    @Override
    public byte[] getMatrix() {
        int width = getWidth();
        int height = getHeight();
        int area = width * height;
        byte[] matrix = new byte[area];

        image.getRaster().getDataElements(left, top, width, height, matrix);
        return matrix;
    }
}
