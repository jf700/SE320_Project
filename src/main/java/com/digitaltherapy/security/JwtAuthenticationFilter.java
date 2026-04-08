package com.digitaltherapy.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Set;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final Set<String> PUBLIC = Set.of("/auth/", "/h2-console", "/swagger-ui", "/v3/api-docs");
    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;
    private final TokenBlacklist tokenBlacklist;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider,
                                    UserDetailsService userDetailsService,
                                    TokenBlacklist tokenBlacklist) {
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
        this.tokenBlacklist = tokenBlacklist;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        try {
            String token = resolveToken(req);
            if (token != null && tokenProvider.validateToken(token) && !tokenBlacklist.isBlacklisted(token)) {
                java.util.UUID userId = tokenProvider.getUserIdFromToken(token);
                UserDetails ud = userDetailsService.loadUserByUsername(userId.toString());
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(ud, null, ud.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception e) { log.error("Cannot set user authentication: {}", e.getMessage()); }
        chain.doFilter(req, res);
    }

    private String resolveToken(HttpServletRequest req) {
        String bearer = req.getHeader("Authorization");
        return (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) ? bearer.substring(7) : null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest req) {
        String path = req.getServletPath();
        return PUBLIC.stream().anyMatch(path::startsWith);
    }
}
