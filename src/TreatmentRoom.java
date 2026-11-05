package src;

public class TreatmentRoom {
    public Semaphore rooms;

    public TreatmentRoom(int count) {
        this.rooms = new Semaphore(count);
    }

    public boolean tryAcquire(long timeoutMillis) {
        return rooms.tryAcquire(timeoutMillis);
    }

    public void release() {
        rooms.release();
    }
}