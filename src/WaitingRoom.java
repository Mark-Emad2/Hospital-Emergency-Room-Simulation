package src;

import java.util.LinkedList;
import java.util.Queue;

public class WaitingRoom {
    private int capacity;
    private Queue<Patient> queue;
    public Semaphore seats;
    public Semaphore patientsAvailable;
    public Semaphore mutex;

    public WaitingRoom(int capacity) {
        this.capacity = capacity;
        this.queue = new LinkedList<>();
        this.seats = new Semaphore(capacity);
        this.patientsAvailable = new Semaphore(0);
        this.mutex = new Semaphore(1);
    }

    public void addPatient(Patient patient) {
        mutex.acquire();
        queue.add(patient);
        mutex.release();
        patientsAvailable.release();
    }

    public Patient assignNextPatient(int doctorID) {
        Patient p = null;
        mutex.acquire();
        if (!queue.isEmpty()) {
            p = queue.poll();
            System.out.println("Doctor " + doctorID + " is assigned to P" + p.getPatientId() + ".");
        }
        mutex.release();
        return p;
    }

    public synchronized void reenterWaitingRoom(Patient patient) {
        if (!seats.tryAcquire(0)) {
            System.out.println("The waiting room is full.");
            System.out.println("P" + patient.getPatientId() + " is waiting outside for an available seat.");
            seats.acquire();
        }
        System.out.println("P" + patient.getPatientId() + " entered the waiting room.");
        addPatient(patient);
    }

    public boolean tryAcquireSeat(long timeoutMillis) {
        return seats.tryAcquire(timeoutMillis);
    }

    public void acquireSeat() {
        seats.acquire();
    }

    public void releaseSeat() {
        seats.release();
    }
}