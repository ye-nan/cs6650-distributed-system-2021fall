import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class Client {
    private static final int SKIDAY = 420;  // 420 mins from 9am - 4pm

    public static void main(String[] args) throws ParseException, InterruptedException, IOException {
        CommandLineParser parser = new CommandLineParser();
        InputParams params = parser.parse(args);
        System.out.println(params);

        Stats stats = new Stats(
                new AtomicInteger(0),
                new AtomicInteger(0),
                0, 0,
                Paths.get("src", "main", "resources", "record.csv"));
        Files.newBufferedWriter(stats.getFilePath() , StandardOpenOption.TRUNCATE_EXISTING);

        // dummy data for assignment 1
        int resortId = 1;
        String seasonId = "2021";
        String dayId = "1";
        String serverURL = "http://" + params.getServer() + "/server_war_exploded/skiers";

        stats.setLoadTestStart(System.currentTimeMillis());
        // Phase 1: startup
        int numThreads = params.getNumThreads() / 4;
        int numSkiersEachThread = params.getNumSkiers() / numThreads;
        int startTime = 1;
        int endTime = 90;
        int numRequests = (int) (params.getNumRuns() * 0.2) * numSkiersEachThread;

        System.out.println("Phase1-num of threads: " + numThreads);
        System.out.println("Phase1-num of skiers for each thread: " + numSkiersEachThread);
        System.out.println("Phase1-num of requests per thread: " + numRequests);
        System.out.println();

        CountDownLatch latch = new CountDownLatch(numThreads / 10);

        for (int i = 0; i < numThreads; i++) {
            Thread thread = new Thread(new PostThread(latch, 1, numSkiersEachThread,
                    numRequests, params.getNumLifts(), startTime, endTime, serverURL, stats,
                    resortId, seasonId, dayId));
            thread.start();
        }

        latch.await();

        // Phase 2: peak
        numThreads = params.getNumThreads();
        numSkiersEachThread = params.getNumSkiers() / numThreads;
        startTime = 91;
        endTime = 360;
        numRequests = (int) (params.getNumRuns() * 0.6) * numSkiersEachThread;

        System.out.println("Phase2-num of threads: " + numThreads);
        System.out.println("Phase2-num of skiers for each thread: " + numSkiersEachThread);
        System.out.println("Phase2-num of requests per thread: " + numRequests);
        System.out.println();

        latch = new CountDownLatch(numThreads / 10);

        for (int i = 0; i < numThreads; i++) {
            Thread thread = new Thread(new PostThread(latch, 1, numSkiersEachThread,
                    numRequests, params.getNumLifts(), startTime, endTime, serverURL, stats,
                    resortId, seasonId, dayId));
            thread.start();
        }

        latch.await();

        // Phase 3: cool down
        numThreads = params.getNumThreads() / 4;
        numSkiersEachThread = params.getNumSkiers() / numThreads;
        startTime = 361;
        endTime = 420;
        numRequests = (int) (params.getNumRuns() * 0.1) * numSkiersEachThread;

        System.out.println("Phase3-num of threads: " + numThreads);
        System.out.println("Phase3-num of skiers for each thread: " + numSkiersEachThread);
        System.out.println("Phase3-num of requests per thread: " + numRequests);
        System.out.println();

        latch = new CountDownLatch(numThreads);

        for (int i = 0; i < numThreads; i++) {
            Thread thread = new Thread(new PostThread(latch, 1, numSkiersEachThread,
                    numRequests, params.getNumLifts(), startTime, endTime, serverURL, stats,
                    resortId, seasonId, dayId));
            thread.start();
        }

        latch.await();

        stats.setLoadTestEnd(System.currentTimeMillis());
        System.out.println("Time spent: " + (stats.getLoadTestEnd() - stats.getLoadTestStart()));
        System.out.println("Success requests: " + stats.getNumSuccessReq());
        System.out.println("Failed requests: " + stats.getNumFailReq());

        PerfCalculator.calculate(stats.getFilePath(), stats);
        stats.setThroughput((stats.getNumSuccessReq().getAndAdd(stats.getNumFailReq().intValue())) /
                (int)(stats.getLoadTestEnd() - stats.getLoadTestStart()));
        System.out.println("Mean response Time: " + stats.getMeanResponse());
        System.out.println("Median response Time: " + stats.getMedianResponse());
        System.out.println("P99 response Time: " + stats.getP99());
        System.out.println("Max response Time: " + stats.getMaxResponse());
        System.out.println("Throughput: " + stats.getThroughput());
    }
}
