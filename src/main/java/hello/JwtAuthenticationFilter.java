package hello;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static hello.JwtUtil.HEADER_STRING;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    List<String> excludeUrlPatterns = new LinkedList<>();
    PathMatcher pathMatcher = new AntPathMatcher();

    public JwtAuthenticationFilter(String... excludeUrlPatterns) {
        this.excludeUrlPatterns.addAll(
                Arrays.asList(excludeUrlPatterns));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            String token = request.getHeader(HEADER_STRING);
            JwtUtil.validateToken(token);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
            return;
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return excludeUrlPatterns.stream()
                .anyMatch(p -> pathMatcher.match(p, request.getServletPath()));
    }

}