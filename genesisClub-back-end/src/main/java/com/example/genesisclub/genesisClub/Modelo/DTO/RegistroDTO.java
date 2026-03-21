package com.example.genesisclub.genesisClub.Modelo.DTO;

import java.time.LocalDate;

import com.example.genesisclub.genesisClub.Modelo.Enums.EstadoSocioEnums;
import com.example.genesisclub.genesisClub.Modelo.Enums.RolesEnums;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistroDTO {
    
    // Usuario común

    @NotBlank(message = "El nombre es obligatorio")
    @Pattern(
        regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ]+(?: [A-Za-zÁÉÍÓÚáéíóúÑñ]+)*$",
        message = "El nombre solo puede contener letras"
    )
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String nombre;


    @NotBlank(message = "El apellido es obligatorio")
    @Pattern(
        regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ]+(?: [A-Za-zÁÉÍÓÚáéíóúÑñ]+)*$",
        message = "El apellido solo puede contener letras"
    )
    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
    private String apellido;


    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no es válido")
    private String email;


    @NotBlank(message = "La contraseña es obligatoria")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$",
        message = "La contraseña debe tener mínimo 8 caracteres, mayúscula, minúscula, número y símbolo"
    )
    private String password;


    private LocalDate fechaCreacion;


    @NotBlank(message = "El código de área es obligatorio")
    @Pattern(
        regexp = "^[1-9][0-9]{1,4}$",
        message = "Código de área inválido (no incluir el 0)"
    )
    private String codigoArea;


    @NotBlank(message = "El número de celular es obligatorio")
    @Pattern(
        regexp = "^[0-9]{6,10}$",
        message = "Número de celular inválido (no incluir el 15)"
    )
    private String numeroCelular;


    private RolesEnums rol;

    private EstadoSocioEnums estado;
}