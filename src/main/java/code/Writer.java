package code;

import common.BitMatrix;
import exception.WriterException;

public interface Writer {

    public BitMatrix encode(String contents, int width, int height) throws WriterException;
}
