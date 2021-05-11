package decoder;

import common.BitSource;
import common.StringUtils;
import exception.FormatException;
import metadata.ErrorCorrectionLevel;
import metadata.Mode;
import metadata.Version;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class DecodedBitStreamParser {
    private static final char[] ALPHANUMERIC_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ $%*+-./:".toCharArray();

    private DecodedBitStreamParser(){

    }

    public static DecoderResult decode(byte[] bytes) throws FormatException {
        BitSource bits = new BitSource(bytes);
        StringBuilder result = new StringBuilder(50);
        List<byte[]> byteSegments = new ArrayList<>();
        ErrorCorrectionLevel ecLevel = null;
        try{
            Mode mode;

            while(true){
                if(bits.available() < 4){
                    mode = Mode.TERMINATOR;
                }else{// 头四个字节是mode
                    mode = Mode.forBits(bits.readBits(4));
//                    System.out.println(mode);
                }
                if(mode == Mode.TERMINATOR){
                    break;
                }
//                ecLevel = ErrorCorrectionLevel.forBits(bits.readBits(2));
                int count = bits.readBits(mode.getCharacterCountBits());

                switch (mode){
                    case NUMERIC:
                        decodeNumericSegment(bits, result ,count);
                        break;
                    case ALPHANUMERIC:
                        decodeAlphanumericSegment(bits, result, count);
                        break;
                    case BYTE:
                        decodeByteSegment(bits, result, count, byteSegments);
                        break;
                    default:
                        throw FormatException.getFormatInstance();
                }
            }
        }catch (IllegalAccessError e){
            throw FormatException.getFormatInstance();
        }

        return new DecoderResult(bytes, result.toString(), byteSegments.isEmpty() ? null : byteSegments);
    }

    public static void decodeByteSegment(BitSource bits, StringBuilder result, int count, Collection<byte[]> byteSegments) throws FormatException {
        if(8 * count > bits.available()){
            throw FormatException.getFormatInstance();
        }

        byte[] readBytes = new byte[count];
        for(int i = 0;i < count;i++){
            readBytes[i] = (byte) bits.readBits(8);
        }

        Charset encoding = StringUtils.guessCharset(readBytes);
        result.append(new String(readBytes, encoding));
        byteSegments.add(readBytes);
    }

    public static void decodeNumericSegment(BitSource bits, StringBuilder result, int count) throws FormatException {
        while(count >= 3){
            if(bits.available() < 10){
                throw FormatException.getFormatInstance();
            }
            int threeDigitBits = bits.readBits(10);
            if(threeDigitBits > 999){
                throw FormatException.getFormatInstance();
            }
            result.append(toAlphaNumericChar(threeDigitBits / 100));
            result.append(toAlphaNumericChar((threeDigitBits / 10) % 10));
            result.append(toAlphaNumericChar(threeDigitBits % 10));
            count -= 3;
        }

        if(count == 2){
            if(bits.available() < 7){
                throw FormatException.getFormatInstance();
            }
            int twoDigitsBits = bits.readBits(7);
            if(twoDigitsBits > 99){
                throw FormatException.getFormatInstance();
            }
            result.append(toAlphaNumericChar(twoDigitsBits / 10));
            result.append(toAlphaNumericChar(twoDigitsBits % 10));
        }else if(count == 1){
            if(bits.available() < 4){
                throw  FormatException.getFormatInstance();
            }
            int digitBits = bits.readBits(4);
            if(digitBits > 9){
                throw FormatException.getFormatInstance();
            }
            result.append(toAlphaNumericChar(digitBits));
        }
    }

    public static void decodeAlphanumericSegment(BitSource bits, StringBuilder result, int count) throws FormatException {
        int start = result.length();

        while(count > 1){
            if(bits.available() < 11){
                throw  FormatException.getFormatInstance();
            }

            int nextTwoCharsBits = bits.readBits(11);
            result.append(toAlphaNumericChar(nextTwoCharsBits / 45));
            result.append(toAlphaNumericChar(nextTwoCharsBits % 45));
            count -= 2;
        }
        if(count == 1){
            if(bits.available() < 6){
                throw FormatException.getFormatInstance();
            }
            result.append(toAlphaNumericChar(bits.readBits(6)));
        }
    }


    private static char toAlphaNumericChar(int value) throws FormatException {
        if(value >= ALPHANUMERIC_CHARS.length){
            throw FormatException.getFormatInstance();
        }
        return ALPHANUMERIC_CHARS[value];
    }
}
