import javax.servlet.ServletConfig
import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

public class RssServlet extends HttpServlet {

    VkWallParser vkWallParser

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        getServletContext().log("init() called");
        vkWallParser = new VkWallParser()
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("application/rss+xml;charset=UTF-8")

        String[] group = request.parameterMap.group
        String xmlText = 'group parameter missing'

        if (group && group[0]) {
            xmlText = '<?xml version="1.0" encoding="utf-8"?>\n' + vkWallParser.parse(group[0])
        }

        response.getWriter() << xmlText

    }

    @Override
    public void destroy() {
        getServletContext().log("destroy() called");
    }

}