import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import static java.lang.Thread.sleep;

public class GasStation {
    private static final int SIMULATION_TIME = 240; // Время моделирования в минутах
    private static final int MAINTENANCE_GAP = 45; // Промежуток между обслуживаниями
    private static final int MAINTENANCE_TIME1 = 10; // Время технического обслуживания для 1 колонки
    private static final int MAINTENANCE_TIME2 = 15; // Время технического обслуживания для 2 колонки
    private static final int MAINTENANCE_TIME3 = 5; // Время технического обслуживания для 3 колонки
    private static final int MAINTENANCE_TIME4 = 13; // Время технического обслуживания для 4 колонки
    private static final int SERVICE_TIME = 6; // Среднее время заправки одной машины
    private static int time; // Текущее время

    public static void main(String[] args) {
        
        // Создаём объекты, которые будут выполняться в потоке
        GasPump gasPump1 = new GasPump(1, MAINTENANCE_TIME1);
        GasPump gasPump2 = new GasPump(2, MAINTENANCE_TIME2);
        GasPump gasPump3 = new GasPump(3, MAINTENANCE_TIME3);
        GasPump gasPump4 = new GasPump(4, MAINTENANCE_TIME4);

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

        for (time = 1; time < SIMULATION_TIME + 1; time++){
            try {
                System.out.println("\nВремя: " + time + "\n");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        gasPump1.stop();
        gasPump2.stop();
        gasPump3.stop();
        gasPump4.stop();

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
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("D:/Учёба своё/Marina/TP/laba1/src/file.txt", true))) {
            writer.write("Первая колонка: " + carsCountOfGasPump1 + "\n" + "Вторая колонка: " + carsCountOfGasPump2 + "\n" +
                    "Третья колонка: " + carsCountOfGasPump3 + "\n" + "Четвёртая колонка: " + carsCountOfGasPump4 + "\n" +
                    "Общее количество: " + totalCarsCount + "\n\n");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static class GasPump implements Runnable {
        private final int number; // Номер колонки
        private final int maintenanceTime; // Время обслуживания
        private int servicedCarsCount; // Количество обслуженных автомобилей
        private boolean mutex = true; // Служит для завершения потока

        public GasPump(int number, int maintenanceTime) {
            this.number = number;
            this.maintenanceTime = maintenanceTime;
        }

        @Override
        public void run() {
            while (mutex) {
                try {
                    int intervalTime = time % (MAINTENANCE_GAP + maintenanceTime); // Получаем сколько времени из промежутка между обслуживаниями прошло
                    if (intervalTime == 45) { // Если текущее время кратно промежутку между обслуживаниями, т.е. настало время обслуживания
                        makeMaintenance();
                    } else if (MAINTENANCE_GAP - intervalTime < SERVICE_TIME) { // Если до обслуживания осталось меньше 6 минут
                        System.out.println("Колонка №" + number + " начала заправлять автомобиль " + (servicedCarsCount + 1));
                        int remainingTimeBeforeMaintenance = MAINTENANCE_GAP - intervalTime; // Сколько времени мы будем заправлять машину до обслуживания
                        Thread.sleep((remainingTimeBeforeMaintenance) * 1000L);
                        makeMaintenance();
                        Thread.sleep((SERVICE_TIME - remainingTimeBeforeMaintenance) * 1000L); // Ждём оставшееся время для заправки машины, которая приехала перед обслуживанием и не успела заправиться полностью
                        servicedCarsCount++;
                    } else { // Если просто заправляем машину
                        System.out.println("Колонка №" + number + " начала заправлять автомобиль " + (servicedCarsCount + 1));
                        Thread.sleep(SERVICE_TIME * 1000); // Задержка для имитации времени заправки
                        servicedCarsCount++;
                    }
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());;
                }
            }
        }

        private void makeMaintenance() throws InterruptedException { // Обслуживание колонки
            System.out.println("Колонка №" + number + " закрывается на обслуживание на " + maintenanceTime + " мин");
            Thread.sleep(maintenanceTime * 1000L); // Задержка для имитации времени заправки
        }

        public void stop() {
            mutex = false;
        }
    }
}