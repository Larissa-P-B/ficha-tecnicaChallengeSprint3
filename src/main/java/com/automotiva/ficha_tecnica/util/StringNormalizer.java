package com.automotiva.ficha_tecnica.util;



import java.text.Normalizer;

public class StringNormalizer {

    public static String normalize(String input) {

        if (input == null) return null;

        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);

        return normalized
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "") // remove acentos
                .toLowerCase()
                .trim();
    }
}