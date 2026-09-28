package be.mjodheim.cellar.identity.internal.adapter.out.security;

import be.mjodheim.cellar.identity.internal.application.port.AccessTokenPort;
import be.mjodheim.cellar.identity.internal.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Issues short-lived signed JWT access tokens using Spring Security's JWT support.
 */
@Component
class JwtAccessTokenAdapter implements AccessTokenPort {

    private final JwtEncoder jwtEncoder;
    private final String issuer;
    private final Duration ttl;

    JwtAccessTokenAdapter(
            JwtEncoder jwtEncoder,
            @Value("${cellar.security.jwt.issuer}") String issuer,
            @Value("${cellar.security.jwt.access-token-ttl}") Duration ttl
    ) {
        this.jwtEncoder = jwtEncoder;
        this.issuer = issuer;
        this.ttl = ttl;
    }

    @Override
    public String generate(User user) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject(user.email())
                .issuedAt(now)
                .expiresAt(now.plus(ttl))
                .claim("uid", user.id())
                .claim("name", user.displayName())
                .claim("roles", List.of(user.role().name()))
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    @Override
    public long expiresInSeconds() {
        return ttl.toSeconds();
    }
}
