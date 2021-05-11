package image;

import common.BitArray;
import common.BitMatrix;
import exception.NotFoundException;

public abstract class Binarizer {
    private final LuminanceSource source;

    protected Binarizer(LuminanceSource source){
        this.source = source;
    }

    public final LuminanceSource getLuminanceSource(){
        return source;
    }

    public abstract BitArray getBlackRow(int y, BitArray row) throws NotFoundException;

    public abstract BitMatrix getBlackMatrix() throws NotFoundException;

    public abstract Binarizer createBinarizer(LuminanceSource source);

    public final int getWidth(){
        return source.getWidth();
    }

    public final int getHeight(){
        return source.getHeight();
    }
}
