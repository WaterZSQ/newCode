package code;

import exception.ChecksumException;
import exception.FormatException;
import exception.NotFoundException;
import image.BinaryBitmap;

public interface Reader {

    Result decode(BinaryBitmap image) throws NotFoundException, ChecksumException, FormatException;

    void reset();
}
