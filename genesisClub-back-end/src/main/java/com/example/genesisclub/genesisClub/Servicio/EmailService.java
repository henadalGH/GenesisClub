package com.example.genesisclub.genesisClub.Servicio;


public interface EmailService {

    void enviarEmailSimple(String destino, String asunto, String mensaje);

    void enviarEmailHtml(String destino, String asunto, String contenidoHtml) throws Exception;
}