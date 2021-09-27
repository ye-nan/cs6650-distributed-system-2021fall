import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

public class Stats {

    private AtomicInteger numSuccessReq;
    private AtomicInteger numFailReq;
    private long loadTestStart;
    private long loadTestEnd;
    private Path filePath;

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

    public void setLoadTestStart(long loadTestStart) {
        this.loadTestStart = loadTestStart;
    }

    public void setLoadTestEnd(long loadTestEnd) {
        this.loadTestEnd = loadTestEnd;
    }
}
