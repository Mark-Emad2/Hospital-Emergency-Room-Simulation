package src;
//hi
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class Hospital {

    private int waitingRoomCapacity;
    private int numberOfDoctors;
    private int numberOfPatients;

    private Queue<Patient> waitingRoomQueue;

    // Semaphores
    public Semaphore seats;
    public Semaphore patientsAvailable;
    public Semaphore treatmentRooms;
    public Semaphore mutex;

    private int patientsFinished = 0;

    public Hospital() {
        waitingRoomQueue = new LinkedList<>();
    }

    public void readInput() {
        Scanner input = new Scanner(System.in);
        System.out.print("Waiting room capacity: ");
        waitingRoomCapacity = input.nextInt();
        System.out.print("Number of doctors: ");
        numberOfDoctors = input.nextInt();
        System.out.print("Number of patients: ");
        numberOfPatients = input.nextInt();
    }

    public void initializeHospital() {
        seats = new Semaphore(waitingRoomCapacity);
        patientsAvailable = new Semaphore(0);
        treatmentRooms = new Semaphore(2);
        mutex = new Semaphore(1);
    }

    public void createPatients() {
        for (int i = 1; i <= numberOfPatients; i++) {
            new Thread(new Patient(i, this)).start();
        }
    }

    public void createDoctors() {
        for (int i = 1; i <= numberOfDoctors; i++) {
            new Thread(new Doctor(i, this)).start();
        }
    }

    // --- SHARED QUEUE OPERATIONS ---

    public void addPatientToQueue(Patient patient) {
        mutex.acquire(); // Lock the queue
        waitingRoomQueue.add(patient);
        mutex.release(); // Unlock the queue

        // Signal a doctor that a patient is available
        patientsAvailable.release();
    }

    public Patient getNextPatientFromQueue() {
        Patient p = null;
        mutex.acquire(); // Lock the queue
        if (!waitingRoomQueue.isEmpty()) {
            p = waitingRoomQueue.poll(); // Remove the first patient
        }
        mutex.release(); // Unlock the queue
        return p;
    }

    public synchronized void incrementPatientsFinished() {
        patientsFinished++;
    }

    public synchronized int getPatientsFinishedCount() {
        return patientsFinished;
    }

    public int getTotalPatients() {
        return numberOfPatients;
    }

    public void runSimulation() {
        readInput();
        initializeHospital();

        System.out.println("Patients arriving: ");
        createPatients();

        // Small delay to let patients arrive before doctors start
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }

        createDoctors();
    }
}