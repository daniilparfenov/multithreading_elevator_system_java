import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        final int ELEVATOR_NUM = 2; // Кол-во лифтов в доме
        final int FLOOR_NUM = 15; // Кол-во этажей в доме

        // Массивы лифтов и потоков для них
        ArrayList<Elevator> elevators = new ArrayList<>();
        ArrayList<Thread> elevatorThreads = new ArrayList<>();
        for (int i = 0; i < ELEVATOR_NUM; i++) {
            elevators.add(new Elevator(i, FLOOR_NUM));
            elevatorThreads.add(new Thread(elevators.get(i)));
        }

        // Создание системы управления лифтами и потока для генератора запросов
        ElevatorSystem elevatorSystem = new ElevatorSystem(elevators);
        Thread requestGenerator = new Thread(new RequestGenerator(elevatorSystem, FLOOR_NUM));

        // Запуск потоков
        for (Thread elevatorThread : elevatorThreads) {
            elevatorThread.start();
        }
        requestGenerator.start();
    }
}