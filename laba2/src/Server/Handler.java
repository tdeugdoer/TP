package Server;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

class Handler implements Runnable {
    private final Socket socket;

    public Handler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream()); // Поток для отправления данных клиенту
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) // Поток для получения данных от клиента
        {

            String clientName = socket.getInetAddress().toString();
            System.out.println("Подключён " + clientName);

            while (true) {
                int[] numbers = (int[]) in.readObject();

                int[] response = new int[3];
                response[0] = countMultiplesOfThree(numbers);
                response[1] = findMin(numbers);
                response[2] = findMax(numbers);

                System.out.println("Поступивший массив: " + Arrays.toString(numbers));
                System.out.println("Количество чисел, кратных трём: " + response[0]);
                System.out.println("Минимальное: " + response[1]);
                System.out.println("Максимальное: " + response[2]);

                out.writeObject(response);
                out.flush();
                
                writeToFile(clientName, numbers, response);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static int countMultiplesOfThree(int[] arr) { // подсчёт количества чисел в массиве, кратных 3
        int count = 0;
        for(int i = 0; i < arr.length; i++) {
            if(arr[i] % 3 == 0) {
                count++;
            }
        }
        return count;
    }

    private static int findMax(int[] arr) { // поиск максимального элемента в массиве
        int max = arr[0];
        for(int i = 1; i < arr.length; i++) {
            if(max < arr[i]) {
                max = arr[i];
            }
        }
        return max;
    }

    private static int findMin(int[] arr) { // поиск минимального элемента в массиве
        int min = arr[0];
        for(int i = 1; i < arr.length; i++) {
            if(min > arr[i]) {
                min = arr[i];
            }
        }
        return min;
    }

    private static void writeToFile(String client, int[] req, int[] res) { // запись в файл
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("D:/Учёба своё/Marina/TP/laba2/src/file.txt", true))) {
            writer.write("Клиент: " + client + "\n" + "Поступивший массив: " + Arrays.toString(res) + "\n" +
                    "Количество чисел, кратных трём: " + res[0] + "\n" +
                    "Минимальное: " + res[1] + "\n" +
                    "Максимальное: " + res[2] + "\n\n");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}