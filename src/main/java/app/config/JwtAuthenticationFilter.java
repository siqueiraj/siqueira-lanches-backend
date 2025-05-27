package app.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter implements Filter {

    private final JwtServiceGenerator jwtService;

    public JwtAuthenticationFilter(JwtServiceGenerator jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String path = httpRequest.getServletPath();

        if (path.equals("/api/usuarios/login") ||
            path.startsWith("/api/produtos") ||
            path.startsWith("/api/avaliacoes") ||
            path.equals("/api/pedidos/save") ||
            path.equals("/api/pagamentos/save")
        ) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = httpRequest.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtService.validateToken(token)) {
                String email = jwtService.getUsernameFromToken(token);
                String role = jwtService.getRoleFromToken(token);

                var auth = new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        Collections.singleton(() -> "ROLE_" + role)
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        chain.doFilter(request, response);
    }
}
