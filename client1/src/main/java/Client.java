import org.apache.commons.cli.ParseException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class Client {
    private static final int SKIDAY = 420;  // 420 mins from 9am - 4pm
    private static final String WEB_APP = "/server_war";

    public static void main(String[] args) throws ParseException, InterruptedException {
        CommandLineParser parser = new CommandLineParser();
        InputParams params = parser.parse(args);
        System.out.println(params);

        Stats stats = new Stats(
                new AtomicInteger(0),
                new AtomicInteger(0),
                0, 0);
        stats.setLoadTestStart(System.currentTimeMillis());

        // dummy data for assignment 1
        int resortId = 1;
        String seasonId = "2021";
        String dayId = "1";
        String serverURL = "http://" + params.getServer() + WEB_APP + "/skiers";

        // Phase 1: startup
        System.out.println("Phase1-num of threads: " + params.getNumThreads() / 4);
        System.out.println("Phase1-num of skiers for each thread: " + params.getNumSkiers() / (params.getNumThreads() / 4));
        System.out.println("Phase1-num of requests per thread: " + (int) (params.getNumRuns() * 0.2) * (params.getNumSkiers() / (params.getNumThreads() / 4)));
        System.out.println();

        CountDownLatch latch1 = new CountDownLatch(params.getNumThreads() / 4 / 10);

        for (int i = 0; i < params.getNumThreads() / 4; i++) {
            Thread thread = new Thread(
                    new PostThread(latch1, 1, params.getNumSkiers() / (params.getNumThreads() / 4),
                            (int) (params.getNumRuns() * 0.2) * (params.getNumSkiers() / (params.getNumThreads() / 4)),
                            params.getNumLifts(),
                            1, 90, serverURL, stats,
                    resortId, seasonId, dayId));
            thread.start();
        }

        latch1.await();

        // Phase 2: peak
        System.out.println("Phase2-num of threads: " + params.getNumThreads());
        System.out.println("Phase2-num of skiers for each thread: " + params.getNumSkiers() / params.getNumThreads());
        System.out.println("Phase2-num of requests per thread: " + (int) (params.getNumRuns() * 0.6) * (params.getNumSkiers() / params.getNumThreads()));
        System.out.println();

        CountDownLatch latch2 = new CountDownLatch(params.getNumThreads() / 10);

        for (int i = 0; i < params.getNumThreads(); i++) {
            Thread thread = new Thread(new PostThread(latch2,
                    1, params.getNumSkiers() / params.getNumThreads(),
                    (int) (params.getNumRuns() * 0.6) * (params.getNumSkiers() / params.getNumThreads()),
                    params.getNumLifts(), 91, 360, serverURL, stats,
                    resortId, seasonId, dayId));
            thread.start();
        }

        latch2.await();

        // Phase 3: cool down
        System.out.println("Phase3-num of threads: " + params.getNumThreads() / 4);
        System.out.println("Phase3-num of skiers for each thread: " + params.getNumSkiers() / (params.getNumThreads() / 4));
        System.out.println("Phase3-num of requests per thread: " + (int) (params.getNumRuns() * 0.1) * (params.getNumSkiers() / (params.getNumThreads() / 4)));
        System.out.println();

        CountDownLatch latch3 = new CountDownLatch(params.getNumThreads() / 4);

        for (int i = 0; i < params.getNumThreads() / 4; i++) {
            Thread thread = new Thread(new PostThread(latch3,
                    1, params.getNumSkiers() / (params.getNumThreads() / 4),
                    (int) (params.getNumRuns() * 0.1) * (params.getNumSkiers() / (params.getNumThreads() / 4)),
                    params.getNumLifts(),
                    361, 420, serverURL, stats,
                    resortId, seasonId, dayId));
            thread.start();
        }

        latch3.await();

        stats.setLoadTestEnd(System.currentTimeMillis());
        System.out.println("Time spent: " + (stats.getLoadTestEnd() - stats.getLoadTestStart()));
        System.out.println("Success requests: " + stats.getNumSuccessReq());
        System.out.println("Failed requests: " + stats.getNumFailReq());
    }
}
