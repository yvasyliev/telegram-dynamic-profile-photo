package app.telegram.server;

import java.io.IOException;
import java.util.Map;

/**
 * Basic HTTP server. Receives HTTP request and parses URL params.
 */
public interface Server {
    /**
     * Parses URL params from first request.
     *
     * @param timeout request timeout.
     * @return URL params.
     * @throws IOException if errors occur.
     */
    Map<String, String> getURLParams(int timeout) throws IOException;
}
