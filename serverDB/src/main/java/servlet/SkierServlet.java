package servlet;

import com.google.gson.Gson;

import dal.SkierDao;
import dal.SkierDataSource;
import model.LiftRide;
import model.Message;

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
    private static final String RMQ_EXCHANGE_NAME = "LiftRide";
    private static final String RMQ_HOST_NAME = "34.202.92.225";
    private static final String RMQ_USERNAME = System.getProperty("RMQ_USERNAME");
    private static final String RMQ_PASSWORD = System.getProperty("RMQ_PASSWORD");
//    private static final String HOST_NAME = "localhost";
    private static final int NUM_CHANNELS = 128;
    private static final int PORT = 5672;
    private static Connection conn;
    private static Gson gson ;

    private static ObjectPool<Channel> channelPool;
//    private static SkierDao dao;

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
        factory.setUsername(RMQ_USERNAME);
        factory.setPassword(RMQ_PASSWORD);
        factory.setVirtualHost("/");
        factory.setHost(RMQ_HOST_NAME);
        factory.setPort(PORT);
        try {
            conn = factory.newConnection();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
//        SkierDataSource.getDataSource();
//        dao = new SkierDao();
        GenericObjectPoolConfig<Channel> config = new GenericObjectPoolConfig<>();
        config.setMaxTotal(NUM_CHANNELS);
        config.setMaxIdle(NUM_CHANNELS);
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
            response.setStatus(HttpServletResponse.SC_OK);
            if (urlParts[2].equals("vertical")) {
                int skierId = Integer.parseInt(urlParts[1]);
                SkierDao dao = new SkierDao();
                int totalVert = dao.getTotalVertical(skierId);
                response.getWriter().write("Total vertical for Skier " + skierId
                        + " is " + totalVert + " m.");
            } else {
                int resortId = Integer.parseInt(urlParts[1]);
                String season = urlParts[3];
                int day = Integer.parseInt(urlParts[5]);
                int skierId = Integer.parseInt(urlParts[7]);
                SkierDao dao = new SkierDao();
                int dayVert = dao.getDayVertical(resortId, season, day, skierId);
                response.getWriter().write("Ski day " + day
                        + " vertical for Skier" + skierId
                        + " for Season " + season
                        + " at Resort " + resortId
                        + " is " + dayVert + " m.");
            }
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
                channel.basicPublish(RMQ_EXCHANGE_NAME, "", null,
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
            // e.g., urlPath  = "/1/seasons/Spring/day/1/skier/123"
            try {
                return (Integer.parseInt(urlPath[5]) >= 1
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
