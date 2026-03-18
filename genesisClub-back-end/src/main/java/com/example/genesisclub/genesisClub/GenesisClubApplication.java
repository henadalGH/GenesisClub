package com.example.genesisclub.genesisClub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GenesisClubApplication {

	private static final Logger log = LoggerFactory.getLogger(GenesisClubApplication.class);

	@jakarta.annotation.PostConstruct
	public void init() {
	    log.info("========================================================");
	    log.info("Backend iniciado correctamente - Sistema en línea");
	    log.info("========================================================");
	}

	public static void main(String[] args) {
		SpringApplication.run(GenesisClubApplication.class, args); 
	}

}
