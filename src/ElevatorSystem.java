import java.util.ArrayList;

public class ElevatorSystem {
    private final ArrayList<Elevator> elevators;

    public ElevatorSystem(ArrayList<Elevator> elevators) {
        this.elevators = elevators;
    }

    // Отправляет запрос в самый оптимальный лифт
    public void requestFloor(int floor) throws IllegalArgumentException {
        Elevator nearestAvailableElevator = findNearestAvailableElevator(floor);
        nearestAvailableElevator.requestFloor(floor);
    }

    // Возвращает ближайший доступный лифт.
    // В случае, если все лифты заняты, берется первый лифт
    private Elevator findNearestAvailableElevator(int floor) {
        Elevator nearestAvailableElevator = elevators.getFirst();
        int minDist = Math.abs(nearestAvailableElevator.getCurrentFloor() - floor);

        for (Elevator elevator : elevators) {
            int curDist = Math.abs(floor - elevator.getCurrentFloor());

            // Берем ближайший лифт, который свободен либо может остановиться на этаже,
            // т.к. он по пути к его основной цели
            if (curDist < minDist && (elevator.isFree() || elevator.floorIsAlongWay(floor))) {
                minDist = curDist;
                nearestAvailableElevator = elevator;
            }
        }
        return nearestAvailableElevator;
    }
}
