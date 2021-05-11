package metadata;

public final class Version {
    private final int versionNumber;
    public static int totalCodewords;


    private int[][] codeword;
    private Version(int versionNumber, int[][] codewords){
        this.versionNumber = versionNumber;
        this.codeword = codewords;

        totalCodewords = codewords[2][0] + codewords[2][1];
    }

    public int[][] getCodeword() {
        return codeword;
    }


    private static final Version[] VERSIONS = buildVersions();

    private static Version[] buildVersions(){
        int[][] arr1 = new int[][]{{72, 36}, {72, 36},{72, 36},{72, 36}};// 版本1的L、M、Q、H
        return new Version[]{
          new Version(1, arr1)
        };
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    public int getTotalCodewords() {
        return totalCodewords;
    }


    public int getDimensionForVersion(){
        return 74;
    }

    public static Version getVersionForNumber(int versionNumber){
        if(versionNumber < 1 || versionNumber > VERSIONS.length){
            throw new IllegalArgumentException();
        }

        return VERSIONS[versionNumber - 1];
    }

    public String toString(){
        return versionNumber + "";
    }
}
