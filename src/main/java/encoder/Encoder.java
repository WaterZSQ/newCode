package encoder;

import common.BitArray;
import common.reedsolomon.GenericGF;
import common.reedsolomon.ReedSolomonEncoder;
import exception.WriterException;
import metadata.ErrorCorrectionLevel;
import metadata.Mode;
import metadata.Version;

import java.util.ArrayList;
import java.util.Collection;

public class Encoder {
    // The original table is defined in the table 5 of JISX0510:2004 (p.19).
    // 数字 + 字母 + 一些符号（空格 $ % * + - . /）
    private static final int[] ALPHANUMERIC_TABLE = {
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,  // 0x00-0x0f 0 - 15
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,  // 0x10-0x1f 16 - 31
            36, -1, -1, -1, 37, 38, -1, -1, -1, -1, 39, 40, -1, 41, 42, 43,  // 0x20-0x2f 32 - 47
            0,   1,  2,  3,  4,  5,  6,  7,  8,  9, 44, -1, -1, -1, -1, -1,  // 0x30-0x3f
            -1, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24,  // 0x40-0x4f
            25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, -1, -1, -1, -1, -1,  // 0x50-0x5f
    };


    public static Code encode(String content, ErrorCorrectionLevel ecLevel) throws WriterException {
        Mode mode = chooseMode(content);

        BitArray headerBits = new BitArray();

        appendModeInfo(mode, headerBits);
//        appendEcLevelInfo(ecLevel, headerBits);
//        System.out.println("header : " + headerBits.toString());

        BitArray dataBits = new BitArray();
        appendBytes(content, mode, dataBits);


        Version version = Version.getVersionForNumber(1);

        BitArray headerAndDataBits = new BitArray();
        headerAndDataBits.appendBitArray(headerBits);
//        System.out.println("mode : " + mode.toString());

        int numLetters = content.length();
        appendLengthInfo(numLetters, mode, headerAndDataBits);
        headerAndDataBits.appendBitArray(dataBits);
//        System.out.println("header and data : " + headerAndDataBits);
//        System.out.println("header and data length : " + headerAndDataBits.getSize());
//        System.out.println("data : " + dataBits.toString());

        int numDataBytes = version.getCodeword()[ecLevel.getBits()][0];

        // 终止符，补齐
        terminateBits(numDataBytes, headerAndDataBits);

        // 得到 模式（4位） + 长度 + 数据 + 纠错码 字节数组
        BitArray finalBits = interleaveWithECBytes(version, ecLevel, headerAndDataBits, version.getTotalCodewords());
//        System.out.println("finalBits : " + finalBits.toString());

        int dimension = version.getDimensionForVersion();
        ByteMatrix matrix = new ByteMatrix(dimension, dimension);

        MatrixUtil.buildMatrix(finalBits, ecLevel, version, matrix);

//        System.out.println("headerAndDataBits : " + headerAndDataBits.toString());
//        System.out.println("-----------------------------------");
//        System.out.println(matrix.toString());

        Code code = new Code();
        code.setEcLevel(ecLevel);
        code.setMode(mode);
        code.setVersion(version);
        code.setMatrix(matrix);
        return code;
    }

    private static BitArray interleaveWithECBytes(Version version, ErrorCorrectionLevel ecLevel, BitArray bits, int numTotalBytes) throws WriterException {
        int dataBytesOffset = 0;
        int maxNumDataBytes = 0;
        int maxNumECBytes = 0;


        int numDataBytes = version.getCodeword()[ecLevel.getBits()][0];
        int numEcBytes = version.getTotalCodewords() - numDataBytes;

        if(bits.getSizeInBytes() != numDataBytes){
            throw new WriterException("Number of bits and data bytes doer not match");
        }

        // 得到块中数据码字和纠错码字的字数
//        getNumDataByteAndNumECBytes(numTotalBytes, numDataBytes, numDataBytesInBlock, numEcBytesInBlock);
//        System.out.println("数据码字数量：" + numDataBytes);
//        System.out.println("纠错码字数量：" + numEcBytes);


        int size = numDataBytes;
        byte[] dataBytes = new byte[size];
        bits.toBytes(8 * dataBytesOffset, dataBytes, 0, size);

        byte[] ecBytes = generateECBytes(dataBytes, numEcBytes);

        BlockPair blockPair = new BlockPair(dataBytes, ecBytes);

        maxNumDataBytes = Math.max(maxNumDataBytes, size);
        maxNumECBytes = Math.max(maxNumECBytes, ecBytes.length);
        dataBytesOffset += numDataBytes;


        if(numDataBytes != dataBytesOffset){
            throw new WriterException("Data bytes does not match offset");
        }

        BitArray result = new BitArray();
        // 放置数据码字
        for(int i = 0;i < maxNumDataBytes;i++){
            byte[] dataByte = blockPair.getDataBytes();
            if(i < dataByte.length){
                result.appendBits(dataBytes[i], 8);
            }
        }

        // 放置纠错码字
        for(int i = 0;i < maxNumECBytes;i++){
            byte[] ecByte = blockPair.getErrorCorrectionBytes();
            if(i < ecByte.length){
                result.appendBits(ecByte[i], 8);
            }
        }

        if(numTotalBytes != result.getSizeInBytes()){
            System.out.println(numTotalBytes);
            System.out.println(result.getSizeInBytes());
            throw new WriterException();
        }
        return result;
    }

