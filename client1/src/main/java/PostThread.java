import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
    private Random rand;
    private int resortId;
    private String seasonId;
    private String dayId;

    private static final int RETRIES = 5;   // max number of retries
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public PostThread(CountDownLatch latch, int startSkier, int endSkier,
                      int numRequests, int numLifts, int startTime, int endTime,
                      String serverURL, Stats stats, int resortId, String seasonId, String dayId) {
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
        this.seasonId = seasonId;
        this.dayId = dayId;
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
                    .append("/seasons/").append(seasonId)
                    .append("/days/").append(dayId)
                    .append("/skiers/").append(skierId).toString();

            String postBody = new StringBuilder()
                    .append("{")
                    .append("\"liftId\":\"").append(liftId).append("\",")
                    .append("\"time\":\"").append(time).append("\"")
                    .append("}").toString();

            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(postBody))
                    .uri(URI.create(url))
                    .setHeader("User-Agent", "Java 11 HttpClient Bot") // add request header
                    .header("Content-Type", "application/json")
                    .build();

            try {
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
