package Server;

import java.net.ServerSocket;
import java.net.Socket;


public class Server {
    public static void main(String[] args) {
        try(ServerSocket serverSocket = new ServerSocket(3030)) {
            System.err.println("Сервер стартовал");

            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new Handler(socket)).start();
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Сервер завершил работу");
        }
    }
}