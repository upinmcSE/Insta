package init.upinmcse.backend.config.init;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Component
public class LoggingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("(\"password\"\\s*:\\s*\").*?(\")", Pattern.CASE_INSENSITIVE);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(req);
        HttpServletResponse res = (HttpServletResponse) response;
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(res);

        chain.doFilter(wrappedRequest, wrappedResponse);

        String requestBody = new String(wrappedRequest.getContentAsByteArray(), StandardCharsets.UTF_8);
        String sanitizedRequestBody = maskPassword(requestBody);

        logger.info("=== REQUEST ===\n{} {}\nBody: {}", req.getMethod(), req.getRequestURI(), sanitizedRequestBody);

        String responseBody = new String(wrappedResponse.getContentAsByteArray(), StandardCharsets.UTF_8);
        logger.info("=== RESPONSE ===\nStatus: {}\nBody: {}", res.getStatus(), responseBody);

        wrappedResponse.copyBodyToResponse();
    }

    private String maskPassword(String body) {
        if (body == null || body.isEmpty()) return body;
        return PASSWORD_PATTERN.matcher(body).replaceAll("$1***$2");
    }
}

