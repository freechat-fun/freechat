package fun.freechat.access;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class ConnectorRequestFilter extends OncePerRequestFilter {
    private static final int MAX_REQUEST_BYTES = 64 * 1024;
    private static final org.springframework.web.util.pattern.PathPattern PATH =
            org.springframework.web.util.pattern.PathPatternParser.defaultInstance.parse(
                    "/api/v2/connector/{*remaining}");

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String raw = request.getRequestURI().substring(request.getContextPath().length());
        return !PATH.matches(org.springframework.http.server.PathContainer.parsePath(raw));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        response.setHeader("Cache-Control", "no-store");
        if (request.getContentLengthLong() > MAX_REQUEST_BYTES) {
            response.setStatus(413);
            response.setContentType("application/json");
            response.getWriter()
                    .write(
                            "{\"code\":413,\"errorCode\":\"CONNECTOR_LIMIT_EXCEEDED\",\"message\":\"CONNECTOR_LIMIT_EXCEEDED\"}");
            return;
        }
        chain.doFilter(
                new HttpServletRequestWrapper(request) {
                    private ServletInputStream bounded;

                    @Override
                    public ServletInputStream getInputStream() throws IOException {
                        if (bounded == null) {
                            ServletInputStream delegate = request.getInputStream();
                            bounded = new ServletInputStream() {
                                private int remaining = MAX_REQUEST_BYTES;
                                private boolean exceeded;

                                private void check() throws IOException {
                                    if (exceeded) {
                                        throw new IOException("CONNECTOR_LIMIT_EXCEEDED");
                                    }
                                }

                                @Override
                                public int read() throws IOException {
                                    check();
                                    int value = delegate.read();
                                    if (value != -1 && --remaining < 0) {
                                        exceeded = true;
                                        check();
                                    }
                                    return value;
                                }

                                @Override
                                public int read(byte[] bytes, int offset, int length) throws IOException {
                                    check();
                                    int count = delegate.read(bytes, offset, Math.min(length, remaining + 1));
                                    if (count > remaining) {
                                        exceeded = true;
                                        check();
                                    }
                                    if (count > 0) {
                                        remaining -= count;
                                    }
                                    return count;
                                }

                                @Override
                                public boolean isFinished() {
                                    return delegate.isFinished();
                                }

                                @Override
                                public boolean isReady() {
                                    return delegate.isReady();
                                }

                                @Override
                                public void setReadListener(ReadListener listener) {
                                    delegate.setReadListener(listener);
                                }

                                @Override
                                public void close() throws IOException {
                                    delegate.close();
                                }
                            };
                        }
                        return bounded;
                    }

                    @Override
                    public BufferedReader getReader() throws IOException {
                        return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
                    }
                },
                response);
    }
}
