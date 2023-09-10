package com.polarbookshop.edgeservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.server.WebSessionServerOAuth2AuthorizedClientRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

@EnableWebFluxSecurity // Enables Spring Security for a WebFlux application
public class SecurityConfig {
    /**
     * Defines a repositoty to store Access Tokens in the eb session
     * @return
     */
    @Bean
    ServerOAuth2AuthorizedClientRepository authorizedClientRepository(){
        return new WebSessionServerOAuth2AuthorizedClientRepository();
    }

    /**
     * The SecurityWebFilterChain is the entry point to configure
     *
     * @param http                         is the object that allows configuring the security
     * @param clientRegistrationRepository is automatically
     * @return
     */
    @Bean
    SecurityWebFilterChain springSecurityFilterChain(
            ServerHttpSecurity http,
            ReactiveClientRegistrationRepository clientRegistrationRepository
    ) {
        return http
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/", "/*.css", "/*.js", "/favicon.ico") // Allow unauthenticated access to the SPA static resources
                        .permitAll()
                        .pathMatchers(HttpMethod.GET, "/books/**") // Allow unauthenticated access to the books in the catalog
                        .permitAll()
                        .anyExchange().authenticated() // All requests require authentication
                )
                // Returning 401 when the user is not authenticated
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .oauth2Login(Customizer.withDefaults()) // Enable OAuth 2.0 login
                // Define a custom handler for the scenario where a logout operation is completed successfully
                .logout(logout -> logout.logoutSuccessHandler(oidcLogoutSuccessHandler(clientRegistrationRepository)))
                // Uses a cookie-based strategy for exchanging CSRF tokens with the Angular frontend
                .csrf(csrf -> csrf.csrfTokenRepository((CookieServerCsrfTokenRepository.withHttpOnlyFalse())))
                .build();
    }

    /**
     * Due to the Reactive streams need to be subscribed to in order to
     * activate them. At the moment, CookieServerCsrfTokenRepository doesn’t ensure a
     * subscription to CsrfToken, so you must explicitly provide a workaround in a WebFilter bean
     * A filter with the only purpose of subscribing to the CsrfToken reactive stream
     * and ensuring its value is extracted correctly
     * @return
     */
    @Bean
    WebFilter csrfWebFilter(){
        return (exchange, chain) -> {
            exchange.getResponse().beforeCommit(() -> Mono.defer(() -> {
                Mono<CsrfToken> csrfToken =
                        exchange.getAttribute(CsrfToken.class.getName());
                return csrfToken != null ? csrfToken.then() : Mono.empty();
            }));
            return chain.filter(exchange);
        };
    }

    /**
     * After logging out from the OIDC Provider, Keycloak will
     * redirect the user to the application base URL computed
     * dynamically from Spring (locally, it’s http:/ /localhost:9000)
     *
     * @param clientRegistrationRepository is automatically
     *                                     configured by Spring Boot for storing the information about the clients registered with Keycloak, and it’s used by Spring Security for authentication/
     *                                     authorization purposes. In our example, there’s only a client: the one we configured earlier in the application.yml file
     * @return
     */
    private ServerLogoutSuccessHandler oidcLogoutSuccessHandler(
            ReactiveClientRegistrationRepository clientRegistrationRepository
    ) {
        var oidcLogoutSuccessHandler = new OidcClientInitiatedServerLogoutSuccessHandler(
                clientRegistrationRepository
        );
        oidcLogoutSuccessHandler.setPostLogoutRedirectUri(
                "{baseUrl}");
        return oidcLogoutSuccessHandler;

    }
}
