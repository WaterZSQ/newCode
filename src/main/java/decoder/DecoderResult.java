package decoder;

import java.util.Arrays;
import java.util.List;

public class DecoderResult {
    private final byte[] rawBytes; // 未加工的byte[]
    private final String text;
    private final List<byte[]> byteSegments;

    public DecoderResult(byte[] rawBytes, String text, List<byte[]> byteSegments) {
        this.rawBytes = rawBytes;
        this.text = text;
        this.byteSegments = byteSegments;
    }

    public byte[] getRawBytes() {
        return rawBytes;
    }

    public String getText() {
        return text;
    }

    public List<byte[]> getByteSegments() {
        return byteSegments;
    }

    @Override
    public String toString() {
        return "DecoderResult{" +
//                "rawBytes=" + Arrays.toString(rawBytes) +
                ", text='" + text + '\'' +
                '}';
    }
}
