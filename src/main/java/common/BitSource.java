package common;

public class BitSource {
    private final byte[] bytes;
    private int byteOffset;
    private int bitOffset;

    public BitSource(byte[] bytes){
        this.bytes = bytes;
    }


    // 读取指定个数的bits
    public int readBits(int numBits){
        if(numBits < 1 || numBits > 32 || numBits > available()){
            throw new IllegalArgumentException(String.valueOf(numBits));
        }

        int result = 0;

        // 读取之前剩下的bit
        if(bitOffset > 0){
            int bitsLeft = 8 - bitOffset;
            int toRead = Math.min(numBits, bitsLeft);

            int bitsToNotRead = bitsLeft - toRead;
            int mask = (0xFF >> (8 - toRead)) << bitsToNotRead;
            result = (bytes[byteOffset] & mask) >> bitsToNotRead;
            numBits -= toRead;
            bitOffset += toRead;
            if(bitOffset == 8){
                bitOffset = 0;
                byteOffset++;
            }
        }

        // 如果还有的话，继续读取下一个byte的bit
        if(numBits > 0){
            while(numBits >= 8){
                result = (result << 8) | (bytes[byteOffset] & 0xff);
                byteOffset++;
                numBits -= 8;
            }

            if(numBits > 0){
                int bitsToNotRead = 8 - numBits;
                int mask = (0xff >> bitsToNotRead) << bitsToNotRead;
                result = (result << numBits) | ((bytes[byteOffset] & mask) >> bitsToNotRead);
                bitOffset += numBits;
            }
        }

        return result;
    }

    // 剩下的bit数
    public int available(){
        return 8 * (bytes.length - byteOffset) - bitOffset;
    }
}
