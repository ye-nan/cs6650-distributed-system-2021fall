import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

public class Stats {

    private AtomicInteger numSuccessReq;
    private AtomicInteger numFailReq;
    private long loadTestStart;
    private long loadTestEnd;
    private Path filePath;

    private double meanResponse;
    private double medianResponse;
    private int throughput;
    private long p99;
    private long maxResponse;

    public Stats(AtomicInteger numSuccessReq, AtomicInteger numFailReq,
                 long loadTestStart, long loadTestEnd, Path filePath) {
        this.numSuccessReq = numSuccessReq;
        this.numFailReq = numFailReq;
        this.loadTestStart = loadTestStart;
        this.loadTestEnd = loadTestEnd;
        this.filePath = filePath;
    }

    public AtomicInteger getNumSuccessReq() {
        return numSuccessReq;
    }

    public AtomicInteger getNumFailReq() {
        return numFailReq;
    }

    public long getLoadTestStart() {
        return loadTestStart;
    }

    public long getLoadTestEnd() {
        return loadTestEnd;
    }

    public Path getFilePath() {
        return filePath;
    }

    public double getMeanResponse() {
        return meanResponse;
    }

    public double getMedianResponse() {
        return medianResponse;
    }

    public int getThroughput() {
        return throughput;
    }

    public long getP99() {
        return p99;
    }

    public long getMaxResponse() {
        return maxResponse;
    }

    public void setLoadTestStart(long loadTestStart) {
        this.loadTestStart = loadTestStart;
    }

    public void setLoadTestEnd(long loadTestEnd) {
        this.loadTestEnd = loadTestEnd;
    }

    public void setMeanResponse(double meanResponse) {
        this.meanResponse = meanResponse;
    }

    public void setMedianResponse(double medianResponse) {
        this.medianResponse = medianResponse;
    }

    public void setThroughput(int throughput) {
        this.throughput = throughput;
    }

    public void setP99(long p99) {
        this.p99 = p99;
    }

    public void setMaxResponse(long maxResponse) {
        this.maxResponse = maxResponse;
    }
}
