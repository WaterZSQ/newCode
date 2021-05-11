package metadata;

public enum ErrorCorrectionLevel {

    L(0x00),
    M(0x01),
    Q(0x02),
    H(0x03);


    private static final ErrorCorrectionLevel[] EC_LEVEL = {L, M, Q, H};
    private final int bits;
    ErrorCorrectionLevel(int bits){
        this.bits = bits;
    }

    public int getBits(){
        return bits;
    }

    public static ErrorCorrectionLevel forBits(int bits){
        if(bits < 0 || bits >= EC_LEVEL.length){
            throw new IllegalArgumentException();
        }

        return EC_LEVEL[bits];
    }
}
