package biscof.app.security.jwt;

import biscof.app.exception.exceptions.AuthException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private final JwtUtils jwtUtils;
    @Autowired
    private final UserDetailsService userDetailsService;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader(HEADER_STRING);
        if (authHeader == null || !authHeader.startsWith(TOKEN_PREFIX)) {
            // Pass the request to the next filter.
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(TOKEN_PREFIX.length());
        final String userEmail = jwtUtils.extractUsername(jwt);
        // Check if the JWT token contains a username, and the current user isn't already authenticated.
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
            if (jwtUtils.isTokenValid(jwt, userDetails)) {
                updateSecurityContext(userDetails, request);
            } else {
                throw new AuthException("The provided token is either invalid or has expired.");
            }
        }
        filterChain.doFilter(request, response);
    }

    private void updateSecurityContext(UserDetails userDetails, HttpServletRequest request) {
        // Create an authentication token used for updating the security context.
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // Update the security context to keep information about the authenticated user.
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}
