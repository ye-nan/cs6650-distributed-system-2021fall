import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

class PostThread implements Runnable {

    private final CountDownLatch latch;

    private final int startSkier;
    private final int endSkier;
    private final int numRequests;
    private final int numLifts;
    private final int startTime;
    private final int endTime;
    private final String serverURL;
    private final Stats stats;
    private final Random rand;
    private final int resortId;
    private final String season;
    private final int day;
    private final int vertical;

    private static final int RETRIES = 5;   // max number of retries
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public PostThread(CountDownLatch latch, int startSkier, int endSkier,
                      int numRequests, int numLifts, int startTime, int endTime,
                      String serverURL, Stats stats, int resortId, String season, int day, int vertical) {
        this.latch = latch;
        this.startSkier = startSkier;
        this.endSkier = endSkier;
        this.numRequests = numRequests;
        this.numLifts = numLifts;
        this.startTime = startTime;
        this.endTime = endTime;
        this.serverURL = serverURL;
        this.stats = stats;
        this.resortId = resortId;
        this.season = season;
        this.day = day;
        this.vertical = vertical;
        this.rand = new Random();
    }

    @Override
    public void run() {
        for (int i = 0; i < numRequests; i++) {
            int skierId = startSkier + rand.nextInt(endSkier);
            int liftId = 1 + rand.nextInt(numLifts);
            int time = startTime + rand.nextInt(endTime);

            // /10/seasons/2016/day/363/skier/2
            String url = new StringBuilder(serverURL)
                    .append("/").append(resortId)
                    .append("/seasons/").append(season)
                    .append("/days/").append(day)
                    .append("/skiers/").append(skierId).toString();

            String postBody = new StringBuilder()
                    .append("{")
                    .append("\"skierId\": ").append(skierId).append(",")
                    .append("\"liftId\": ").append(liftId).append(",")
                    .append("\"resortId\": ").append(resortId).append(",")
                    .append("\"season\": \"").append(season).append("\",")
                    .append("\"day\": ").append(day).append(",")
                    .append("\"time\": ").append(time).append(",")
                    .append("\"vertical\": ").append(vertical)
                    .append("}").toString();

            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(postBody))
                    .uri(URI.create(url))
                    .setHeader("User-Agent", "Java 11 HttpClient Bot") // add request header
                    .header("Content-Type", "application/json")
                    .build();

            try {
                long startRequest = System.currentTimeMillis();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (is2xxSuccessful(response.statusCode())) {
                    stats.getNumSuccessReq().getAndIncrement();
                } else if (is4xxClientError(response.statusCode()) || is5xxServerError(response.statusCode())) {
                    int retry = 0;
                    while (retry < RETRIES) {
                        if (is2xxSuccessful(response.statusCode())) {
                            stats.getNumSuccessReq().getAndIncrement();
                            break;
                        }
                        retry++;
                    }
                    if (retry == RETRIES)
                        stats.getNumFailReq().getAndIncrement();
                }
                long endRequest = System.currentTimeMillis();
                long latency = endRequest - startRequest;
                try (BufferedWriter bufferedWriter = Files.newBufferedWriter(
                        stats.getFilePath(),
                        StandardCharsets.UTF_8,
                        StandardOpenOption.APPEND)) {
                    bufferedWriter.write(
                            startRequest + ",POST," + latency + "," + response.statusCode() + "\n");
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }

        try {
            this.latch.countDown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean is2xxSuccessful(int statusCode) {
        return 200 <= statusCode && statusCode < 300;
    }

    private boolean is4xxClientError(int statusCode) {
        return 400 <= statusCode && statusCode < 500;
    }

    private boolean is5xxServerError(int statusCode) {
        return 500 <= statusCode && statusCode < 600;
    }
}
