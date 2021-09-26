public class InputParams {
    private int numThreads;
    private int numSkiers;
    private int numLifts;
    private int numRuns;
    private String server;

    public InputParams(int numThreads, int numSkiers, int numLifts, int numRuns, String server) {
        this.numThreads = numThreads;
        this.numSkiers = numSkiers;
        this.numLifts = numLifts;
        this.numRuns = numRuns;
        this.server = server;
    }

    public int getNumThreads() {
        return numThreads;
    }

    public int getNumSkiers() {
        return numSkiers;
    }

    public int getNumLifts() {
        return numLifts;
    }

    public int getNumRuns() {
        return numRuns;
    }

    public String getServer() {
        return server;
    }

    @Override
    public String toString() {
        return "InputParams{" +
                "numThreads=" + numThreads +
                ", numSkiers=" + numSkiers +
                ", numLifts=" + numLifts +
                ", numRuns=" + numRuns +
                ", server='" + server + '\'' +
                '}';
    }
}
