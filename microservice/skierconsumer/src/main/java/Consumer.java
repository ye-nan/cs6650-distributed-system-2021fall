import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import dal.LiftRide;
import dal.LiftRideDao;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

public class Consumer {

    private static final String QUEUE_NAME = "postLiftQ";
    private static final String HOST_NAME = "34.202.92.225";
    private static final int PORT = 5672;
    private static final int NUMTHREADS = 128;

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("admin");
        factory.setPassword("password");
        factory.setVirtualHost("/");
        factory.setHost(HOST_NAME);
        factory.setPort(PORT);
        final Connection connection = factory.newConnection();
        CountDownLatch completed = new CountDownLatch(NUMTHREADS);
        for (int i = 0; i < NUMTHREADS; i++) {
            Runnable runnable = () -> {
                try {
                    final Channel channel = connection.createChannel();
                    // max one message per receiver
                    channel.basicQos(1);
                    System.out.println(" [*] Thread waiting for messages. To exit press CTRL+C");

                    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                        String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                        String[] tokens = message.split(",");
                        int skierId = Integer.parseInt(tokens[0].split("=")[1]);
                        int liftId = Integer.parseInt(tokens[1].split("=")[1]);
                        int resortId = Integer.parseInt(tokens[2].split("=")[1]);
                        String season = tokens[3].split("=")[1];
                        int day = Integer.parseInt(tokens[4].split("=")[1]);
                        int time = Integer.parseInt(tokens[5].split("=")[1]);

                        LiftRideDao dao = new LiftRideDao();
                        dao.createLiftRide(new LiftRide(skierId, liftId, resortId, season, day, time));

                        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    };
                    // process messages
                    channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> { });
                } catch (IOException e) {
                    e.printStackTrace();
                }
                completed.countDown();
            };
            Thread t = new Thread(runnable);
            t.start();
        }
        completed.await();
        System.out.println(NUMTHREADS + " consumers started.");
    }
//public static void main(String[] args) {
//    LiftRideDao dao = new LiftRideDao();
//    dao.createLiftRide(new LiftRide(1, 1, 1, "Spring", 1, 1));
//}
}
