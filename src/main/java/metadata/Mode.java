package metadata;

public enum Mode {

    NUMERIC(10, 0x01),
    ALPHANUMERIC(9, 0x02),
    BYTE(8, 0x03),
    TERMINATOR(0, 0x00);


    private final int characterCountBits;
    private final int bits;

    Mode(int characterCountBits, int bits){
        this.characterCountBits = characterCountBits;
        this.bits = bits;
    }

    public static Mode forBits(int bits){
        switch (bits){
            case 0x00:
                return TERMINATOR;
            case 0x01:
                return NUMERIC;
            case 0x02:
                return ALPHANUMERIC;
            case 0x03:
                return BYTE;
            default:
                throw new IllegalArgumentException();
        }
    }

    public int getCharacterCountBits(){
        return this.characterCountBits;
    }

    public int getBits(){
        return bits;
    }

}
