package com.project.hmartweb.config.security;

import com.project.hmartweb.application.constant.SecurityConstant;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    @Value("${api.prefix}")
    private String API_PREFIX;
    private final CustomUserDetailService userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        final String authHeader = request.getHeader(SecurityConstant.HEADER_AUTHORIZATION);
        try {
            if (isByPassToken(request)) {
                filterChain.doFilter(request, response);
                return;
            }
            if (authHeader == null || !authHeader.startsWith(SecurityConstant.TOKEN_PREFIX)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            } else {
                final String token = authHeader.substring(7);
                final String userName = jwtTokenProvider.extractUsername(token);

                if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
                    if (jwtTokenProvider.validateToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
                filterChain.doFilter(request, response);
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().print("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private boolean isByPassToken(@NonNull HttpServletRequest request) {
        final List<Pair<String, String>> bypassTokens = Arrays.asList(
                Pair.of((API_PREFIX + "/users/login"), "POST"),
                Pair.of((API_PREFIX + "/users/register"), "POST"),
                Pair.of((API_PREFIX + "/products"), "GET"),
                Pair.of((API_PREFIX + "/categories"), "GET"),
                Pair.of((API_PREFIX + "/feedbacks"), "GET"),
                Pair.of((API_PREFIX + "/galleries"), "GET")
        );
        String requestPath = request.getServletPath();
        String requestMethod = request.getMethod();
        for (Pair<String, String> token : bypassTokens) {
            if (requestPath.contains(token.getFirst()) && requestMethod.equals(token.getSecond())) {
                return true;
            }
        }
        return false;
    }
}
