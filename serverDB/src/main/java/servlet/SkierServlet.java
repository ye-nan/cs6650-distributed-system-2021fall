package servlet;

import com.google.gson.Gson;
import model.LiftEvent;
import model.LiftRide;
import model.Message;
import model.SkierVertical;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

@WebServlet(name = "SkierServlet")
public class SkierServlet extends HttpServlet {
    private static final String POST_LIFT_QUEUE_NAME = "postLiftQ";
//    private static final String HOST_NAME = "34.202.92.225";
    private static final String HOST_NAME = "localhost";
    private static final int PORT = 5672;
    private static Connection conn;
    private static Gson gson ;

    private static ObjectPool<Channel> channelPool;

    private static class ChannelFactory extends BasePooledObjectFactory<Channel> {
        @Override
        public Channel create() throws IOException {
            return conn.createChannel();
        }

        @Override
        public PooledObject<Channel> wrap(Channel channel) {
            return new DefaultPooledObject<>(channel);
        }
    }


    @Override
    public void init() {
        gson = new Gson();
        ConnectionFactory factory = new ConnectionFactory();
//        factory.setUsername("admin");
//        factory.setPassword("password");
//        factory.setVirtualHost("/");
        factory.setHost(HOST_NAME);
//        factory.setPort(PORT);
        try {
            conn = factory.newConnection();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
        GenericObjectPoolConfig<Channel> config = new GenericObjectPoolConfig<>();
        config.setMaxTotal(128);
        config.setMaxIdle(128);
        channelPool = new GenericObjectPool<>(new ChannelFactory(), config);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String urlPath = request.getPathInfo();

        // check we have a URL!
        if (urlPath == null || urlPath.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("missing paramterers");
            return;
        }

        String[] urlParts = urlPath.split("/");

        if (!isUrlValid(urlParts)) {
            Message message = new Message("Invalid URL.");
            response.getWriter().write(gson.toJson(message));
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            if (urlParts[2].equals("vertical")) {
                SkierVertical skierVertical = new SkierVertical(Integer.parseInt(urlParts[1]), -1, "Spring", 99);
                response.getWriter().write(gson.toJson(skierVertical));
            } else {
                response.getWriter().write(gson.toJson(new LiftRide(
                        Integer.parseInt(urlParts[7]),
                        -1,
                        Integer.parseInt(urlParts[1]),
                        urlParts[3],
                        Integer.parseInt(urlParts[5]),
                        -1)));
            }
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String urlPath = request.getPathInfo();

        // check we have a URL!
        if (urlPath == null || urlPath.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("missing parameters");
            return;
        }

        String[] urlParts = urlPath.split("/");
        // and now validate url path and return the response status code
        // (and maybe also some value if input is valid)
        if (!isUrlValid(urlParts)) {
            Message message = new Message("Invalid URL.");
            response.getWriter().write(gson.toJson(message));
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            try {
                Channel channel = channelPool.borrowObject();
                LiftRide liftRide = gson.fromJson(request.getReader(), LiftRide.class);
                channel.basicPublish("", POST_LIFT_QUEUE_NAME, null,
                        liftRide.toString().getBytes(StandardCharsets.UTF_8));
                channelPool.returnObject(channel);
                response.getWriter().write((gson.toJson(liftRide)));
                response.setStatus(HttpServletResponse.SC_CREATED);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void destroy() {
        if (conn != null)
            try {
                conn.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    private boolean isUrlValid(String[] urlPath) {

        if (urlPath.length == 3) {
            // e.g., urlPath  = "/1/vertical"
            try {
                Integer.parseInt(urlPath[1]);
                return urlPath[2].equals("vertical");
            } catch (Exception e) {
                return false;
            }
        } else if (urlPath.length == 8) {
            // e.g., urlPath  = "/1/seasons/2019/day/1/skier/123"
            try {
                for (int i = 1; i < urlPath.length; i += 2) {
                    Integer.parseInt(urlPath[i]);
                }

                return (urlPath[3].length() == 4
                        && Integer.parseInt(urlPath[5]) >= 1
                        && Integer.parseInt(urlPath[5]) <= 365
                        && urlPath[2].equals("seasons")
                        && urlPath[4].equals("days")
                        && urlPath[6].equals("skiers"));
            } catch (Exception e) {
                return false;
            }
        } else {
            return false;
        }
    }
}
