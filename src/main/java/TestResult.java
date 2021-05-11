public class TestResult {
    private final int mustPassCount;
    private final int tryHarderCount;
    private final int maxMisreads;
    private final int maxTryHarderMisreads;
    private final float rotation;

    public int getMustPassCount() {
        return mustPassCount;
    }

    public int getTryHarderCount() {
        return tryHarderCount;
    }

    public int getMaxMisreads() {
        return maxMisreads;
    }

    public int getMaxTryHarderMisreads() {
        return maxTryHarderMisreads;
    }

    public float getRotation() {
        return rotation;
    }

    public TestResult(int mustPassCount, int tryHarderCount, int maxMisreads, int maxTryHarderMisreads, float rotation) {
        this.mustPassCount = mustPassCount;
        this.tryHarderCount = tryHarderCount;
        this.maxMisreads = maxMisreads;
        this.maxTryHarderMisreads = maxTryHarderMisreads;
        this.rotation = rotation;
    }
}
