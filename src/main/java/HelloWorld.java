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

public class HelloWorld extends AbstractHandler
{
    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response)
        throws IOException, ServletException
    {
        //response.setContentType("text/html;charset=utf-8");
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

        //var handler = new HelloWorld();
        //handler.setStopTimeout(1);
        //server.setHandler(handler);
        ServerConnector localhost = createSocketConnector(server, "localhost", 8081);
        server.setConnectors(new Connector[] {localhost});

        ServletContextHandler context = new ServletContextHandler(server, "/");
        context.addServlet(HelloServlet.class, "/hello");
        DoSFilter filter = new DoSFilter();
        //filter.setMaxRequestMs(100);
        FilterHolder holder = new FilterHolder(filter);
        String name = "FilterHolder";
        holder.setName(name);
        holder.setInitParameter("maxRequestMs", "7000");
        context.addFilter(holder, "/*", EnumSet.of(DispatcherType.REQUEST));
        context.setInitParameter(ServletContextHandler.MANAGED_ATTRIBUTES, name);

        server.start();
        server.join();
    }

    public static ServerConnector createSocketConnector(Server server, String host, int port) {

        HttpConnectionFactory httpConnectionFactory = createHttpConnectionFactory();
        ServerConnector connector = new ServerConnector(server, httpConnectionFactory);
        initializeConnector(connector, host, port);
        return connector;
    }

    private static void initializeConnector(ServerConnector connector, String host, int port) {
        // Set some timeout options to make debugging easier.
        connector.setIdleTimeout(TimeUnit.HOURS.toMillis(1));
        connector.setHost(host);
        connector.setPort(port);
    }

    private static HttpConnectionFactory createHttpConnectionFactory() {
        HttpConfiguration httpConfig = new HttpConfiguration();
        httpConfig.setSecureScheme("https");
        httpConfig.setBlockingTimeout(1);
        httpConfig.setMinResponseDataRate(1);
        httpConfig.setMinRequestDataRate(1);
        httpConfig.setIdleTimeout(1);
        httpConfig.addCustomizer(new ForwardedRequestCustomizer());
        return new HttpConnectionFactory(httpConfig);
    }
}