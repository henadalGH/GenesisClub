package com.example.genesisclub.genesisClub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GenesisClubApplication {

	@jakarta.annotation.PostConstruct
public void init() {
    System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    System.out.println("EL BACKEND ESTA ABIERTO - SI VES ESTO, EL CODIGO SUBIO BIEN");
    System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
}

	public static void main(String[] args) {
		SpringApplication.run(GenesisClubApplication.class, args);
	}

}
