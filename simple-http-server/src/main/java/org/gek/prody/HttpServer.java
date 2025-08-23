package org.gek.prody;//не рекомендуется добавлять классы без конкретных пакетов, описывающий предметную область

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// https://www.baeldung.com/java-serversocket-simple-http-server
// https://iprody.eduonline.io/learn/wq6_FLZLnkyTWsDcRoY99g/theory

public class HttpServer {
    public static final String GET_METHOD = "GET";
    public static final String STATIC_DIRECTORY_PATH = "simple-http-server/src/main/java/org/gek/prody/static";

    // HTTP - 7ой уровень модели OSI (Open Systems Interconnection model)
    // Протокол HTTP использует:
    // 1) IP (Internet Protocol) - межсетевой протокол, отвечает за IP адресацию устройств в сети
    // 2) TCP (Transmission Control Protocol) - потоковая передача бинарных данных (сообщений) между устройствами в сети
    // 2*) UDP (User Datagram Protocol) - в отличие от TCP обеспечивает передачу данных без получения подтверждения от пользователя (Звонки в Skype/Google Meet)

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("Server started at http://localhost:8080");

        Socket clientSocket = serverSocket.accept();
        System.out.println("New client socket");

        // Получаем список всех файлов
        List<String> fileNames = getFilesFromDirectory(STATIC_DIRECTORY_PATH);

        while (true) {
            try {
                BufferedReader in = new BufferedReader(new
                        InputStreamReader(clientSocket.getInputStream(),//поток для чтения данных
                        StandardCharsets.UTF_8));
                BufferedWriter out = new BufferedWriter(new
                        OutputStreamWriter(clientSocket.getOutputStream(),//поток для отправки данных клиенту
                        StandardCharsets.UTF_8));
                String response = "";
                StringBuilder responseBuilder = new StringBuilder();
                // Чтение запроса
                String request;
                while ((request = in.readLine()) != null && !request.isEmpty()) {
                    System.out.println(request);
                    String[] parsedRequest = parseHttpRequest(request);
                    if (parsedRequest.length > 0) {
                        if (Objects.equals(parsedRequest[0], GET_METHOD)) {
                            System.out.println("Получен GET запрос");
                            if (fileNames.contains(parsedRequest[1].toLowerCase())) {
                                System.out.println("Файл найден в дирректории");
                                try {
                                    byte[] fileContent = Files.readAllBytes(Paths.get(STATIC_DIRECTORY_PATH + "/" + parsedRequest[1]));
                                    BufferedInputStream bis = new BufferedInputStream(new java.io.ByteArrayInputStream(fileContent));
                                    int data;
                                    while ((data = bis.read()) != -1) {
                                        responseBuilder.append((char) data);
                                    }
                                    response = responseBuilder.toString();
                                    System.out.println("response =");
                                    System.out.println(response);
                                    out.write("HTTP/1.1 200 OK\r\n");
                                    String[] fileName = request.split("\\.");
                                    out.write("Content-Type: " + fileName[1] + "; charset=UTF-8\r\n");// какого типа содержимое мы отправляем (по умолчанию читает текстом)
                                    out.write("Content-Length: " + response.length() + "\r\n");
                                    out.write("\r\n");
                                } catch (IOException e) {
                                    System.err.println("Ошибка при чтении файла: " + e.getMessage());
                                    out.write("HTTP/1.1 404 Not Found\r\n");
                                    out.write("\r\n");
                                }
                            } else {
                                System.err.println("Файл с названием " + parsedRequest[1].substring(1) + " не найден");
                                out.write("HTTP/1.1 404 File not found\r\n");
                                out.write("\r\n");
                            }
                        }
                    } else {
                        System.out.println("Ошибка. Неверный формат HTTP-запроса: Это не GET запрос!");
                        response = Files.readString(Paths.get("static/wrong_http_query.html"));
                        out.write("HTTP/1.1 200 OK\r\n");
                        out.write("Content-Type: text/html; charset=UTF-8\r\n");// какого типа содержимое мы отправляем (по умолчанию читает текстом)
                        out.write("Content-Length: " + response.length() + "\r\n");
                        out.write("\r\n");
                    }
                    out.write(response);
                    out.flush();// метод PrintWriter - является буферизованным, поэтому обязательно делаем flush
                }
            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.out.println("IOException finally: " + e.getMessage());
                }
            }
        }
    }

    public static String[] parseHttpRequest(String request) {
        String[] parts = request.trim().split("\\s+");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid HTTP request format");
        }
        return new String[] {
            parts[0],
            parts[1].startsWith("/") ? parts[1].substring(1) : parts[1],
            parts[2]
        };
    }

    public static List<String> getFilesFromDirectory(String directoryPath) {
        List<String> fileList = new ArrayList<>();
        File directory = new File(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                System.out.println("Files in directory static:");
                for (File file : files) {
                    if (file.isFile()) { // Добавляем только файлы
                        fileList.add(file.getName());
                        System.out.println(file.getName());
                    }
                }
            }
        } else {
            System.err.println("Указанная директория не существует или не является директорией: " + directoryPath);
        }
        return fileList;
    }

}