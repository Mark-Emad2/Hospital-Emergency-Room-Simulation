package src;

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
        treatmentRooms = new Semaphore(2);   // PDF: exactly 2 treatment rooms
        mutex = new Semaphore(1);
    }

    public void createPatients() {
        System.out.println("Patients arriving: ");
        for (int i = 1; i <= numberOfPatients; i++) {
            new Thread(new Patient(i, this)).start();
            try {
                Thread.sleep(100); // stagger arrivals so P1, P2, ... print in order
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void createDoctors() {
        for (int i = 1; i <= numberOfDoctors; i++) {
            new Thread(new Doctor(i, this)).start();
        }
    }

    // --- SHARED QUEUE OPERATIONS ---

    public Patient assignNextPatient(int doctorID) {
        Patient p = null;
        mutex.acquire();
        if (!waitingRoomQueue.isEmpty()) {
            p = waitingRoomQueue.poll();
            System.out.println("Doctor " + doctorID + " is assigned to P" + p.getPatientId() + ".");
        }
        mutex.release();
        return p;
    }

    public void addPatientToQueue(Patient patient) {
        mutex.acquire();
        waitingRoomQueue.add(patient);
        mutex.release();

        // Signal a doctor that a patient is available
        patientsAvailable.release();
    }

    public void reenterWaitingRoom(Patient patient) {
        // Try to grab a seat without blocking, so we print the "full"
        // message only when it actually applies.
        if (!seats.tryAcquire(0)) {
            System.out.println("The waiting room is full.");
            System.out.println("P" + patient.getPatientId()
                    + " is waiting outside for an available seat.");
            seats.acquire();   // block until a seat frees up
        }
        System.out.println("P" + patient.getPatientId() + " entered the waiting room.");
        addPatientToQueue(patient);
    }

    public synchronized void incrementPatientsFinished() {
        patientsFinished++;
        if (patientsFinished == numberOfPatients) {
            System.out.println("All patients have been treated.");
            System.out.println("Simulation completed.");

            for (int i = 0; i < numberOfDoctors; i++) {
                patientsAvailable.release();
            }
        }
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

        createPatients();

        // Small delay to let patients arrive before doctors start
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        createDoctors();
    }
}