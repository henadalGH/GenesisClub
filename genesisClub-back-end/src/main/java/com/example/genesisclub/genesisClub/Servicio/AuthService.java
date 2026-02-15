package com.example.genesisclub.genesisClub.Servicio;


import java.util.Map;
import com.example.genesisclub.genesisClub.Modelo.DTO.LoginDTO;

public interface AuthService {

    public Map<String, Object> login(LoginDTO login) throws Exception;
    public void logout(String token);

}
