import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

import java.util.Objects;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Тестовая утилита, Mitm для анализа HMI-протокола");
        System.out.println("Usage: java -cp JAR.jar Main http://hostsid:50000 uname passwd");
        System.out.println("Смотри папку logs для снятых пейлоадов");
        Server server = new Server();
        HttpConfiguration http_config = new HttpConfiguration();
        ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(http_config));
        http.setPort(8080);
        server.addConnector(http);

        String uri = Objects.requireNonNull(args[0]);
        String uname = Objects.requireNonNull(args[1]);
        String passwd = Objects.requireNonNull(args[2]);

        MitmServlet servlet = new MitmServlet(uri, uname, passwd);
        ServletContextHandler context = new ServletContextHandler();
        context.addServlet(servlet, "/");
        servlet.init();

        server.setHandler(context);
        server.start();
    }
}
