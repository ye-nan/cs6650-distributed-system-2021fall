import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class Consumer {

    private static final String QUEUE_NAME = "postLiftQ";
    private static final String HOST_NAME = "localhost";
    private static final int NUMTHREADS = 4;

    private static class LiftEvent {
        int time;
        int liftId;
        LiftEvent(int time, int liftId) {
            this.time = time;
            this.liftId = liftId;
        }
    }
    private static final Map<Integer, List<LiftEvent>> map = new ConcurrentHashMap<>();

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST_NAME);
        final Connection connection = factory.newConnection();
        CountDownLatch completed = new CountDownLatch(NUMTHREADS);
        for (int i = 0; i < NUMTHREADS; i++) {
            Runnable runnable = () -> {
                try {
                    final Channel channel = connection.createChannel();
                    channel.queueDeclare(QUEUE_NAME, false, false, false, null);
                    // max one message per receiver
                    channel.basicQos(1);
                    System.out.println(" [*] Thread waiting for messages. To exit press CTRL+C");

                    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                        String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                        String[] tokens = message.split(",");
                        int skierId = Integer.parseInt(tokens[0]);
                        int time = Integer.parseInt(tokens[1]);
                        int liftId = Integer.parseInt(tokens[2]);
                        map.putIfAbsent(skierId, new ArrayList<>());
                        map.get(skierId).add(new LiftEvent(time, liftId));
                        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    };
                    // process messages
                    channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> { });
                    } catch (IOException e) {
                        e.printStackTrace();
                }
            };
            Thread t = new Thread(runnable);
            t.start();
            completed.countDown();
        }
        completed.await();
        System.out.println(NUMTHREADS + " consumers started.");
    }
}