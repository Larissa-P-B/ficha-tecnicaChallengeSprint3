package com.automotiva.ficha_tecnica.temp;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GerarSenhaHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String senhaHash = encoder.encode("user");
        System.out.println(senhaHash);
    }
}
