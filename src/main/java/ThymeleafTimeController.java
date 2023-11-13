import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.context.Context;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

@WebServlet("/time")
public class ThymeleafTimeController extends HttpServlet {
    private TemplateEngine engine;
    @Override
    public void init() throws SecurityException {
        engine = new TemplateEngine();
        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setPrefix("./templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(engine.getTemplateResolvers().size());
        resolver.setCacheable(false);
        engine.addTemplateResolver(resolver);
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws SecurityException {
        resp.setContentType("text/html");
        String timezone = req.getParameter("timezone");
        if(timezone != null) {
            resp.addCookie(new Cookie("timezone", timezone.replace(" ", "+")));
        }
        if(timezone == null) {
            if(req.getCookies() != null) {
                Cookie[] cookies = req.getCookies();
                for(Cookie cookie : cookies) {
                    timezone = cookie.getValue();
                }
            }else{
                timezone = "UTC";
            }
        }
        String[] parts;
        TimeZone timeZone = TimeZone.getTimeZone("GMT");
        Calendar calendar = Calendar.getInstance(timeZone);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
        dateFormat.setTimeZone(calendar.getTimeZone());
        if(timezone.length() > 4 && timezone.startsWith("UTC") && Character.isDigit(timezone.charAt(4))) {
            parts = timezone.toUpperCase().split("\\s+|\\+");
            calendar.add(Calendar.HOUR, Integer.parseInt(parts[1]));
            timezone = dateFormat.format(calendar.getTime()) + "UTC+" + parts[1];
        }else{
            timezone = dateFormat.format(calendar.getTime()) + "UTC";
        }
        Context context = new Context();
        context.setVariable("time", timezone);
        try {
            engine.process("time", context, resp.getWriter());
            resp.getWriter().close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
