import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class Client {
    private static final String WEB_APP = "/serverDB_war";
//    private static final String WEB_APP = "/serverDB_war_exploded";

    private static final Random rand = new Random();
    private static final String[] seasons = {"Spring", "Summer", "Fall", "Winter"};
    private static final int NUM_RESORTS = 9;   // resort 1 to 9
    private static final int NUM_DAYS = 90;     // number of days per season
    private static final int MAX_VERTICAL = 1000;   // maximum vertical is 1000 feet

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

        String serverURL = "http://" + params.getServer() + WEB_APP + "/skiers";

        stats.setLoadTestStart(System.currentTimeMillis());
        // Phase 1: startup
        System.out.println("Phase1-num of threads: " + params.getNumThreads() / 4);
        System.out.println("Phase1-num of skiers for each thread: " + params.getNumSkiers() / (params.getNumThreads() / 4));
        System.out.println("Phase1-num of requests per thread: " + (int) ((params.getNumRuns() * 0.2) * params.getNumSkiers()) / (params.getNumThreads() / 4));
        System.out.println();

        CountDownLatch latch1 = new CountDownLatch(params.getNumThreads() / 4 / 10 == 0 ? 1 : params.getNumThreads() / 4 / 10);

        for (int i = 0; i < params.getNumThreads() / 4; i++) {
            Thread thread = new Thread(
                    new PostThread(latch1, 1, params.getNumSkiers() / (params.getNumThreads() / 4),
                            (int) ((params.getNumRuns() * 0.2) * params.getNumSkiers()) / (params.getNumThreads() / 4),
                            params.getNumLifts(),
                            1, 90, serverURL, stats,
                            rand.nextInt(NUM_RESORTS) + 1, seasons[rand.nextInt(seasons.length)], rand.nextInt(NUM_DAYS) + 1, rand.nextInt(MAX_VERTICAL) + 1));
            thread.start();
        }

        latch1.await();

        // Phase 2: peak
        System.out.println("Phase2-num of threads: " + params.getNumThreads());
        System.out.println("Phase2-num of skiers for each thread: " + params.getNumSkiers() / params.getNumThreads());
        System.out.println("Phase2-num of requests per thread: " + (int) ((params.getNumRuns() * 0.6) * params.getNumSkiers()) / params.getNumThreads());
        System.out.println();

        CountDownLatch latch2 = new CountDownLatch(params.getNumThreads() / 10);

        for (int i = 0; i < params.getNumThreads(); i++) {
            Thread thread = new Thread(new PostThread(latch2,
                    1, params.getNumSkiers() / params.getNumThreads(),
                    (int) ((params.getNumRuns() * 0.6) * params.getNumSkiers()) / params.getNumThreads(),
                    params.getNumLifts(), 91, 360, serverURL, stats,
                    rand.nextInt(NUM_RESORTS) + 1, seasons[rand.nextInt(seasons.length)], rand.nextInt(NUM_DAYS) + 1, rand.nextInt(MAX_VERTICAL) + 1));
            thread.start();
        }

        latch2.await();

        // Phase 3: cool down
        System.out.println("Phase3-num of threads: " + params.getNumThreads() / 4);
        System.out.println("Phase3-num of skiers for each thread: " + params.getNumSkiers() / (params.getNumThreads() / 4));
        System.out.println("Phase3-num of requests per thread: " + (int) ((params.getNumRuns() * 0.1) * params.getNumSkiers()) / (params.getNumThreads() / 4));
        System.out.println();

        CountDownLatch latch3 = new CountDownLatch(params.getNumThreads() / 4);

        for (int i = 0; i < params.getNumThreads() / 4; i++) {
            Thread thread = new Thread(new PostThread(latch3,
                    1, params.getNumSkiers() / (params.getNumThreads() / 4),
                    (int) ((params.getNumRuns() * 0.1) * params.getNumSkiers()) / (params.getNumThreads() / 4),
                    params.getNumLifts(),
                    361, 420, serverURL, stats,
                    rand.nextInt(NUM_RESORTS) + 1, seasons[rand.nextInt(seasons.length)], rand.nextInt(NUM_DAYS) + 1, rand.nextInt(MAX_VERTICAL) + 1));
            thread.start();
        }

        latch3.await();

        stats.setLoadTestEnd(System.currentTimeMillis());
        System.out.println("Time spent: " + (stats.getLoadTestEnd() - stats.getLoadTestStart()));
        System.out.println("Success requests: " + stats.getNumSuccessReq());
        System.out.println("Failed requests: " + stats.getNumFailReq());

        PerfCalculator.calculate(stats.getFilePath(), stats);
        stats.setThroughput((int)(stats.getNumSuccessReq().getAndAdd(stats.getNumFailReq().intValue()) /
                ((stats.getLoadTestEnd() - stats.getLoadTestStart()) / 1000.0)));
        System.out.println("Mean response Time: " + stats.getMeanResponse());
        System.out.println("Median response Time: " + stats.getMedianResponse());
        System.out.println("P99 response Time: " + stats.getP99());
        System.out.println("Max response Time: " + stats.getMaxResponse());
        System.out.println("Throughput: " + stats.getThroughput() + " requests/s");
    }
}
