package com.example.genesisclub.genesisClub.Servicio.servicioImpl;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.example.genesisclub.genesisClub.Servicio.JWTUtilityService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jwt.*;

@Service
public class JWTUtilityServiceImpl implements JWTUtilityService {

    @Value("classpath:jwtKeys/private_key.pem")
    private Resource privateResource;

    @Value("classpath:jwtKeys/public_key.pem")
    private Resource publicResource;

    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 4; // 4 horas

    // ======================================================
    // 🔥 GENERAR TOKEN (USER + ROL)
    // ======================================================
    @Override
    public String generateJWT(Long userId, String rol) throws Exception {

        PrivateKey privateKey = loadPrivateKey(privateResource);
        JWSSigner signer = new RSASSASigner(privateKey);

        Date now = new Date();

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(userId.toString())
                .claim("rol", rol)   // 🔥 CLAIM PERSONALIZADO
                .issueTime(now)
                .expirationTime(new Date(now.getTime() + EXPIRATION_TIME))
                .build();

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader(JWSAlgorithm.RS256),
                claimsSet
        );

        signedJWT.sign(signer);

        return signedJWT.serialize();
    }

    // ======================================================
    // 🔥 PARSEAR Y VALIDAR TOKEN
    // ======================================================
    @Override
    public JWTClaimsSet parseJWT(String jwt) throws Exception {

        PublicKey publicKey = loadPublicKey(publicResource);

        SignedJWT signedJWT = SignedJWT.parse(jwt);

        JWSVerifier verifier = new RSASSAVerifier((RSAPublicKey) publicKey);

        if (!signedJWT.verify(verifier)) {
            throw new JOSEException("Firma inválida");
        }

        JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

        if (claims.getExpirationTime().before(new Date())) {
            throw new JOSEException("Token expirado");
        }

        return claims;
    }

    // ======================================================
    // 🔥 OBTENER USER ID
    // ======================================================
    @Override
    public Long getUserId(String jwt) throws Exception {
        return Long.parseLong(parseJWT(jwt).getSubject());
    }

    // ======================================================
    // 🔥 OBTENER ROL
    // ======================================================
    @Override
    public String getRol(String jwt) throws Exception {
        return parseJWT(jwt).getStringClaim("rol");
    }

    // ======================================================
    // 🔥 VALIDACIÓN SIMPLE
    // ======================================================
    @Override
    public boolean isTokenValid(String jwt) {
        try {
            parseJWT(jwt);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ======================================================
    // 🔥 CARGAR PRIVATE KEY
    // ======================================================
    private PrivateKey loadPrivateKey(Resource resource) throws Exception {

        byte[] keyBytes = Files.readAllBytes(Path.of(resource.getURI()));

        String key = new String(keyBytes, StandardCharsets.UTF_8)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(key);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(decoded));
    }

    // ======================================================
    // 🔥 CARGAR PUBLIC KEY
    // ======================================================
    private PublicKey loadPublicKey(Resource resource) throws Exception {

        byte[] keyBytes = Files.readAllBytes(Path.of(resource.getURI()));

        String key = new String(keyBytes, StandardCharsets.UTF_8)
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(key);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        return keyFactory.generatePublic(new X509EncodedKeySpec(decoded));
    }
}
