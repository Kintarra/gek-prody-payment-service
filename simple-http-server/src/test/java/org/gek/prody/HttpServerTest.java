package org.gek.prody;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpServerTest {

    @Test
    void testParseValidRequest1() {
        String request = "baeldung.html";
        String[] result = request.split("\\.");
        assertEquals("html", result[1]);
    }

    @Test
    void testParseValidRequest() {
        String request = "GET /baeldung.html HTTP/1.1";
        String[] result = parseHttpRequest(request);
        assertArrayEquals(
            new String[] {"GET", "baeldung.html", "HTTP/1.1"},
            result,
            "Корректный разбор валидного HTTP-запроса"
        );
    }

    public static String[] parseHttpRequest(String request) {
        String[] parts = request.trim().split("\\s+");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid HTTP request format");
        }
        // Удаляем '/' из названия файла
        String fileName = parts[1].startsWith("/") ? parts[1].substring(1) : parts[1];
        return new String[] {
            parts[0],
            fileName,
            parts[2]
        };
    }

}