import code.CodeReader;
import code.Reader;
import code.Result;
import exception.ReaderException;
import image.BinaryBitmap;
import image.BufferedImageLuminanceSource;
import image.HybridBinarizer;
import image.LuminanceSource;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Logger;

public class BlackBoxText {
    private static final Logger log = Logger.getLogger(BlackBoxText.class.getSimpleName());

//    private final Path testBase;
    private Reader barcodeReader;

//    public BlackBoxText(String testBasePathSuffix,
//                                       Reader barcodeReader) {
////        this.testBase = buildTestBase(testBasePathSuffix);
//        this.barcodeReader = barcodeReader;
////        testResults = new ArrayList<>();
//
//        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");
//    }

    @Test
    public void testBlackBox() throws IOException {
        this.barcodeReader = new CodeReader();
        Path testImage = Paths.get("F://test4.jpg");
        log.info(String.format("Starting %s", testImage));
        BufferedImage image = ImageIO.read(testImage.toFile());
        if (image == null) {
            throw new IOException("Could not read image: " + testImage);
        }

        LuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        Result result;

        try {
            result = getReader().decode(bitmap);
//            log.info(String.format("Found false positive: '%s' )",
//                    result.getText()));
//            return;
        } catch (ReaderException re) {
            System.out.println("wrong!");
            // continue
        }
    }

    final Reader getReader() {
        return barcodeReader;
    }
}
