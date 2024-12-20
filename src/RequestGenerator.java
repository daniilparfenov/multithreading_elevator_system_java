import java.util.Random;

public class RequestGenerator implements Runnable {
    private final ElevatorSystem elevatorSystem; // Система управления лифтами, через которую будем отправлять запросы
    private final int maxFloors;                 // Кол-во этажей п дому

    public RequestGenerator(ElevatorSystem elevatorSystem, int maxFloors) {
        this.elevatorSystem = elevatorSystem;
        this.maxFloors = maxFloors;
    }

    // Запускает бесконечную генерацию запросов для лифтов и отправляет их
    @Override
    public void run() {
        Random random = new Random();
        while (true) {
            try {
                int requestedFloor = random.nextInt(maxFloors) + 1; // Генерация этажа от 1 до maxFloors
                System.out.println("[Request Generator] New requested floor - " + requestedFloor);

                elevatorSystem.requestFloor(requestedFloor); // Отправляем запрос

                Thread.sleep(random.nextInt(2001) + 2000); // Один запрос каждые 2-4 секунды
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}