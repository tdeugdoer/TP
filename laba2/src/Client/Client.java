package Client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;


public class Client {
    public static void main(String[] args) {
        try(Socket socket = new Socket(InetAddress.getLocalHost(), 3030);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream()))
        {
            System.out.println(socket.getInetAddress());
            System.err.println("Вы подключились к серверу");
            Scanner sc = new Scanner(System.in);

            while (true) {
                System.out.println("Введите массив из 10 цифр: ");
                int[] arr = new int[10];
                for (int i = 0; i < 10; i++) {
                    try {
                        int number = Integer.parseInt(sc.next());
                        arr[i] = number;
                    } catch (NumberFormatException e) {
                        System.out.println("Некорректный ввод. Введите число.");
                        i--;
                    }
                }
                out.writeObject(arr);
                out.flush();

                int[] response = (int[]) in.readObject();
                System.out.println("Изначальный массив: " + Arrays.toString(arr));
                System.out.println("Количество чисел, кратных трём: " + response[0]);
                System.out.println("Минимальное: " + response[1]);
                System.out.println("Максимальное: " + response[2]);

                System.out.println("Желаете продолжить? (введите 'Да' для продолжения)");
                sc.nextLine();
                String s = sc.nextLine();
                if (!Objects.equals(s, "Да")) {
                    break;
                }
            }
            System.err.println("Вы отключились от сервера");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}