package edu.pure.server.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.util.Date;

@Component
public class JwtProvider {
    private static final Logger logger = LoggerFactory.getLogger(JwtProvider.class);

    private final KeyPair keyPair;

    @Value("${jwt-provider.issuer}")
    private String issuer;

    @Value("${jwt-provider.expiration-ms}")
    private int expirationMs;

    public JwtProvider() {
        this.keyPair = Keys.keyPairFor(SignatureAlgorithm.ES512);
        JwtProvider.logger.info("Using generated JWT key pair:");
        JwtProvider.logger.info("-- BEGIN PRIVATE KEY --");
        JwtProvider.logger.info(Encoders.BASE64.encode(this.keyPair.getPrivate().getEncoded()));
        JwtProvider.logger.info("-- END PRIVATE KEY --");
        JwtProvider.logger.info("-- BEGIN PUBLIC KEY --");
        JwtProvider.logger.info(Encoders.BASE64.encode(this.keyPair.getPublic().getEncoded()));
        JwtProvider.logger.info("-- END PUBLIC KEY --");
    }

    public String generateToken(@NotNull final Authentication authentication) {
        final UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        final Date now = new Date();
        final Date expiry = new Date(now.getTime() + this.expirationMs);
        return Jwts.builder()
                   .setIssuer(this.issuer)
                   .setSubject(userPrincipal.getId().toString())
                   .setIssuedAt(now)
                   .setExpiration(expiry)
                   .signWith(this.keyPair.getPrivate())
                   .compact();
    }

    Long getUserIdFromJwt(final String token) {
        final Claims claims = Jwts.parserBuilder()
                                  .setSigningKey(this.keyPair.getPublic())
                                  .requireIssuer(this.issuer)
                                  .build()
                                  .parseClaimsJws(token).getBody();
        return Long.parseLong(claims.getSubject());
    }

    boolean validateJwt(final String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(this.keyPair.getPublic())
                .requireIssuer(this.issuer)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (final ExpiredJwtException e) {
            JwtProvider.logger.error("Expired JWT token");
        } catch (final UnsupportedJwtException e) {
            JwtProvider.logger.error("Unsupported JWT token");
        } catch (final MalformedJwtException e) {
            JwtProvider.logger.error("Invalid JWT token");
        } catch (final SignatureException e) {
            JwtProvider.logger.error("Invalid JWT signature");
        } catch (final IllegalArgumentException e) {
            JwtProvider.logger.error("Blank JWT claims string");
        }
        return false;
    }
}
