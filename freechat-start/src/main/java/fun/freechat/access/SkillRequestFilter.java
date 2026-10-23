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
public class SkillRequestFilter extends OncePerRequestFilter {
    private static final int MAX_REQUEST_BYTES = 2 * 1024 * 1024;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String raw = request.getRequestURI().substring(request.getContextPath().length());
        var path = org.springframework.http.server.PathContainer.parsePath(raw);
        var pattern = org.springframework.web.util.pattern.PathPatternParser.defaultInstance.parse(
                "/api/v2/skill/{*remaining}");
        return !pattern.matches(path);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        response.setHeader("Cache-Control", "no-store");
        if (request.getContentLengthLong() > MAX_REQUEST_BYTES) {
            response.sendError(413, "SKILL_LIMIT_EXCEEDED");
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

                                @Override
                                public int read() throws IOException {
                                    int value = delegate.read();
                                    if (value != -1 && --remaining < 0) {
                                        throw new IOException("SKILL_LIMIT_EXCEEDED");
                                    }
                                    return value;
                                }

                                @Override
                                public int read(byte[] bytes, int offset, int length) throws IOException {
                                    int count = delegate.read(bytes, offset, Math.min(length, remaining + 1));
                                    if (count > remaining) {
                                        throw new IOException("SKILL_LIMIT_EXCEEDED");
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
