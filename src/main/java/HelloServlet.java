import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Iterator;

public class HelloServlet extends HttpServlet {
    private int counter = 1;
    private final String backend;
    private final Authenticator authenticator;
    private final Path logs = Paths.get("logs");
    private final HttpClient client;

    public HelloServlet(String uri, String uname, String passwd) throws IOException, InterruptedException {
        if (!Files.isDirectory(logs)) Files.createDirectories(logs);
        this.backend = uri;
        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(uname, passwd.toCharArray());
            }
        };
        this.authenticator = auth;

        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);

        client = HttpClient.newBuilder()
                .authenticator(authenticator)
                .version(HttpClient.Version.HTTP_1_1)
                .cookieHandler(cookieManager)
                .build();

        String b64 =  Base64.getEncoder().encodeToString((uname + ":" + passwd).getBytes(StandardCharsets.UTF_8));
        HttpRequest getAuth = HttpRequest
                .newBuilder(URI.create(uri + "/rep/applcomp/int"))
                .setHeader("authorization", "Basic " + b64)
                .build();
        HttpResponse<String> respAuth = client.send(getAuth, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (respAuth.statusCode()!=200) {
            throw new IllegalStateException("Wrong response code: " + respAuth + " when auth, " + respAuth.body());
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Path logPath = logs.resolve(String.format("%05d.log", counter));

        String method = req.getMethod(), body = "";
        String s = String.format("method=%s, path=%s, query=%s, counter=%05d\n", method, req.getServletPath(), req.getQueryString(), counter);
        String url = backend + req.getServletPath();
        if (req.getQueryString() != null) {
            url += "?" + req.getQueryString();
        }
        if (req.getInputStream() != null) {
            body = IOUtils.toString(req.getInputStream(), StandardCharsets.UTF_8);
        }
        OutputStream os = Files.newOutputStream(logPath);
        counter++;
        try {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(URI.create(url));
            if (method.equals("POST")) {
                requestBuilder = requestBuilder.method(method, HttpRequest.BodyPublishers.ofString(body));
            }
            for (Iterator<String> it = req.getHeaderNames().asIterator(); it.hasNext(); ) {
                String h = it.next();
                String hv = req.getHeader(h);
                if (!h.equalsIgnoreCase("host") &&
                        !h.equalsIgnoreCase("connection") &&
                        !h.equalsIgnoreCase("content-length") &&
                        !h.equalsIgnoreCase("authorization")) {
                    s += "\n" + h + ": " + hv;
                    requestBuilder = requestBuilder.header(h, hv);
                }
            }
            s += "\nBODY request: " + body + "\n***********************\n";

            HttpResponse<String> response;
            HttpRequest httpRequest = requestBuilder.build();
//            s += "\n\n" + httpRequest;
//            for (String hn : httpRequest.headers().map().keySet()) {
//                String hv = httpRequest.headers().firstValue(hn).get();
//                s += "\n" + hn + ": " + hv;
//            }

            response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            resp.setStatus(response.statusCode());
            s += "\n==============================\nresponse code: " + response.statusCode();

            for (String hn : response.headers().map().keySet()) {
                String hv = response.headers().firstValue(hn).get();
                resp.setHeader(hn, hv);
                s += "\n" + hn + ": " + hv;
            }
            body = response.body();
            s += "\nBODY: " + body;
            resp.getWriter().print(body);
        } catch (Exception e) {
            resp.setStatus(500);
            resp.setContentType("text/html");
            resp.getWriter().printf("<html><pre>%s</pre></html>", s);
            s += "Exception occurs: " + e;
        }
        IOUtils.write(s, os, StandardCharsets.UTF_8);
        os.close();
    }

}
