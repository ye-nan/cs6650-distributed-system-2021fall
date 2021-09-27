import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PerfCalculator {
    public static void calculate(Path filePath, Stats stats) {
        try (BufferedReader reader = Files.newBufferedReader(stats.getFilePath())) {
            int totalLatency = 0;
            List<Long> latencies = new ArrayList<>();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                long startTime = Long.parseLong(fields[0]);
                String method = fields[1];
                long latency = Long.parseLong(fields[2]);
                String statusCode = fields[3];

                totalLatency += latency;
                latencies.add(latency);
            }
            Collections.sort(latencies);
            stats.setMeanResponse(totalLatency / (double) latencies.size());
            stats.setMaxResponse(latencies.get(latencies.size() - 1));
            stats.setP99(latencies.get((int) (0.99 * (latencies.size() - 1))));
            stats.setMedianResponse(
                    latencies.size() % 2 != 0 ?
                        (double)latencies.get(latencies.size() / 2)
                        :
                        (latencies.get((latencies.size() - 1) / 2) + latencies.get(latencies.size() / 2)) / 2.0);
        } catch (IOException e) {
            e.printStackTrace();;
        }
    }
}
