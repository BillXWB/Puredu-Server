package edu.pure.server.security;

import io.jsonwebtoken.lang.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtProvider tokenProvider;

    @Autowired
    private UserPrincipalService userPrincipalService;

    @Override
    protected void doFilterInternal(@NotNull final HttpServletRequest request,
                                    @NotNull final HttpServletResponse response,
                                    @NotNull final FilterChain filterChain)
            throws ServletException, IOException {
        try {
            final String token = JwtAuthenticationFilter.getJwtFromRequest(request);
            if (Strings.hasText(token) && this.tokenProvider.validateJwt(token)) {
                final Long userId = this.tokenProvider.getUserIdFromJwt(token);
                final UserDetails userDetails = this.userPrincipalService.loadUserById(userId);
                final UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null,
                                                                userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource()
                                                  .buildDetails(request));
                SecurityContextHolder.getContext()
                                     .setAuthentication(authentication);
            }
        } catch (final Exception e) {
            JwtAuthenticationFilter.logger
                    .error("Could not set user authentication in security context", e);
        }
        filterChain.doFilter(request, response);
    }

    private static @Nullable String getJwtFromRequest(@NotNull final HttpServletRequest request) {
        final String bearerToken = request.getHeader("Authorization");
        final String TOKEN_HEADER = "Bearer ";
        if (Strings.hasText(bearerToken) && bearerToken.startsWith(TOKEN_HEADER)) {
            return bearerToken.substring(TOKEN_HEADER.length());
        }
        return null;
    }
}
