package cl.rporras.desafio2025.usuariosapi.security;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    public JwtAuthenticationFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        //TODO: Mover validacion a Web.config
        if ("prod".equalsIgnoreCase(activeProfile)) {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);


            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                if (jwtProvider.validateToken(token)) {
                    String email = jwtProvider.getEmailFromToken(token);
                    // Aquí podrías setear un SecurityContext si usas Spring Security
                    request.setAttribute("usuarioEmail", email); // Simple ejemplo
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"mensaje\":\"Token inválido o expirado\"}");
                    return;
                }
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"mensaje\":\"Token ausente o mal formado\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
