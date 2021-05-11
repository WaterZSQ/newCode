package decoder;

import metadata.ErrorCorrectionLevel;
import metadata.Version;

public class FormatInformation {
    private Version version;
    private ErrorCorrectionLevel ecLevel;

    public FormatInformation(Version version, ErrorCorrectionLevel ecLevel) {
        this.version = version;
        this.ecLevel = ecLevel;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public ErrorCorrectionLevel getEcLevel() {
        return ecLevel;
    }

    public void setEcLevel(ErrorCorrectionLevel ecLevel) {
        this.ecLevel = ecLevel;
    }
}
