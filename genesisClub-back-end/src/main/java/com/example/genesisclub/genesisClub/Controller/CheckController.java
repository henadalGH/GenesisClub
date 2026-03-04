package com.example.genesisclub.genesisClub.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CheckController {
    @GetMapping("/publico/test-final")
    public String test() {
        return "EL CODIGO NUEVO SI SE SUBIO";
    }
}