package app.telegram.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Default HTTP server implementation.
 */
public class DefaultHttpServer implements Server {
    /**
     * Response.
     */
    private static final byte[] RESPONSE = "HTTP/1.1 200 OK\r\nContent-Type: text/html\r\nContent-Length: 2\r\n\r\nok".getBytes(StandardCharsets.UTF_8);

    /**
     * URL params pattern.
     */
    private static final Pattern PARAMS_PATTERN = Pattern.compile("(\\w+=\\w+)");

    /**
     * Reading buffer size.
     */
    private static final int BUFFER_SIZE = 1024;

    /**
     * Server port.
     */
    private final int port;

    public DefaultHttpServer(int port) {
        this.port = port;
    }

    @Override
    public Map<String, String> getURLParams(int timeout) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setSoTimeout(timeout);
            try (Socket socket = serverSocket.accept()) {
                byte[] buffer = new byte[BUFFER_SIZE];
                socket.getInputStream().read(buffer, 0, BUFFER_SIZE);
                String request = new String(buffer, StandardCharsets.UTF_8).split("\n")[0];

                socket.getOutputStream().write(RESPONSE);
                socket.getOutputStream().flush();

                return getURLParams(request);
            }
        }
    }

    /**
     * Parses URL params from an HTTP request first row.
     *
     * @param request an HTTP request first row.
     * @return URL params.
     * @throws UnsupportedEncodingException if decoding errors occur.
     */
    protected Map<String, String> getURLParams(String request) throws UnsupportedEncodingException {
        Map<String, String> URLParams = new HashMap<>();
        Matcher matcher = PARAMS_PATTERN.matcher(request);

        while (matcher.find()) {
            String[] keyVal = matcher.group(1).split("=");
            URLParams.put(
                    URLDecoder.decode(keyVal[0], "UTF-8"),
                    URLDecoder.decode(keyVal[1], "UTF-8")
            );
        }

        return URLParams;
    }
}
