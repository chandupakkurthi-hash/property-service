package com.example.Property_Service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (isPublicRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String token = authHeader.substring(7);
        if (!jwtService.isTokenValid(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String email = jwtService.extractEmail(token);
        Long userId = jwtService.extractUserId(token);
        String role = jwtService.extractRole(token);
        if (email == null || email.isBlank() || userId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String authority = (role == null || role.isBlank()) ? "ROLE_USER"
                : (role.startsWith("ROLE_") ? role : "ROLE_" + role);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                new AuthenticatedUser(userId, email),
                null,
                List.of(new SimpleGrantedAuthority(authority))
        );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private boolean isPublicRequest(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        if (path == null) return false;

        if (path.startsWith("/actuator")) return true;
        if ("/error".equals(path)) return true;

        if ("GET".equalsIgnoreCase(method)) {
            if ("/properties/search".equals(path)) return true;
            if (path.startsWith("/properties") && !path.contains("/owner/")) return true;
        }

        return false;
    }
}
