package tc.you.example;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NoopServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
  
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        // Return success
        resp.setContentType("application/json");
        PrintWriter pw = resp.getWriter();
        pw.write("{\"status\": \"available\"}");
    }
}
