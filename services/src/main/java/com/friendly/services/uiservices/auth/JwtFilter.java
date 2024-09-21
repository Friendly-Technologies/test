package com.friendly.services.uiservices.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.INVALID_TOKEN;

public class JwtFilter extends OncePerRequestFilter {

    private final String secret;

    public JwtFilter(String secret) {
        this.secret = secret;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            try {
                token = token.substring(7);

                Claims claims = Jwts.parser()
                        .setSigningKey(secret.getBytes(StandardCharsets.UTF_8))
                        .parseClaimsJws(token)
                        .getBody();

                String username = claims.getSubject();
                String userId = String.valueOf(claims.get("userId", Integer.class));
                String clientType = claims.get("clientType", String.class);
                String zoneId = claims.get("zoneId", String.class);
                List<SimpleGrantedAuthority> authorities = Collections.emptyList();

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username,
                        null, authorities);
                Map<String, String> details = new HashMap<>();
                details.put("userId", userId);
                details.put("clientType", clientType);
                details.put("zoneId", zoneId);
                authentication.setDetails(details);

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                SecurityContextHolder.clearContext();
                setErrorResponse(response);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return HttpMethod.OPTIONS.matches(request.getMethod())
                || path.contains("/swagger-ui.html")
                || path.contains("/v2/api-docs")
                || path.contains("/swagger-resources")
                || path.contains("/webjars")
                || path.contains("/swagger-ui")
                || path.contains("/iotw/Auth/login")
                || path.contains("/iotw/Auth/logout")
                || path.contains("/iotw/Auth/type")
                || path.contains("/iotw/Auth/password/reset")
                || path.contains("/iotw/System/version")
                || path.contains("/iotw/System/information")
                || path.contains("iotw/User/password/restore")
                || path.contains("/iotw/Setting/interface/hideForgotPassword")
                || path.contains("/iotw/Setting/interface/passwordResetRetryCooldown");
    }

    private void setErrorResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("error", INVALID_TOKEN.getErrorMessage());
        errorDetails.put("code", INVALID_TOKEN.getErrorCode());

        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getOutputStream(), errorDetails);
    }
}

