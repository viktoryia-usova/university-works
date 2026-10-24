package org.example.Utility;


import org.example.Entities.User;



import java.io.*;
import java.net.Socket;

public class ClientSocket {
    private static final ClientSocket single_instance = new ClientSocket();
    private static final String SERVER_ADDRESS = "localhost"; // Адрес сервера
    private static final int SERVER_PORT = 12355; // Порт сервера
    private Socket socket;
    private static PrintWriter writer;
    private static BufferedReader reader;
    private User user;

    public ClientSocket() {
        try {
            this.socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Соединение с сервером установлено.");
        } catch (IOException e) {
            System.err.println("Ошибка при подключении к серверу: " + e.getMessage());
        }
    }

    public static ClientSocket getInstance() {
        return single_instance;
    }


    public Socket getSocket() {
        return socket;
    }

    public PrintWriter getWriter() {
        return writer;
    }

    public static BufferedReader getReader() {
        return reader;
    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }



}
