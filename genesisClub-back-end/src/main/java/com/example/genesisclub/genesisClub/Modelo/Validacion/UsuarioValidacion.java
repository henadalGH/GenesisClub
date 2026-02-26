package com.example.genesisclub.genesisClub.Modelo.Validacion;

import com.example.genesisclub.genesisClub.Modelo.DTO.RegistroDTO;
import com.example.genesisclub.genesisClub.Modelo.DTO.ResponceDTO;

public class UsuarioValidacion {

    public ResponceDTO validation(RegistroDTO usuario){ 

        ResponceDTO responce = new ResponceDTO();
        responce.setNumOfErrors(0);

        // ✅ Validación del nombre (entre 3 y 15 caracteres)
        if(usuario.getNombre() == null || 
            usuario.getNombre().length() < 3 || 
            usuario.getNombre().length() > 15)
        {
            responce.setNumOfErrors(responce.getNumOfErrors() + 1);
            responce.setMensage("El nombre no puede ser nulo y debe tener entre 3 y 15 caracteres");
        }

        // ✅ Validación del apellido (entre 3 y 15 caracteres)
        if(usuario.getApellido() == null || 
            usuario.getApellido().length() < 3 || 
            usuario.getApellido().length() > 15)
        {
            responce.setNumOfErrors(responce.getNumOfErrors() + 1);
            responce.setMensage("El apellido no puede ser nulo y debe tener entre 3 y 15 caracteres");
        }

        // ✅ Validación de email
        if (usuario.getEmail() == null ||
            !usuario.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"))
        {
            responce.setNumOfErrors(responce.getNumOfErrors() + 1);
            responce.setMensage("El email no es válido");
        }

        // ✅ Validación de password segura
        if (usuario.getPassword() == null ||
            !usuario.getPassword().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,}$"))
        {
            responce.setNumOfErrors(responce.getNumOfErrors() + 1);
            responce.setMensage(
                "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula y un número."
            );
        }

        return responce;
}
}
