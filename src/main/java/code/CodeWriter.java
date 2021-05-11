package code;

import common.BitMatrix;
import encoder.ByteMatrix;
import encoder.Code;
import encoder.Encoder;
import exception.WriterException;
import metadata.ErrorCorrectionLevel;

public class CodeWriter {
    private static final int QUIET_ZONE_SIZE = 4;
    public BitMatrix encode(String contents, int width, int height, ErrorCorrectionLevel ecLevel) throws WriterException {
        if(contents.isEmpty()){
            throw new IllegalArgumentException("empty contents");
        }

        if(width <= 0 || height <= 0){
            throw new IllegalArgumentException("dimensions are too small");
        }

        Code code = Encoder.encode(contents, ecLevel);
        Code newCode = CodeConverter.converter(code, 3);
        return renderResult(newCode, width, height, QUIET_ZONE_SIZE);
    }

    // 组合图形
    public BitMatrix encodeMatrix(String contents, int width, int height, ErrorCorrectionLevel ecLevel, int numberV, int numberH) throws WriterException {
        if(contents.isEmpty()){
            throw new IllegalArgumentException("empty contents");
        }

        if(width <= 0 || height <= 0){
            throw new IllegalArgumentException("dimensions are too small");
        }

        Code code = Encoder.encode(contents, ecLevel);
        Code newCode = CodeConverter.converter(code, 3, 2, 2);
        return renderResult(newCode, width, height, QUIET_ZONE_SIZE);
    }

    public BitMatrix encodeOldCode(String contents, int width, int height, ErrorCorrectionLevel ecLevel) throws WriterException {
        if(contents.isEmpty()){
            throw new IllegalArgumentException("empty contents");
        }

        if(width <= 0 || height <= 0){
            throw new IllegalArgumentException("dimensions are too small");
        }

        Code code = Encoder.encode(contents, ecLevel);
        return renderResult(code, width, height, QUIET_ZONE_SIZE);
    }

    public static BitMatrix renderResult(Code code, int width, int height, int quietZone){
        ByteMatrix input = code.getMatrix();

        if(input == null){
            throw new IllegalArgumentException();
        }

        int inputWidth = input.getWidth();
        int inputHeight = input.getHeight();

        // 空白边界
//        int qrWidth = inputWidth + (quietZone * 2);
//        int qrHeight = inputHeight + (quietZone * 2);

        int qrWidth = inputWidth;
        int qrHeight = inputHeight;

        int outputWidth = Math.max(width, qrWidth);
        int outputHeight = Math.max(height, qrHeight);

        int multiple = Math.min(outputWidth / qrWidth, outputHeight / qrHeight);
        // Padding includes both the quiet zone and the extra white pixels to accommodate the requested
        // dimensions. For example, if input is 25x25 the QR will be 33x33 including the quiet zone.
        // If the requested size is 200x160, the multiple will be 4, for a QR of 132x132. These will
        // handle all the padding from 100x100 (the actual QR) up to 200x160.
        int leftPadding = (outputWidth - (inputWidth * multiple)) / 2;
        int topPadding = (outputHeight - (inputHeight * multiple)) / 2;

//        if(leftPadding >= 0){
//            outputWidth = outputWidth - 2 * leftPadding;
//            leftPadding = 0;
//        }
//        if(topPadding >= 0){
//            outputHeight = outputHeight - 2 * topPadding;
//            topPadding = 0;
//        }

//        System.out.println("outputWidth : " + outputWidth);
//        System.out.println("outputHeight : " + outputHeight);
        BitMatrix output = new BitMatrix(outputWidth, outputHeight);

        for (int inputY = 0, outputY = topPadding; inputY < inputHeight; inputY++, outputY += multiple) {
            // Write the contents of this row of the barcode
            for (int inputX = 0, outputX = leftPadding; inputX < inputWidth; inputX++, outputX += multiple) {
                if (input.get(inputX, inputY) == 1) {
                    output.setRegion(outputX, outputY, multiple, multiple);
                }
            }
        }

        return output;
    }
}
