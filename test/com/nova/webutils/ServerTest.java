package com.nova.webutils;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;

import com.nova.webutils.HttpClient;
import com.nova.webutils.http.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.nova.webutils.AppServer;
import com.nova.webutils.Application;

public class ServerTest {
    @Test
    public void pathTest() throws IOException {
        AppServer server = new AppServer(8080);

        server.registerApp("/", TestApp.class);
        server.registerApp("/secret", PathApp.class);
        server.start();

        InetSocketAddress addr = new InetSocketAddress("localhost", 8080);

        HttpResponse res = HttpClient.request(addr, new RequestBuilder()
                .method(HttpMethod.GET)
                .path(URI.create("/"))
                .version("HTTP/1.1")
                .header("Host", "localhost")
                .getRequest()
        );
        HttpResponse res2 = HttpClient.request(addr, new RequestBuilder()
                .method(HttpMethod.GET)
                .path(URI.create("/secret"))
                .version("HTTP/1.1")
                .header("Host", "localhost")
                .getRequest()
        );

        assertNotNull(res);
        assertNotNull(res2);
        System.out.println(HttpParser.serializeResponse(res));
        System.out.println(HttpParser.serializeResponse(res2));
    }

    public static class TestApp implements Application {
        public TestApp() {}
        @Override
        public HttpResponse process(HttpRequest req) {
            return new ResponseBuilder()
                    .version("HTTP/1.1")
                    .status(HttpStatus.OK)
                    .header("Content-Type", "text/html")
                    .body("<html><h1>Hello World!</h1></html>")
            .getResponse();
        }
    }

    public static class PathApp implements Application {
        public PathApp() {}

        @Override
        public HttpResponse process(HttpRequest req) {
            String myBody = "<html><p>you have accessed the top secret page.</p></html>";
            return new ResponseBuilder()
                    .version("HTTP/1.1")
                    .status(HttpStatus.OK)
                    .header("Content-Type", "text/html")
                    .body(myBody)
                    .getResponse();
        }
    }
}
