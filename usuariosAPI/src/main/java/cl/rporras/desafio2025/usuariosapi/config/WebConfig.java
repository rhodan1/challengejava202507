package cl.rporras.desafio2025.usuariosapi.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {

    //TODO: Habilitar filtro de Validacion JWT

//    @Autowired
//    private JwtAuthenticationFilter jwtAuthenticationFilter;
//
//    @Bean
//    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilter(JwtProvider jwtProvider) {
//        FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>();
//        registration.setFilter(new JwtAuthenticationFilter(jwtProvider));
//        registration.addUrlPatterns("/*"); // rutas protegidas
//        registration.setOrder(1);
//        return registration;
//    }
}

