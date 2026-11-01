package br.com.officyna.infrastructure.security;

import br.com.officyna.domain.user.entity.User;
import br.com.officyna.domain.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    @Autowired(required = false)
    private UserRepository userRepository;

    public JwtService() {
    }

    public JwtService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String generateToken(UserDetails userDetails) {
        User user = null;
        if (userRepository != null && userDetails != null && userDetails.getUsername() != null) {
            user = userRepository.findByEmail(userDetails.getUsername()).orElse(null);
        }
        return generateToken(userDetails, user);
    }

    public String generateToken(User user) {
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword() != null ? user.getPassword() : "",
                user.getUserRole() != null
                        ? List.of(new SimpleGrantedAuthority("ROLE_" + user.getUserRole().name()))
                        : List.of());
        return generateToken(userDetails, user);
    }

    public String generateToken(UserDetails userDetails, User user) {
        Map<String, Object> extraClaims = new LinkedHashMap<>();
        if (user != null) {
            if (user.getId() != null) {
                extraClaims.put("id", user.getId());
            }
            if (user.getName() != null) {
                extraClaims.put("name", user.getName());
            }
            if (user.getEmail() != null) {
                extraClaims.put("email", user.getEmail());
            }
        }
        return generateToken(userDetails, extraClaims);
    }

    public String generateToken(UserDetails userDetails, Map<String, Object> extraClaims) {
        log.info("Generating JWT token for user: {}", userDetails.getUsername());

        var builder = Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("roles", getRole(userDetails));

        if (extraClaims != null) {
            extraClaims.forEach((key, value) -> {
                if (value != null) {
                    builder.claim(key, value);
                }
            });
        }

        String token = builder
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();

        log.info("JWT token generated successfully for user: {}", userDetails.getUsername());

        return token;
    }

    private static @NonNull String getRole(UserDetails userDetails) {
        String roles = userDetails.getAuthorities()
                .stream()
                .map(a -> Objects.requireNonNull(a.getAuthority())
                        .replace("ROLE_", ""))
                .collect(Collectors.joining(", "));

        log.debug("User roles extracted: {}", roles);

        return roles;
    }

    public String extractUsername(String token) {
        log.debug("Extracting username from JWT token");

        String username = extractClaim(token, Claims::getSubject);

        log.info("Username extracted from token: {}", username);

        return username;
    }

    public String extractId(String token) {
        return extractClaim(token, claims -> claims.get("id", String.class));
    }

    public String extractName(String token) {
        return extractClaim(token, claims -> claims.get("name", String.class));
    }

    public String extractEmail(String token) {
        return extractClaim(token, claims -> claims.get("email", String.class));
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        log.debug("Validating JWT token for user: {}", userDetails.getUsername());

        String username = extractUsername(token);

        boolean valid = username.equals(userDetails.getUsername())
                && !isTokenExpired(token);

        if (valid) {
            log.info("JWT token is valid for user: {}", username);
        } else {
            log.warn("Invalid JWT token for user: {}", username);
        }

        return valid;
    }

    private boolean isTokenExpired(String token) {
        Date expirationDate = extractClaim(token, Claims::getExpiration);

        boolean expired = expirationDate.before(new Date());

        if (expired) {
            log.warn("JWT token expired at: {}", expirationDate);
        }

        return expired;
    }

    public Claims extractAllClaims(String token) {
        try {
            log.debug("Parsing JWT token claims");

            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (Exception e) {
            log.error("Error parsing JWT token: {}", e.getMessage());
            throw e;
        }
    }

    public <T> T extractClaim(
            String token,
            Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private SecretKey getSigningKey() {
        log.debug("Generating JWT signing key");

        byte[] keyBytes = Decoders.BASE64.decode(
                Base64.getEncoder().encodeToString(secret.getBytes()));

        log.debug("JWT signing key generated successfully");

        return Keys.hmacShaKeyFor(keyBytes);
    }
}
