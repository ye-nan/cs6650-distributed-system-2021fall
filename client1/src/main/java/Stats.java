import java.util.concurrent.atomic.AtomicInteger;

public class Stats {

    private AtomicInteger numSuccessReq;
    private AtomicInteger numFailReq;
    private long loadTestStart;
    private long loadTestEnd;

    public Stats(AtomicInteger numSuccessReq, AtomicInteger numFailReq, long loadTestStart, long loadTestEnd) {
        this.numSuccessReq = numSuccessReq;
        this.numFailReq = numFailReq;
        this.loadTestStart = loadTestStart;
        this.loadTestEnd = loadTestEnd;
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

    public void setLoadTestStart(long loadTestStart) {
        this.loadTestStart = loadTestStart;
    }

    public void setLoadTestEnd(long loadTestEnd) {
        this.loadTestEnd = loadTestEnd;
    }
}
