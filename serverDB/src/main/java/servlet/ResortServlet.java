package servlet;

import com.google.gson.Gson;
import model.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

@WebServlet(name = "ResortServlet")
public class ResortServlet extends HttpServlet {
    private final Gson gson  = new Gson();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String urlPath = request.getPathInfo();

        if (urlPath == null || urlPath.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            Message message = new Message("Invalid URL.");
            response.getWriter().write(gson.toJson(message));
            return;
        }

        String[] urlParts = urlPath.split("/");
        if (!isUrlValid(urlParts)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            Message message = new Message("Invalid URL.");
            response.getWriter().write(gson.toJson(message));
        } else {
            response.getWriter().write(gson.toJson(request.getReader().lines().collect(Collectors.joining())));
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String urlPath = request.getPathInfo();

        // check we have a URL!
        if (urlPath == null || urlPath.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_OK);
            ResortList resorts = new ResortList(Arrays.asList(1, 2, 3));
            response.getWriter().write(gson.toJson(resorts));
            return;
        }

        String[] urlParts = urlPath.split("/");

        if (!isUrlValid(urlParts)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            Message message = new Message("Invalid url.");
            response.getWriter().write(gson.toJson(message));
            return;
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(gson.toJson(new Resort(
                    Integer.parseInt(urlParts[1]),
                    "dummy resort",
                    Arrays.asList("2012", "2014", "2020"))));
        }
    }

    private boolean isUrlValid(String[] urlPath) {
        if (urlPath.length == 1) {
            // e.g., /resorts
            return true;
        }
        if (urlPath.length == 3) {
            try {
                Integer.parseInt(urlPath[1]);
                return urlPath[2].equals("seasons");
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }
}
