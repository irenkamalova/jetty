import javax.management.MBeanServer;
import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.ForwardedRequestCustomizer;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SessionIdManager;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.session.DefaultSessionIdManager;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlets.DoSFilter;

public class HelloWorld extends AbstractHandler {
    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response)
        throws IOException
    {
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        try {
            Thread.sleep(5 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        response.getWriter().println("Hello World");
    }

    public static void main(String[] args) throws Exception
    {
        Server server = new Server(8081);

        ServletContextHandler context = new ServletContextHandler(server, "/");
        context.addServlet(HelloServlet.class, "/hello");
        DoSFilter filter = new DoSFilter();
        FilterHolder holder = new FilterHolder(filter);
        String name = "FilterHolder";
        holder.setName(name);
        holder.setInitParameter("maxRequestMs", "1000");
        context.addFilter(holder, "/*", EnumSet.of(DispatcherType.REQUEST));
        // can we add new filter for different api?
        // this filer for all routes

        // + filter for rout /hello
        context.setInitParameter(ServletContextHandler.MANAGED_ATTRIBUTES, name);

        server.start();
        server.join();
    }
}