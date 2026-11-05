package src;

import java.util.Scanner;

public class Hospital {

    private int waitingRoomCapacity;
    private int numberOfDoctors;
    private int numberOfPatients;

    private WaitingRoom waitingRoom;
    private TreatmentRoom treatmentRoom;

    private int patientsFinished = 0;

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
        waitingRoom = new WaitingRoom(waitingRoomCapacity);
        treatmentRoom = new TreatmentRoom(2);
    }

    public void createPatients() {
        System.out.println("Patients arriving: ");
        for (int i = 1; i <= numberOfPatients; i++) {
            new Thread(new Patient(i, this)).start();
            try {
                Thread.sleep(100); // stagger arrivals
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

    public WaitingRoom getWaitingRoom() {
        return waitingRoom;
    }

    public TreatmentRoom getTreatmentRoom() {
        return treatmentRoom;
    }

    public synchronized void incrementPatientsFinished() {
        patientsFinished++;
        if (patientsFinished == numberOfPatients) {
            System.out.println("All patients have been treated.");
            System.out.println("Simulation completed.");

            for (int i = 0; i < numberOfDoctors; i++) {
                waitingRoom.patientsAvailable.release();
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

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        createDoctors();
    }
}