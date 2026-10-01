package be.mjodheim.cellar.identity.internal.adapter.out.security;

import be.mjodheim.cellar.identity.internal.application.port.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Central stateless HTTP security configuration.
 *
 * <p>Authentication is delegated to Spring Security's OAuth2 Resource Server support.
 * Access tokens are signed with HS256. The signing key is loaded exclusively from the
 * {@code JWT_SECRET} environment variable and must be a Base64-encoded key of at least
 * 256 bits.</p>
 */
@Configuration
@EnableMethodSecurity
class SecurityConfiguration {

    /**
     * Provides the BCrypt encoder used for password storage and verification.
     *
     * @return password encoder configured with work factor 12
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Decodes and validates the HMAC signing key from configuration.
     *
     * @param encodedSecret Base64-encoded JWT secret
     * @return HS256-compatible secret key
     * @throws IllegalStateException when the secret is missing, malformed or too short
     */
    @Bean
    SecretKey jwtSecretKey(@Value("${cellar.security.jwt.secret}") String encodedSecret) {
        if (encodedSecret == null || encodedSecret.isBlank()) {
            throw new IllegalStateException("JWT_SECRET must be defined");
        }

        byte[] decoded;
        try {
            decoded = Base64.getDecoder().decode(encodedSecret);
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException("JWT_SECRET must be valid Base64", exception);
        }

        if (decoded.length < 32) {
            throw new IllegalStateException("JWT_SECRET must contain at least 256 bits");
        }

        return new SecretKeySpec(decoded, "HmacSHA256");
    }

    /**
     * Creates the JWT encoder used to issue access tokens.
     *
     * @param key HMAC signing key
     * @return configured JWT encoder
     */
    @Bean
    JwtEncoder jwtEncoder(SecretKey key) {
        return NimbusJwtEncoder.withSecretKey(key)
                .algorithm(MacAlgorithm.HS256)
                .build();
    }

    /**
     * Creates the JWT decoder and issuer validator used for authenticated requests.
     *
     * @param key HMAC signing key
     * @param issuer expected token issuer
     * @param userRepository current account state for token validation
     * @return configured JWT decoder
     */
    @Bean
    JwtDecoder jwtDecoder(
            SecretKey key,
            @Value("${cellar.security.jwt.issuer}") String issuer,
            UserRepository userRepository
    ) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefaultWithIssuer(issuer),
                new JwtAccountValidator(userRepository)
        ));
        return decoder;
    }

    /**
     * Maps the custom {@code roles} JWT claim to Spring Security authorities.
     *
     * @return JWT authentication converter using the {@code ROLE_} prefix
     */
    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authorities = new JwtGrantedAuthoritiesConverter();
        authorities.setAuthoritiesClaimName("roles");
        authorities.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authorities);
        return converter;
    }

    /**
     * Configures stateless endpoint authorization and JWT resource-server processing.
     *
     * @param http Spring Security HTTP configuration
     * @param authenticationConverter converter for JWT roles
     * @return configured security filter chain
     * @throws Exception when Spring Security cannot build the filter chain
     */
    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationConverter authenticationConverter
    ) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/actuator/health"
                        ).permitAll()
                        .requestMatchers(
                                "/api/auth/register",
                                "/api/auth/login",
                                "/api/auth/refresh",
                                "/api/auth/logout"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/catalog/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/catalog/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/catalog/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/catalog/**").hasRole("ADMIN")
                        .requestMatchers("/api/inventory/**").hasRole("ADMIN")
                        .requestMatchers("/api/orders/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/catalog/**").authenticated()
                        .requestMatchers("/api/auth/me").authenticated()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(resourceServer ->
                        resourceServer.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(authenticationConverter)))
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
