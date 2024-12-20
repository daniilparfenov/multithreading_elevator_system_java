import java.util.LinkedList;

public class Elevator implements Runnable {
    private final int id;
    private final LinkedList<Integer> requestedFloors;
    private final int maxFloors;
    private boolean isMovingUp;
    private int currentFloor;
    private final Object lock;
    private final int oneFloorMovingTimeMS;

    // Конструктор
    public Elevator(int id, int maxFloors) {
        this.id = id;
        this.maxFloors = maxFloors;
        this.oneFloorMovingTimeMS = 100;
        this.requestedFloors = new LinkedList<>();
        this.isMovingUp = true;
        this.currentFloor = maxFloors / 2; /* Изначально помещаем лифт в середину дома, чтобы после перемещения
                                              первого лифта, остальные лифты не простаивали внизу,
                                              т.к. зачастую они оказываются дальше первого лифт */
        this.lock = new Object();
    }

    // Возвращает этаж, где находится лифт
    public int getCurrentFloor() {
        return currentFloor;
    }

    // Возвращает идентификатор лифта
    public int getId() {
        return id;
    }

    // Возвращает, свободен ли лифт, т.е. нет ли запросов
    public boolean isFree() {
        synchronized (lock) {
            return requestedFloors.isEmpty();
        }
    }

    // Добавляет этаж в список запрошенных
    public synchronized void requestFloor(int floor) throws IllegalArgumentException {
        if (floor < 1 || floor > maxFloors) {
            throw new IllegalArgumentException("Illegal floor. Floor must be between 1 and " + maxFloors);
        }

        synchronized (lock) {
            if (floor == currentFloor) {
                System.out.println("[Elevator " + id + "] is already on floor " + floor);
                return;
            }

            // Добавляем только если запроса еще нет в списке
            if (!requestedFloors.contains(floor)) {
                System.out.println("[Elevator " + id + "] Floor " + floor + " added to requested floors");
                requestedFloors.add(floor);
            }
        }
    }

    // Основная функция лифта-потока
    @Override
    public void run() {
        try {
            while (true) {
                // Проверяем запрошенные этажи и едем на них
                while (!requestedFloors.isEmpty()) {
                    int targetFloor = -1;
                    synchronized (lock) {
                        targetFloor = requestedFloors.pop();
                    }
                    moveToFloor(targetFloor);
                }
                Thread.sleep(100); // Задержка для накопления этажей-запросов
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }

    // Проверяет, "по пути" лифта находится этаж
    public boolean floorIsAlongWay(int floor) throws IllegalArgumentException {
        if (floor < 1 || floor > maxFloors) {
            throw new IllegalArgumentException("Illegal floor. Floor must be between 1 and " + maxFloors);
        }
        if (floor <= currentFloor && !isMovingUp) {
            return true;
        }
        return floor >= currentFloor && isMovingUp;
    }

    // Постепенно, этаж за этажом, перемещает лифт. Останавливается и на других запрошенных этажах, если они по пути
    private void moveToFloor(int targetFloor) {
        if (targetFloor == currentFloor) {
            System.out.println("[Elevator " + id + "] is already on floor " + targetFloor);
            return;
        }

        System.out.println("[Elevator " + id + "] is moving from " + currentFloor + " to " + targetFloor + " floor");
        try {
            while (currentFloor != targetFloor) {
                if (currentFloor < targetFloor) {
                    isMovingUp = true;
                    currentFloor++;
                } else {
                    currentFloor--;
                    isMovingUp = false;
                }
                System.out.println("[Elevator " + id + "] is on floor " + currentFloor + " to eventually reach the target floor " + targetFloor);
                if (requestedFloors.contains(currentFloor)) {
                    System.out.println("[Elevator " + id + "] takes passengers on the floor " + currentFloor + " because they were along the way");
                    requestedFloors.remove(Integer.valueOf(currentFloor));
                }
                Thread.sleep(oneFloorMovingTimeMS);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        currentFloor = targetFloor;
        System.out.println("[Elevator " + id + "] is arrived to floor " + targetFloor);
    }
}