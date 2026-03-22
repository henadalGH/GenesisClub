package com.example.genesisclub.genesisClub.Servicio;

import com.nimbusds.jwt.JWTClaimsSet;

public interface JWTUtilityService {

    String generateJWT(Long userId, String rol, Long socioId, Long jugadorId, Long adminId) throws Exception;

    JWTClaimsSet parseJWT(String jwt) throws Exception;

    Long getUserId(String jwt) throws Exception;

    String getRol(String jwt) throws Exception;

    Long getSocioId(String jwt) throws Exception;

    Long getJugadorId(String jwt) throws Exception;

    Long getAdminId(String jwt) throws Exception;

    boolean isTokenValid(String jwt);

}
