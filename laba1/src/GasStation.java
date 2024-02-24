import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class GasStation {
    private static final int SIMULATION_TIME = 240; // Время моделирования в минутах
    private static final int MAINTENANCE_GAP = 45; // Промежуток между обслуживаниями
    private static final int MAINTENANCE_TIME1 = 10; // Время технического обслуживания для 1 колонки
    private static final int MAINTENANCE_TIME2 = 15; // Время технического обслуживания для 2 колонки
    private static final int MAINTENANCE_TIME3 = 5; // Время технического обслуживания для 3 колонки
    private static final int MAINTENANCE_TIME4 = 13; // Время технического обслуживания для 4 колонки
    private static final int AVERAGE_SERVICE_TIME = 6; // Среднее время заправки одной машины

    public static void main(String[] args) {

        // Создаём объекты, которые будут выполняться в потоке
        GasPump gasPump1 = new GasPump(MAINTENANCE_TIME1);
        GasPump gasPump2 = new GasPump(MAINTENANCE_TIME2);
        GasPump gasPump3 = new GasPump(MAINTENANCE_TIME3);
        GasPump gasPump4 = new GasPump(MAINTENANCE_TIME4);

        // Создаём потоки
        Thread thread1 = new Thread(gasPump1);
        Thread thread2 = new Thread(gasPump2);
        Thread thread3 = new Thread(gasPump3);
        Thread thread4 = new Thread(gasPump4);

        // Запускаем потоки
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();

        // Ждём завершения работы потоков
        try {
            thread1.join();
            thread2.join();
            thread3.join();
            thread4.join();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
            return;
        }

        // Получаем из объектов количество обслуженных автомобилей
        int carsCountOfGasPump1 = gasPump1.servicedCarsCount;
        int carsCountOfGasPump2 = gasPump2.servicedCarsCount;
        int carsCountOfGasPump3 = gasPump3.servicedCarsCount;
        int carsCountOfGasPump4 = gasPump4.servicedCarsCount;
        int totalCarsCount = carsCountOfGasPump1 + carsCountOfGasPump2 + carsCountOfGasPump3 + carsCountOfGasPump4;

        System.out.println("Первая колонка: " + carsCountOfGasPump1);
        System.out.println("Вторая колонка: " + carsCountOfGasPump2);
        System.out.println("Третья колонка: " + carsCountOfGasPump3);
        System.out.println("Четвёртая колонка: " + carsCountOfGasPump4);
        System.out.println("Общее количество: " + totalCarsCount);

        writeToFile(carsCountOfGasPump1, carsCountOfGasPump2, carsCountOfGasPump3, carsCountOfGasPump4, totalCarsCount);
    }

    private static void writeToFile(int carsCountOfGasPump1, int carsCountOfGasPump2, int carsCountOfGasPump3, int carsCountOfGasPump4, int totalCarsCount) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("D:/Учёба/Рис/untitled/untitled3/src/file.txt", true))) {
            writer.write("Первая колонка: " + carsCountOfGasPump1 + "\n" + "Вторая колонка: " + carsCountOfGasPump2 + "\n" +
                    "Третья колонка: " + carsCountOfGasPump3 + "\n" + "Четвёртая колонка: " + carsCountOfGasPump4 + "\n" +
                    "Общее количество: " + totalCarsCount + "\n\n");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static class GasPump implements Runnable {
        private final int maintenanceTime; // Время технического обслуживания для колонки
        private int servicedCarsCount; // Количество обслуженных автомобилей

        public GasPump(int maintenanceTime) {
            this.maintenanceTime = maintenanceTime;
        }

        @Override
        public void run() {
            int countFullInterval = SIMULATION_TIME / (MAINTENANCE_GAP + maintenanceTime); // Количество полных интервалов
            int residue = SIMULATION_TIME % (MAINTENANCE_GAP + maintenanceTime); // Время, которое не вошло в полный интервал
            servicedCarsCount = (int) (countFullInterval * ((double) MAINTENANCE_GAP / AVERAGE_SERVICE_TIME) + (double) residue / AVERAGE_SERVICE_TIME);
        }
    }
}