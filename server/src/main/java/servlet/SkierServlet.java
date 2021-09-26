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

@WebServlet(name = "SkierServlet")
public class SkierServlet extends HttpServlet {
    private final Gson gson  = new Gson();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
                        urlParts[5],
                        -1)));
            }
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
            LiftEvent lift = gson.fromJson(request.getReader(), LiftEvent.class);
            response.getWriter().write((gson.toJson(lift)));
            response.setStatus(HttpServletResponse.SC_CREATED);
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
                        && urlPath[4].equals("day")
                        && urlPath[6].equals("skier"));
            } catch (Exception e) {
                return false;
            }
        } else {
            return false;
        }
    }
}