    // 生成纠错码
    private static byte[] generateECBytes(byte[] dataBytes, int numEcBytesInBlock){
        int numDataBytes = dataBytes.length;
        int[] toEncode = new int[numDataBytes + numEcBytesInBlock];
        for(int i = 0;i < numDataBytes;i++){
            toEncode[i] = dataBytes[i] & 0xFF;
        }

        new ReedSolomonEncoder(GenericGF.CODE_FIELD_256).encode(toEncode, numEcBytesInBlock);

        byte[] ecBytes = new byte[numEcBytesInBlock];
        for(int i = 0;i < numEcBytesInBlock;i++){
            ecBytes[i] = (byte) toEncode[numDataBytes + i];
        }
        return ecBytes;
    }

    private static void getNumDataByteAndNumECBytes(int numTotalBytes, int numDataBytes, int[] numDataBytesInBlock, int[] numECBytesInBlock) throws WriterException {
        int numRSBlocks = 1;

        // numRsBlocksInGroup2 = 196 % 5 = 1
        int numRsBlocksInGroup2 = numTotalBytes % numRSBlocks;
        // numRsBlocksInGroup1 = 5 - 1 = 4
        int numRsBlocksInGroup1 = numRSBlocks - numRsBlocksInGroup2;
        // numTotalBytesInGroup1 = 196 / 5 = 39
        int numTotalBytesInGroup1 = numTotalBytes / numRSBlocks;
        // numTotalBytesInGroup2 = 39 + 1 = 40
        int numTotalBytesInGroup2 = numTotalBytesInGroup1 + 1;
        // numDataBytesInGroup1 = 66 / 5 = 13
        int numDataBytesInGroup1 = numDataBytes / numRSBlocks;
        // numDataBytesInGroup2 = 13 + 1 = 14
        int numDataBytesInGroup2 = numDataBytesInGroup1 + 1;
        // numEcBytesInGroup1 = 39 - 13 = 26
        int numEcBytesInGroup1 = numTotalBytesInGroup1 - numDataBytesInGroup1;
        // numEcBytesInGroup2 = 40 - 14 = 26
        int numEcBytesInGroup2 = numTotalBytesInGroup2 - numDataBytesInGroup2;
        // Sanity checks.
        // 26 = 26
        if (numEcBytesInGroup1 != numEcBytesInGroup2) {
            throw new WriterException("EC bytes mismatch");
        }
        // 196 = (13 + 26) * 4 + (14 + 26) * 1
        if (numTotalBytes !=
                ((numDataBytesInGroup1 + numEcBytesInGroup1) *
                        numRsBlocksInGroup1) +
                        ((numDataBytesInGroup2 + numEcBytesInGroup2) *
                                numRsBlocksInGroup2)) {
            throw new WriterException("Total bytes mismatch");
        }

//        if (0 < numRsBlocksInGroup1) {
            numDataBytesInBlock[0] = numDataBytesInGroup1;
            numECBytesInBlock[0] = numEcBytesInGroup1;
//        } else {
//            numDataBytesInBlock[0] = numDataBytesInGroup2;
//            numECBytesInBlock[0] = numEcBytesInGroup2;
//        }
    }

