package boojongmin.websocketservletdemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketExample {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    void start(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        clientSocket = serverSocket.accept();
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            if (".".equals(inputLine)) {
                out.println("good bye");
                break;
            }
            System.out.println("> " + inputLine);
            out.println("> " + inputLine);
        }
    }

    public static void main(String[] args) throws IOException {
        new SocketExample().start(8080);
    }
}
