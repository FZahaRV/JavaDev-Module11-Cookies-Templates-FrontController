import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/time")
public class TimezoneValidateFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) throws IOException, ServletException {
        String timezone = req.getParameter("timezone");
        if(timezone == null) {
            chain.doFilter(req,resp);
        } else if(timezone.equals("UTC") || (timezone.startsWith("UTC") && timezone.length() > 4 && Integer.parseInt(timezone.replaceAll("[^0-9]", "")) < 24)) {
            chain.doFilter(req,resp);
        } else {
            resp.setStatus(400);
            resp.getWriter().write("Invalid timezone, HTTP code 400");
            resp.getWriter().close();
        }
    }
}