    private static void terminateBits(int numDataBytes, BitArray bits) throws WriterException {
        int capacity = numDataBytes * 8;
        if(bits.getSize() > capacity){
//            System.out.println("数据流的大小：" + bits.getSize());
//            System.out.println("容量: " + capacity);
            throw new WriterException();
        }

        // ？？？
        for(int i = 0;i < 4 && bits.getSize() < capacity;i++){
//            System.out.println("Encoder - terminateBits");
            bits.appendBit(false);
        }

        int numBitsInLastByte = bits.getSize() & 0x07;
        // 如果最后一个码字没有八个bit，需要补齐8bit
        if(numBitsInLastByte > 0){
            for(int i = numBitsInLastByte;i < 8;i++){
                bits.appendBit(false);
            }
        }

        // 如果还有剩余的空间，需要增加填充码
        int numPaddingBytes = numDataBytes - bits.getSizeInBytes();
        for(int i = 0;i < numPaddingBytes;i++){
            bits.appendBits((i & 0x01) == 0 ? 0xEC : 0x11, 8);// 先填充 1110 1100 再填充 0001 0001
        }

        if(bits.getSize() != capacity){
            throw new WriterException();
        }
    }

    private static void appendBytes(String content, Mode mode, BitArray bits) throws WriterException {
        switch (mode){
            case NUMERIC:
                appendNumericBytes(content, bits);
                break;
            case ALPHANUMERIC:
                appendAlphanumericBytes(content, bits);
                break;
            case BYTE:
                append8BitBytes(content, bits);
                break;
            default:
                throw new WriterException("Invalid mode : " + mode);
        }
    }

    private static void appendNumericBytes(String content, BitArray bits){
        int length = content.length();
        int i = 0;
        while(i < length){
            int num1 = content.charAt(i) - '0';
            if(i + 2 < length){//3个数字一组
                int num2 = content.charAt(i + 1) - '0';
                int num3 = content.charAt(i + 2) - '0';
                bits.appendBits(num1 * 100 + num2 * 10 + num3, 10);
                i += 3;
            }else if(i + 1 < length){
                int num2 = content.charAt(i + 1) - '0';
                bits.appendBits(num1 * 10 + num2, 7);
                i += 2;
            }else{
                bits.appendBits(num1, 4);
                i++;
            }
        }
    }

    private static void appendAlphanumericBytes(String content, BitArray bits) throws WriterException {
        int length = content.length();
        int i = 0;
        while(i < length){
            int code1 = getAlphanumericCode(content.charAt(i));
            if(code1 == -1){
                throw new WriterException();
            }

            if(i + 1 < length){// 两个char一组
                int code2 = getAlphanumericCode(content.charAt(i + 1));
                if(code2 == -1){
                    throw new WriterException();
                }

                bits.appendBits(code1 * 45 + code2, 11);
                i += 2;
            }else{
                bits.appendBits(code1, 6);
                i++;
            }
        }
    }

    private static void append8BitBytes(String content, BitArray bits){
        byte[] bytes = content.getBytes();
        for(byte b : bytes){
            bits.appendBits(b, 8);
        }
    }

    private static void appendModeInfo(Mode mode, BitArray bits){
        bits.appendBits(mode.getBits(), 4);
    }

    private static void appendLengthInfo(int numberLetters, Mode mode, BitArray bits) throws WriterException {
        int numBits = mode.getCharacterCountBits();

        // 数据码字数量大于容量
        if(numberLetters >= (1 << numBits)){
            throw new WriterException(numberLetters + " is bigger than " + ((1 << numBits) - 1));
        }
//        System.out.println("length info length : " + numBits);
        bits.appendBits(numberLetters, numBits);
    }

    private static void appendEcLevelInfo(ErrorCorrectionLevel ecLevel, BitArray bits){
        bits.appendBits(ecLevel.getBits(), 2);
    }



    private static Mode chooseMode(String content){
        boolean hasNumeric = false;
        boolean hasAlphanumeric = false;

        for(int i = 0;i < content.length();i++){
            char c = content.charAt(i);
            if(c >= '0' && c <= '9'){
                hasNumeric = true;
            }else if(getAlphanumericCode(c) != -1){
                hasAlphanumeric = true;
            }else{
                return Mode.BYTE;
            }
        }

        if(hasAlphanumeric){
            return Mode.ALPHANUMERIC;
        }

        if(hasNumeric){
            return Mode.NUMERIC;
        }

        return Mode.BYTE;
    }

    private static int getAlphanumericCode(int code){
        if(code >= 97 && code <= 122){
            code -= 32;
        }
        if (code < ALPHANUMERIC_TABLE.length) {
            return ALPHANUMERIC_TABLE[code];
        }
        return -1;
    }


}


