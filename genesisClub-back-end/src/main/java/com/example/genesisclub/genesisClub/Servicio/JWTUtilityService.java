package com.example.genesisclub.genesisClub.Servicio;

import com.nimbusds.jwt.JWTClaimsSet;

public interface JWTUtilityService {

    String generateJWT(Long userId, String rol) throws Exception;

    JWTClaimsSet parseJWT(String jwt) throws Exception;

    Long getUserId(String jwt) throws Exception;

    String getRol(String jwt) throws Exception;

    boolean isTokenValid(String jwt);

}
