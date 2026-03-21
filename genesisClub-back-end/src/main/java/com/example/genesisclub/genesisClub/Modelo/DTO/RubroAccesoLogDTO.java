package com.example.genesisclub.genesisClub.Modelo.DTO;

import java.time.LocalDateTime;
import com.example.genesisclub.genesisClub.Modelo.Enums.TipoUsuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RubroAccesoLogDTO {

    private Long id;
    
    private LocalDateTime fechaAcceso;
    
    private String claveUtilizada;
    
    private TipoUsuario tipoUsuario;
    
    private boolean exitoso;
    
    private String ipAcceso;
    
    private Long idRubro;
    private String nombreRubro;
    
    private Long idUsuario;
    private String nomUsuario;

}
